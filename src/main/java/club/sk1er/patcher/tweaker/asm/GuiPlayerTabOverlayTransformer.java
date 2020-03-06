package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiPlayerTabOverlayTransformer implements PatcherTransformer {
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.gui.GuiPlayerTabOverlay"};
  }

  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode method : classNode.methods) {
      String methodName = mapMethodName(classNode, method);
      if (methodName.equals("renderPlayerlist") || methodName.equals("func_175249_a")) {
        method.instructions.insertBefore(
            method.instructions.getFirst(),
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                "moveTabDownPushMatrix",
                "()V",
                false));
        method.instructions.insertBefore(
            method.instructions.getLast().getPrevious(),
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                "moveTabDownPopMatrix",
                "()V",
                false));
        break;
      }
    }
  }
}
