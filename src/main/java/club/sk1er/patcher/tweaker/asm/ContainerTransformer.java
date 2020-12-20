package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ContainerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.inventory.Container"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);

            if (methodName.equals("putStackInSlot") || methodName.equals("func_75141_a")) {
                method.instructions.insert(playArmorBreakSound());
                break;
            }
        }
    }

    private InsnList playArmorBreakSound() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("ContainerHook"), "playArmorBreakSound", "(Lnet/minecraft/inventory/Container;ILnet/minecraft/item/ItemStack;)V", false));
        return list;
    }
}
