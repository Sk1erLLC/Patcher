package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiScreenOptionsSoundsTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreenOptionsSounds$Button"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if ((methodName.equals("mouseDragged") || methodName.equals("func_146119_b")) || (methodName.equals("mousePressed") || methodName.equals("func_146116_c"))) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("saveOptions") || methodInsnName.equals("func_74303_b")) {
                            method.instructions.remove(next.getPrevious().getPrevious().getPrevious());
                            method.instructions.remove(next.getPrevious().getPrevious());
                            method.instructions.remove(next.getPrevious());
                            method.instructions.remove(next);
                        }
                    }
                }
            } else if (methodName.equals("mouseReleased") || methodName.equals("func_146118_a")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("getSoundHandler") || methodInsnName.equals("func_147118_V")) {
                            method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), saveOptions());
                            break;
                        }
                    }
                }
            }
        }
    }

    private InsnList saveOptions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiScreenOptionsSounds$Button", "this$0", "Lnet/minecraft/client/gui/GuiScreenOptionsSounds;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiScreenOptionsSounds", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71474_y", "Lnet/minecraft/client/settings/GameSettings;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/settings/GameSettings", "func_74303_b", "()V", false));
        return list;
    }
}
