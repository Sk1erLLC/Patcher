package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class NBTTagCompoundTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.nbt.NBTTagCompound"};
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
            Opcodes.ACC_PUBLIC, "profile", "Lcom/mojang/authlib/GameProfile;", null, null));
    classNode.fields.add(
        new FieldNode(
            Opcodes.ACC_PUBLIC, "compound", "Lnet/minecraft/nbt/NBTTagCompound;", null, null));
  }
}
