package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class BlockDoublePlantTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.block.BlockDoublePlant"};
  }

  /**
   * Perform any asm in order to transform code
   *
   * @param classNode the transformed class node
   * @param name the transformed class name
   */
  @Override
  public void transform(ClassNode classNode, String name) {
    classNode.fields.add(
        new FieldNode(
            Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
            "variantMap",
            "Ljava/util/HashMap;",
            null,
            null));

    for (MethodNode methodNode : classNode.methods) {
      String methodName = mapMethodName(classNode, methodNode);

      if (methodName.equals("<clinit>")) {
        methodNode.instructions.insertBefore(
            methodNode.instructions.getLast().getPrevious(), createVariantMap());
      }
    }
  }

  private InsnList createVariantMap() {
    InsnList list = new InsnList();
    list.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashMap"));
    list.add(new InsnNode(Opcodes.DUP));
    list.add(
        new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false));
    list.add(
        new FieldInsnNode(
            Opcodes.PUTSTATIC,
            "net/minecraft/block/BlockDoublePlant",
            "variantMap",
            "Ljava/util/HashMap;"));
    return list;
  }
}
