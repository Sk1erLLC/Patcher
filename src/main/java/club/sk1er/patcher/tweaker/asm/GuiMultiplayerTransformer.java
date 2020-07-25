package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GuiMultiplayerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiMultiplayer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("keyTyped") || methodName.equals("func_73869_a")) {
                method.instructions.insertBefore(method.instructions.getFirst(), selectServer());
                break;
            }
        }
    }

    private InsnList selectServer() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/GuiMultiplayerHook", "keyTyped", "(Lnet/minecraft/client/gui/GuiMultiplayer;)V", false));
        return list;
    }
}
