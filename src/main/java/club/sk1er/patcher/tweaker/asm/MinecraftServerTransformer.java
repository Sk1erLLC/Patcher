package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class MinecraftServerTransformer implements PatcherTransformer {
  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.server.MinecraftServer"};
  }

  /**
   * Perform any asm in order to transform code
   *
   * @param classNode the transformed class node
   * @param name the transformed class name
   */
  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode methodNode : classNode.methods) {
      String methodName = mapMethodName(classNode, methodNode);

      if (methodName.equals("addFaviconToStatusResponse") || methodName.equals("func_147138_a")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("setFavicon")) {
            methodNode.instructions.insertBefore(next.getNext(), releaseIcon());
            break;
          }
        }
      }
    }
  }

  private InsnList releaseIcon() {
    InsnList list = new InsnList();
    list.add(new VarInsnNode(Opcodes.ALOAD, 5));
    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/netty/buffer/ByteBuf", "release", "()Z", false));
    list.add(new InsnNode(Opcodes.POP));
    return list;
  }
}
