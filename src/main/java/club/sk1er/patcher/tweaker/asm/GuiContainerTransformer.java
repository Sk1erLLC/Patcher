package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiContainerTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.gui.inventory.GuiContainer"};
  }

  /**
   * Perform any asm in order to transform code
   *
   * @param classNode the transformed class node
   * @param name the transformed class name
   */
  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode method : classNode.methods) {
      String methodName = mapMethodName(classNode, method);

      if (methodName.equals("mouseClicked") || methodName.equals("func_73864_a")) {
        method.instructions.insertBefore(
            method.instructions.getLast().getPrevious(), checkHotbarKeys());
        break;
      }
    }
  }

  private InsnList checkHotbarKeys() {
    InsnList list = new InsnList();
    list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "mouseBindFix", "Z"));
    LabelNode ifeq = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(new VarInsnNode(Opcodes.ILOAD, 3));

    list.add(new VarInsnNode(Opcodes.BIPUSH, 100));
    list.add(new InsnNode(Opcodes.ISUB));

    list.add(
        new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/gui/inventory/GuiContainer",
            "func_146983_a",
            "(I)Z",
            false));
    list.add(new InsnNode(Opcodes.POP));
    list.add(ifeq);
    list.add(new InsnNode(Opcodes.RETURN));
    return list;
  }
}
