package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ModelSkeletonTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.model.ModelSkeleton"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode postRenderArm = new MethodNode(Opcodes.ACC_PUBLIC, "func_178718_a", "(F)V", null, null);
        postRenderArm.instructions.add(this.fixItemModelPosition());
        classNode.methods.add(postRenderArm);
    }

    private InsnList fixItemModelPosition() {
        InsnList list = new InsnList();
        this.modifyRightArmRotation(list);
        list.add(new InsnNode(Opcodes.FADD));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelRenderer", "field_78800_c", "F"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/model/ModelSkeleton", "field_178723_h", "Lnet/minecraft/client/model/ModelRenderer;"));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/model/ModelRenderer", "func_78794_c", "(F)V", false));
        this.modifyRightArmRotation(list);
        list.add(new InsnNode(Opcodes.FSUB));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelRenderer", "field_78800_c", "F"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private void modifyRightArmRotation(InsnList list) {
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/model/ModelSkeleton", "field_178723_h", "Lnet/minecraft/client/model/ModelRenderer;"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/model/ModelRenderer", "field_78800_c", "F"));
        list.add(new InsnNode(Opcodes.FCONST_1));
    }
}
