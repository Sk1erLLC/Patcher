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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RendererLivingEntityTransformer implements PatcherTransformer {

  @Override
  public String[] getClassName() {
    return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
  }

  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode method : classNode.methods) {
      String methodName = mapMethodName(classNode, method);
      if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                /*
                Find if (shouldSit && entity.ridingEntity instanceof EntityLivingBase)
                    go back to find vars # of f2, f1, f

                    go forward until we find the label we jump to if that statement if false, retract 1 insn, then read f2 = f1 -f
                 */
        int f = 0;
        int f1 = 0;
        int f2 = 0;

        int i = 0;
        while (iterator.hasNext()) {
          AbstractInsnNode next = iterator.next();
          if (next instanceof TypeInsnNode) {
            if (next.getOpcode() == Opcodes.INSTANCEOF && ((TypeInsnNode) next).desc
                .equals("net/minecraft/entity/EntityLivingBase")) {

              //Find values of f2,f1,f
              while ((next = next.getPrevious()) != null) {
                if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.FSTORE) {
                  if (i == 0) {
                    f2 = ((VarInsnNode) next).var;
                  } else if (i == 1) {
                    f1 = ((VarInsnNode) next).var;
                  } else {
                    f = ((VarInsnNode) next).var;
                  }
                  i++;
                  if (i == 3) {
                    break;
                  }
                }
              }
              if (next == null) {
                return;
              }

              LabelNode node = null; //Find label
              while ((next = next.getNext()) != null) {
                if (next instanceof JumpInsnNode && next.getOpcode() == Opcodes.IFEQ) {
                  node = ((JumpInsnNode) next).label;
                  break;
                }
              }
              if (next == null) {
                return;
              }

              LabelNode labelNode = new LabelNode(); //Override final if statement to jump to our new end of block
              while ((next = next.getNext()) != null) {
                if (next instanceof JumpInsnNode && ((JumpInsnNode) next).label.equals(node)
                    && next.getOpcode() == Opcodes.IFLE) {
                  ((JumpInsnNode) next).label = labelNode;
                  break;
                }
              }
              if (next == null) {
                return;
              }

              while ((next = next.getNext()) != null) {
                if (next.equals(node)) {
                  InsnList insnList = new InsnList();
                  insnList.add(labelNode);
                  insnList.add(
                      new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "headRotation",
                          "Z"));
                  LabelNode ifeq = new LabelNode();
                  insnList.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
                  insnList.add(new VarInsnNode(Opcodes.FLOAD, f1));
                  insnList.add(new VarInsnNode(Opcodes.FLOAD, f));
                  insnList.add(new InsnNode(Opcodes.FSUB));
                  insnList.add(new VarInsnNode(Opcodes.FSTORE, f2));
                  insnList.add(ifeq);
                  method.instructions.insertBefore(next, insnList);
                }
              }
            }
          }
        }
      }

      if (methodName.equals("renderName") || methodName.equals("func_177067_a")) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

        while (iterator.hasNext()) {
          AbstractInsnNode node = iterator.next();

          if (node.getOpcode() == Opcodes.GETFIELD) {
            String fieldName = mapFieldNameFromNode((FieldInsnNode) node);
            if (fieldName.equals("playerViewX") || fieldName.equals("field_78732_j")) {
              method.instructions.insert(node, timesByModifier());
            }
          }
        }

        makeNametagTransparent(method);
      }
    }
  }

  private InsnList timesByModifier() {
    InsnList list = new InsnList();
    list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        "club/sk1er/patcher/tweaker/asm/optifine/OptifineRenderTransformer", "checkPerspective",
        "()F", false));
    list.add(new InsnNode(Opcodes.FMUL));
    return list;
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
        }
      }
    }
  }
}
