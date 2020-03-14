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

public class EnchantmentTransformer implements PatcherTransformer {
  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.enchantment.Enchantment"};
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

      if (methodName.equals("getTranslatedName")) {
        methodNode.instructions.insertBefore(
            methodNode.instructions.getFirst(), getNumericalName());
      }
    }
  }

  private InsnList getNumericalName() {
    InsnList list = new InsnList();
    list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "romanNumerals", "Z"));
    LabelNode labelNode = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(new VarInsnNode(Opcodes.ILOAD, 1));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/EnchantmentHook",
            "getNumericalName",
            "(Lnet/minecraft/enchantment/Enchantment;I)Ljava/lang/String;",
            false));
    list.add(new InsnNode(Opcodes.ARETURN));
    list.add(labelNode);
    return list;
  }
}
