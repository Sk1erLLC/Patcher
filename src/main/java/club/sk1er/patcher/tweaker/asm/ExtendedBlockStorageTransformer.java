package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class ExtendedBlockStorageTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.chunk.storage.ExtendedBlockStorage"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("isEmpty") || methodName.equals("func_76663_a")) {
                method.instructions.insertBefore(method.instructions.getFirst(), fixFaultyChecks());
                break;
            }
        }
    }

    private InsnList fixFaultyChecks() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "chunkLighting", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }
}
