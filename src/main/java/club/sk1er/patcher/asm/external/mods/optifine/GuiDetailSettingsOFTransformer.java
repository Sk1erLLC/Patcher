package club.sk1er.patcher.asm.external.mods.optifine;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class GuiDetailSettingsOFTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.optifine.gui.GuiDetailSettingsOF"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<clinit>")) {
                final InsnList instructions = method.instructions;
                ListIterator<AbstractInsnNode> iterator = instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode && ((FieldInsnNode) next).name.equals("ALTERNATE_BLOCKS")) {
                        instructions.remove(next.getPrevious().getPrevious());
                        instructions.remove(next.getPrevious());
                        instructions.remove(next.getNext());
                        instructions.remove(next);
                    }
                }

                iterator = instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof IntInsnNode && next.getOpcode() == Opcodes.BIPUSH) {
                        if (next.getNext() instanceof FieldInsnNode && next.getNext().getOpcode() == Opcodes.GETSTATIC) {
                            if (((IntInsnNode) next).operand == 16 && ((FieldInsnNode) next.getNext()).name.equals("SWAMP_COLORS")) {
                                ((IntInsnNode) next).operand = 15;
                            } else if (((IntInsnNode) next).operand == 17 && ((FieldInsnNode) next.getNext()).name.equals("SMOOTH_BIOMES")) {
                                ((IntInsnNode) next).operand = 16;
                            }
                        } else if (((IntInsnNode) next).operand == 18) {
                            ((IntInsnNode) next).operand = 17;
                        }
                    }
                }
            }
        }
    }
}
