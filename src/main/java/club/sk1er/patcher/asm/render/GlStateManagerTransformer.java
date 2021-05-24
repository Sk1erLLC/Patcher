package club.sk1er.patcher.asm.render;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GlStateManagerTransformer implements PatcherTransformer {

    private final String hudCaching = "club/sk1er/patcher/screen/render/caching/HUDCaching";

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.GlStateManager"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            switch (methodName) {
                case "func_179112_b":
                case "blendFunc":
                    method.instructions.insert(blendFuncHook());
                    break;

                case "func_179120_a":
                case "tryBlendFuncSeparate":
                    method.instructions.insert(tryBlendFuncSeparateHook());
                    break;

                case "func_179084_k":
                case "disableBlend":
                    method.instructions.insert(toggleBlendHook(false));
                    break;

                case "func_179147_l":
                case "enableBlend":
                    method.instructions.insert(toggleBlendHook(true));
                    break;

                case "func_179131_c":
                case "color":
                    if (method.desc.equals("(FFFF)V")) method.instructions.insert(colorHook());
                    break;
            }
        }
    }

    private InsnList colorHook() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHookClass("GlStateManagerHook"), "blendEnabled", "Z"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, hudCaching, "renderingCacheOverride", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new InsnNode(Opcodes.FCMPG));
        list.add(new JumpInsnNode(Opcodes.IFGE, label));
        list.add(new VarInsnNode(Opcodes.FLOAD, 0));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 2));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179131_c", "(FFFF)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }

    private InsnList toggleBlendHook(boolean mode) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, hudCaching, "renderingCacheOverride", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(mode ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("GlStateManagerHook"), "blendEnabled", "Z"));
        list.add(ifeq);
        return list;
    }

    private InsnList tryBlendFuncSeparateHook() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, hudCaching, "renderingCacheOverride", "Z"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new IntInsnNode(Opcodes.SIPUSH, GL11.GL_ONE_MINUS_SRC_ALPHA));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("GlStateManagerHook"), "desSrcFactor", "I"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("GlStateManagerHook"), "desDstFactor", "I"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("GlStateManagerHook"), "desSrcAlphaFactor", "I"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, GL11.GL_ONE_MINUS_SRC_ALPHA));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "func_148821_a", "(IIII)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }

    private InsnList blendFuncHook() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, hudCaching, "renderingCacheOverride", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, GL11.GL_ONE_MINUS_SRC_ALPHA));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "func_148821_a", "(IIII)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}