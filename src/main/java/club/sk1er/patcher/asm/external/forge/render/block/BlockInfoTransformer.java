package club.sk1er.patcher.asm.external.forge.render.block;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BlockInfoTransformer implements PatcherTransformer {

    private static final String owner = "net/minecraftforge/client/model/pipeline/BlockInfo";

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.pipeline.BlockInfo"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode reset = new MethodNode(Opcodes.ACC_PUBLIC, "reset", "()V", null, null);
        reset.instructions.add(resetInstructions());
        classNode.methods.add(reset);

        MethodNode updateFlatLighting = new MethodNode(Opcodes.ACC_PUBLIC, "updateFlatLighting", "()V", null, null);
        updateFlatLighting.instructions.add(updateFlatLighting());
        classNode.methods.add(updateFlatLighting);

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("updateLightMatrix")) {
                clearInstructions(method);
                method.instructions.insert(useFasterLightMatrix());
                break;
            }
        }
    }

    private InsnList updateFlatLighting() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "block", "Lnet/minecraft/block/Block;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "world", "Lnet/minecraft/world/IBlockAccess;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "blockPos", "Lnet/minecraft/util/BlockPos;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("BlockInfoHook"), "updateFlatLighting",
            "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList useFasterLightMatrix() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "blockPos", "Lnet/minecraft/util/BlockPos;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "world", "Lnet/minecraft/world/IBlockAccess;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "block", "Lnet/minecraft/block/Block;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "translucent", "[[[Z"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "s", "[[[I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "b", "[[[I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "ao", "[[[F"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "skyLight", "[[[[F"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "blockLight", "[[[[F"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("BlockInfoHook"), "updateLightMatrix",
            "(Lnet/minecraft/util/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;[[[Z[[[I[[[I[[[F[[[[F[[[[F)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList resetInstructions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "world", "Lnet/minecraft/world/IBlockAccess;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "blockPos", "Lnet/minecraft/util/BlockPos;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "cachedTint", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "cachedMultiplier", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shz", "F"));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shy", "F"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shx", "F"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
