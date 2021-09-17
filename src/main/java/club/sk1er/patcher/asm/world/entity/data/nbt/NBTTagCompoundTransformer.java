package club.sk1er.patcher.asm.world.entity.data.nbt;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class NBTTagCompoundTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.nbt.NBTTagCompound"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "profile", "Lcom/mojang/authlib/GameProfile;", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "compound", "Lnet/minecraft/nbt/NBTTagCompound;", null, null));
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("setTag") || methodName.equals("func_74782_a")) {
                method.instructions.insert(preventEntityCrash());
                break;
            }
        }
    }

    private InsnList preventEntityCrash() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("NBTTagCompoundHook"), "checkNullValue", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false));
        return list;
    }
}
