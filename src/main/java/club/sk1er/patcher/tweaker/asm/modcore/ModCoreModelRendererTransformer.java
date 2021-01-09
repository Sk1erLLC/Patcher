package club.sk1er.patcher.tweaker.asm.modcore;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ModCoreModelRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.modcore.cosmetics.ModCoreModelRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("cosmeticsShouldRender")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.DSTORE) {
                        method.instructions.insertBefore(next.getNext(), checkPatcherRenderDistance());
                        break;
                    }
                }

                break;
            }
        }
    }

    private InsnList checkPatcherRenderDistance() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.DLOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "entityRenderDistance", "I"));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DCMPL));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLE, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "entityRenderDistanceToggle", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(label);
        return list;
    }
}
