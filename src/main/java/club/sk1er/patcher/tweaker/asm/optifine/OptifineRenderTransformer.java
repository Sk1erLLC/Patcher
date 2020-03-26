package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class OptifineRenderTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraft.client.renderer.entity.Render"};
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

      if (methodName.equals("renderLivingLabel") || methodName.equals("func_147906_a")) {
        makeNametagTransparent(methodNode);
        break;
      }
    }
  }

  public void makeNametagTransparent(MethodNode methodNode) {
    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
    LabelNode afterDraw = new LabelNode();
    while (iterator.hasNext()) {
      AbstractInsnNode node = iterator.next();
      if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
        String nodeName = mapMethodNameFromNode((MethodInsnNode) node);
        if (nodeName.equals("begin") || nodeName.equals("func_181668_a")) {
          AbstractInsnNode prevNode = node.getPrevious().getPrevious().getPrevious();
          methodNode.instructions.insertBefore(prevNode,
              new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentNameTags",
                  "Z"));
          methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, afterDraw));
        } else if (nodeName.equals("draw") || nodeName.equals("func_78381_a")) {
          methodNode.instructions.insert(node, afterDraw);
          break;
        }
      }
    }
  }
}
