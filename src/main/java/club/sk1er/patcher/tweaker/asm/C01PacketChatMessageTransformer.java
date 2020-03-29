package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class C01PacketChatMessageTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraft.network.play.client.C01PacketChatMessage"};
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
      if ((methodNode.name.equals("<init>") && methodNode.desc.equals("(Ljava/lang/String;)V")
          || methodName.equals("readPacketData") || methodName.equals("func_148837_a"))) {
        extendChatLength(methodNode);
      }
    }
  }

  public static void extendChatLength(MethodNode methodNode) {
    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
    while (iterator.hasNext()) {
      AbstractInsnNode node = iterator.next();
      if (node.getOpcode() == Opcodes.BIPUSH && ((IntInsnNode) node).operand == 100) {
        methodNode.instructions.insertBefore(node, new FieldInsnNode(Opcodes.GETSTATIC,
            "club/sk1er/patcher/tweaker/asm/GuiChatTransformer", "maxChatLength", "I"));
        methodNode.instructions.remove(node);
      }
    }
  }
}
