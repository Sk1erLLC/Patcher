package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@SuppressWarnings("unused")
public class BlockCactusTransformer implements PatcherTransformer {

    public static final AxisAlignedBB CACTUS_COLLISION_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    public static final AxisAlignedBB CACTUS_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.block.BlockCactus"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("getCollisionBoundingBox") || methodName.equals("func_180640_a")) {
                clearInstructions(method);
                method.instructions.insert(fixedBoundingBox());
            } else if (methodName.equals("getSelectedBoundingBox") || methodName.equals("func_180646_a")) {
                clearInstructions(method);
                method.instructions.insert(fixedSelectionBox());
            }
        }
    }

    private InsnList fixedSelectionBox() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/tweaker/asm/BlockCactusTransformer", "CACTUS_AABB", "Lnet/minecraft/util/AxisAlignedBB;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/tweaker/asm/BlockCactusTransformer", "offset", "(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList fixedBoundingBox() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/tweaker/asm/BlockCactusTransformer", "CACTUS_COLLISION_AABB", "Lnet/minecraft/util/AxisAlignedBB;"));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    public static AxisAlignedBB offset(AxisAlignedBB bb, BlockPos pos) {
        return new AxisAlignedBB(bb.minX + (double) pos.getX(),
            bb.minY + (double) pos.getY(),
            bb.minZ + (double) pos.getZ(),
            bb.maxX + (double) pos.getX(),
            bb.maxY + (double) pos.getY(),
            bb.maxZ + (double) pos.getZ());
    }
}
