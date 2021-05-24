package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FramebufferTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.shader.Framebuffer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("bindFramebuffer") || methodName.equals("func_147610_a")) {
                method.instructions.insert(checkCurrentBuffer());
                break;
            }
        }
    }

    private InsnList checkCurrentBuffer() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/screen/render/caching/HUDCaching", "renderingCacheOverride", "Z"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_147110_a", "()Lnet/minecraft/client/shader/Framebuffer;", false));
        list.add(new JumpInsnNode(Opcodes.IF_ACMPNE, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/screen/render/caching/HUDCaching", "framebuffer", "Lnet/minecraft/client/shader/Framebuffer;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/shader/Framebuffer", "func_147610_a", "(Z)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }
}