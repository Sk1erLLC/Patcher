package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LoadingScreenRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.LoadingScreenRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("setLoadingProgress") || methodName.equals("func_73718_a")) {
                method.instructions.insert(skipProgress());
                break;
            }
        }
    }

    private InsnList skipProgress() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFGE, label));
        list.add(getPatcherSetting("optimizedWorldSwapping", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }
}
