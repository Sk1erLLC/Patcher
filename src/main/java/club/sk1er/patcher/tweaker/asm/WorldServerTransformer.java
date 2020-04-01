package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class WorldServerTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraft.world.WorldServer"};
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

      if (methodName.equals("tick")) {
        LabelNode gotoInsn = new LabelNode();
        methodNode.instructions.insert(methodNode.instructions.toArray()[69], new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        methodNode.instructions.insert(methodNode.instructions.toArray()[109], gotoInsn);
      }
    }
  }
}
