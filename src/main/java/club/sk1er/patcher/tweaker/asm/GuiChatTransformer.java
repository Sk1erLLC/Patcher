package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiChatTransformer implements PatcherTransformer {

  public static int maxChatLength = 100;

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.gui.GuiChat"};
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

      if (methodName.equals("initGui") || methodName.equals("func_73866_w_")) {
        C01PacketChatMessageTransformer.extendChatLength(methodNode);
      } else if (methodName.equals("drawScreen") || methodName.equals("func_73863_a")) {
        LabelNode ifne = new LabelNode();
        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getOption(ifne));

        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof MethodInsnNode) {
            if (next.getOpcode() == Opcodes.INVOKESTATIC) {
              String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

              if (methodInsnName.equals("drawRect") || methodInsnName.equals("func_73734_a")) {
                methodNode.instructions.insertBefore(next.getNext(), ifne);
              }
            }
          }
        }
      }
    }
  }

  private InsnList getOption(LabelNode ifne) {
    InsnList list = new InsnList();
    list.add(
        new FieldInsnNode(
            Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentChatInputField", "Z"));
    list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
    return list;
  }
}
