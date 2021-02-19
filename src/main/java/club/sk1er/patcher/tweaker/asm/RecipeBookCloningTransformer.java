package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RecipeBookCloningTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.item.crafting.RecipeBookCloning"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        LabelNode ifeq = new LabelNode();
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("getCraftingResult") || methodName.equals("func_77572_b")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if ((methodInsnName.equals("getGeneration") || methodInsnName.equals("func_179230_h")) && next.getNext().getOpcode() == Opcodes.ICONST_2) {
                            method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious().getPrevious(), checkTagCompound(ifeq));
                        }
                    }
                }

                method.instructions.insertBefore(method.instructions.getLast().getPrevious().getPrevious(), ifeq);
                break;
            }
        }
    }

    private InsnList checkTagCompound(LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77942_o", "()Z", false));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        return list;
    }
}
