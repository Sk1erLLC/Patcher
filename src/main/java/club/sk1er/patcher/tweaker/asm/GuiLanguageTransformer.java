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

public class GuiLanguageTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiLanguage"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode onGuiClosed = new MethodNode(Opcodes.ACC_PUBLIC, "func_146281_b", "()V", null, null);
        onGuiClosed.instructions.add(createOnGuiClosedInsns());
        classNode.methods.add(onGuiClosed);
    }

    private InsnList createOnGuiClosedInsns() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiLanguage", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71456_v", "Lnet/minecraft/client/gui/GuiIngame;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/GuiIngame", "getChatGUI", "()Lnet/minecraft/client/gui/GuiNewChat;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/GuiNewChat", "refreshChat", "()V;", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
