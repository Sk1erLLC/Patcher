package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ScreenShotHelperTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraft.util.ScreenShotHelper"};
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
      String methodDesc = mapMethodDesc(methodNode);
      if ((methodName.equals("saveScreenshot") || methodName.equals("func_148259_a")) && methodDesc
          .equals(
              "(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("png")) {
            methodNode.instructions.insertBefore(next.getPrevious(), setFilePath());
            break;
          }
        }
      }
    }
  }

  private InsnList setFilePath() {
    InsnList list = new InsnList();
    list.add(new VarInsnNode(Opcodes.ALOAD, 8));
    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "getCanonicalFile",
        "()Ljava/io/File;", false));
    list.add(new VarInsnNode(Opcodes.ASTORE, 8));
    return list;
  }
}
