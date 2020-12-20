package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GlStateManagerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.GlStateManager"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            switch (methodName) {
                case "blendFunc":
                case "func_179112_b":
                    method.instructions.insert(blendFunc());
                    break;
                case "tryBlendFuncSeparate":
                case "func_179120_a":
                    method.instructions.insert(tryBlendFuncSeparate());
                    break;
            }
        }
    }

    private InsnList blendFunc() {
        InsnList list = new InsnList();
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179120_a", "(IIII)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList tryBlendFuncSeparate() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        LabelNode ificmpeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ,  ificmpeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ,  ificmpeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 1));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ,  ificmpeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        LabelNode ificmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE,  ificmpne));
        list.add(ificmpeq);
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179120_a", "(IIII)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ificmpne);
        return list;
    }
}
