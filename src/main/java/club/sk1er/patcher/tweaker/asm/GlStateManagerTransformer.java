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
                case "enableBlend":
                case "func_179147_l":
                    method.instructions.insert(checkSrcAndDst());
                    break;
                case "disableBlend":
                case "func_179084_k":
                    method.instructions.insert(disableWantBlend());
                    break;
                case "blendFunc":
                case "func_179112_b":
                case "tryBlendFuncSeparate":
                case "func_179120_a":
                    method.instructions.insertBefore(method.instructions.getLast().getPrevious(), checkBlendState());
                    break;
            }
        }
    }

    private InsnList checkBlendState() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHooksPackage() + "GlStateManagerHook", "srcFactor", "I"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHooksPackage() + "GlStateManagerHook", "dstFactor", "I"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        LabelNode ificmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, ificmpne));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
        list.add(ificmpne);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHooksPackage() + "GlStateManagerHook", "wantBlend", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179147_l", "()V", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, label));
        list.add(ifeq);
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179084_k", "()V", false));
        list.add(label);
        return list;
    }

    private InsnList disableWantBlend() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHooksPackage() + "GlStateManagerHook", "wantBlend", "Z"));
        return list;
    }

    private InsnList checkSrcAndDst() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHooksPackage() + "GlStateManagerHook", "wantBlend", "Z"));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/cache/HudCaching", "renderingCacheOverride", "Z"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHooksPackage() + "GlStateManagerHook", "srcFactor", "I"));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHooksPackage() + "GlStateManagerHook", "dstFactor", "I"));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }
}
