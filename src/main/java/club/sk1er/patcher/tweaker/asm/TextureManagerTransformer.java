package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TextureManagerTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.renderer.texture.TextureManager"};
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

      if (methodName.equals("deleteTexture") || methodName.equals("func_147645_c")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof JumpInsnNode && next.getOpcode() == Opcodes.IFNULL) {
            methodNode.instructions.insertBefore(next.getNext(), removeFromObjects());
          }
        }
      }
    }
  }

  private InsnList removeFromObjects() {
    InsnList list = new InsnList();
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/texture/TextureManager",
            "field_110585_a", // mapTextureObjects
            "Ljava/util/Map;"));
    list.add(new VarInsnNode(Opcodes.ALOAD, 1));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKEINTERFACE,
            "java/util/Map",
            "remove",
            "(Ljava/lang/Object;)Ljava/lang/Object;",
            true));
    list.add(new InsnNode(Opcodes.POP));
    return list;
  }
}
