package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiLanguageListTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.gui.GuiLanguage$List"};
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

      if (methodName.equals("elementClicked") || methodName.equals("func_148144_a")) {
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
          AbstractInsnNode node = iterator.next();
          if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
            String methodInsnName = mapMethodNameFromNode(methodInsnNode);
            if (methodInsnName.equals("refreshResources")
                || methodInsnName.equals("func_110436_a")) {
              methodNode.instructions.remove(node.getPrevious());
              methodNode.instructions.remove(node.getPrevious());
              methodNode.instructions.insertBefore(
                  node,
                  new MethodInsnNode(
                      Opcodes.INVOKESTATIC,
                      "club/sk1er/patcher/tweaker/asm/GuiLanguageListTransformer",
                      "reload",
                      "()V",
                      false));
              methodNode.instructions.remove(node);
              break;
            }
          }
        }
        break;
      }
    }
  }

  @SuppressWarnings("unused")
  public static void reload() {
    Minecraft.getMinecraft()
        .getLanguageManager()
        .onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
  }
}
