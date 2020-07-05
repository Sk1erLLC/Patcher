package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class BlockPistonStructureHelperTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.block.state.BlockPistonStructureHelper"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
            "directions",
            "[Lnet/minecraft/util/EnumFacing;",
            null,
            null));

        // bad news if another mod makes a static field lmfao
        classNode.methods.add(new MethodNode(Opcodes.ACC_STATIC,
            "<clinit>",
            "()V",
            null,
            null));

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodNode.name.equals("<clinit>")) {
                methodNode.instructions.insert(createDirections());
            } else if (methodName.equals("func_177250_b")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("values")) {
                        methodNode.instructions.insertBefore(next, useDirections());
                        methodNode.instructions.remove(next);
                    }
                }
            }
        }
    }

    private InsnList useDirections() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/state/BlockPistonStructureHelper", "directions", "[Lnet/minecraft/util/EnumFacing;"));
        return list;
    }

    private InsnList createDirections() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/EnumFacing", "values", "()[Lnet/minecraft/util/EnumFacing;", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraft/block/state/BlockPistonStructureHelper", "directions", "[Lnet/minecraft/util/EnumFacing;"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
