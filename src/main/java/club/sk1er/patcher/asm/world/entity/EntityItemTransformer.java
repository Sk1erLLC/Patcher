package club.sk1er.patcher.asm.world.entity;

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

public class EntityItemTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.item.EntityItem"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("searchForOtherItemsNearby") || methodName.equals("func_85054_d")) {
                methodNode.instructions.insert(stopSearch(true));
            } else if (methodName.equals("combineItems") || methodName.equals("func_70289_a")) {
                methodNode.instructions.insert(stopSearch(false));
            }
        }
    }

    private InsnList stopSearch(boolean voidReturnType) {
        int varIndex = voidReturnType ? 1 : 2;
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/item/EntityItem", "func_92059_d", "()Lnet/minecraft/item/ItemStack;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, varIndex));
        list.add(new VarInsnNode(Opcodes.ALOAD, varIndex));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "field_77994_a", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, varIndex));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77976_d", "()I", false));
        LabelNode ificmplt = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPLT, ificmplt));

        if (voidReturnType) {
            list.add(new InsnNode(Opcodes.RETURN));
        } else {
            list.add(new InsnNode(Opcodes.ICONST_0));
            list.add(new InsnNode(Opcodes.IRETURN));
        }

        list.add(ificmplt);
        return list;
    }
}
