package club.sk1er.patcher.tweaker.asm.block;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BlockPotatoTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.block.BlockPotato", "net.minecraft.block.BlockCarrot"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        final MethodNode getSelectionBoundingBox = new MethodNode(Opcodes.ACC_PUBLIC, "func_180646_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", null, null);
        InsnList list = new InsnList();
        final LabelNode beforeReturn = new LabelNode();
        final LabelNode ifne = new LabelNode();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/mods/core/util/MinecraftUtils", "isHypixel", "()Z", false));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_71387_A", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "futureHitBoxes", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/FarmHook", "getBox", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)Lnet/minecraft/util/AxisAlignedBB;", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, beforeReturn));
        list.add(ifeq);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/block/Block", "func_180646_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", false));
        list.add(beforeReturn);
        list.add(new InsnNode(Opcodes.ARETURN));
        getSelectionBoundingBox.instructions.add(list);
        classNode.methods.add(getSelectionBoundingBox);
    }
}
