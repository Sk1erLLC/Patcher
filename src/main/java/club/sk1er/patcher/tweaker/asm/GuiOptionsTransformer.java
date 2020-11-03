package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiOptionsTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiOptions"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        final MethodNode onGuiClosed = new MethodNode(Opcodes.ACC_PUBLIC, "onGuiClosed", "()V", null, null);
        onGuiClosed.instructions.add(saveOptions());
        classNode.methods.add(onGuiClosed);
    }

    private InsnList saveOptions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiOptions", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71474_y", "Lnet/minecraft/client/settings/GameSettings;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/settings/GameSettings", "func_74303_b", "()V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
