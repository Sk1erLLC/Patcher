package club.sk1er.patcher.tweaker.asm.pingtag;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TagRendererListenerTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"me.powns.pingtag.rendering.TagRenderListener"};
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
      String methodName = methodNode.name;

      if (methodName.equals("render")) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();

          if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.DSTORE) {
            methodNode.instructions.insertBefore(next.getNext(), changeHeight());
            break;
          }
        }

        break;
      }
    }
  }

  private InsnList changeHeight() {
    InsnList list = new InsnList();
    list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "showOwnNametag", "Z"));
    LabelNode ifeq = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
    list.add(new LdcInsnNode(0.3D));
    list.add(new VarInsnNode(Opcodes.DSTORE, 3));
    list.add(ifeq);
    return list;
  }
}
