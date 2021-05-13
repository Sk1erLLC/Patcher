package club.sk1er.patcher.asm.external.mods.optifine.signfix;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiEditSignTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.inventory.GuiEditSign"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
            "currentlyEditedSign",
            "Lnet/minecraft/tileentity/TileEntitySign;",
            null,
            null));

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>")) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), createSignObject());
            }

            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("onGuiClosed") || methodName.equals("func_146281_b")) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), clearSign());
            }
        }
    }

    private InsnList clearSign() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraft/client/gui/inventory/GuiEditSign", "currentlyEditedSign", "Lnet/minecraft/tileentity/TileEntitySign;"));
        return list;
    }

    private InsnList createSignObject() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraft/client/gui/inventory/GuiEditSign", "currentlyEditedSign", "Lnet/minecraft/tileentity/TileEntitySign;"));
        return list;
    }
}
