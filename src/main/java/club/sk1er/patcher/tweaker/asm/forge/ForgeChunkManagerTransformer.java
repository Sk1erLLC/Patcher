package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ForgeChunkManagerTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraftforge.common.ForgeChunkManager"};
  }

  /**
   * Perform any asm in order to transform code
   *
   * @param classNode the transformed class node
   * @param name      the transformed class name
   */
  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode methodNode : classNode.methods) {
      String methodName = mapMethodName(classNode, methodNode);

      if (methodName.equals("<clinit>")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof FieldInsnNode && ((FieldInsnNode) next).name.equals("forcedChunks")) {
            for (int i = 0; i < 5; ++i) {
              methodNode.instructions.remove(next.getPrevious());
            }

            methodNode.instructions.insertBefore(next, assignForcedChunks());
          }
        }
      }
    }
  }

  private InsnList assignForcedChunks() {
    InsnList list = new InsnList();
    list.add(new TypeInsnNode(Opcodes.NEW, "java/util/WeakHashMap"));
    list.add(new InsnNode(Opcodes.DUP));
    list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/WeakHashMap", "<init>", "()V", false));
    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "synchronizedMap", "(Ljava/util/Map;)Ljava/util/Map;", false));
    return list;
  }
}
