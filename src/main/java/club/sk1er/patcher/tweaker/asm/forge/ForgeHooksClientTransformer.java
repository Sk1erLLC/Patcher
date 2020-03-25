package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ForgeHooksClientTransformer implements PatcherTransformer {
  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraftforge.client.ForgeHooksClient"};
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
      if (methodNode.name.equals("getSkyBlendColour")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();
          if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) next;
            String methodInsnName = mapMethodNameFromNode(methodInsnNode);
            if (methodInsnName.equals("getY") || methodInsnName.equals("func_177956_o")) {
              methodInsnNode.name = "func_177952_p"; // getZ
              break;
            }
          }
        }
      }
    }
  }
}
