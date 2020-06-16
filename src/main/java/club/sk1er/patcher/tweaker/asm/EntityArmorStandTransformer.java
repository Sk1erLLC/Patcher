package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityArmorStandTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.item.EntityArmorStand"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "cachedHeightResult", "F", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "cachedSmallResult", "B", null, null));
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "getEyeHeight":
                case "func_70047_e":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(fasterHeightCheck());
                    break;
                case "setSmall":
                case "func_175420_a":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), setCachedSmallResult());
                    break;
                case "isSmall":
                case "func_175410_n":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(getCachedSmallResult());
                    break;
            }

            if (methodNode.name.equals("<init>")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), initializeCachedSmallResult());
            }
        }
    }

    private InsnList initializeCachedSmallResult() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedSmallResult", "B"));
        return list;
    }

    private InsnList getCachedSmallResult() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedSmallResult", "B"));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        LabelNode ificmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, ificmpne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "field_70180_af", "Lnet/minecraft/entity/DataWatcher;"));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/DataWatcher", "func_75683_a", "(I)B", false));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.IAND));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        list.add(new InsnNode(Opcodes.I2B));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedSmallResult", "B"));
        list.add(ificmpne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedSmallResult", "B"));
        LabelNode ifle = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLE, ifle));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn2 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn2));
        list.add(ifle);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn2);
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }

    private InsnList setCachedSmallResult() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        list.add(new InsnNode(Opcodes.I2B));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedSmallResult", "B"));
        return list;
    }

    private InsnList fasterHeightCheck() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedHeightResult", "F"));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCMPL));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/item/EntityArmorStand", "func_70631_g_", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "field_70131_O", "F"));
        list.add(new LdcInsnNode(0.5F));
        list.add(new InsnNode(Opcodes.FMUL));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedHeightResult", "F"));
        list.add(new InsnNode(Opcodes.FRETURN));
        list.add(ifeq);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "field_70131_O", "F"));
        list.add(new LdcInsnNode(0.9F));
        list.add(new InsnNode(Opcodes.FMUL));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedHeightResult", "F"));
        list.add(new InsnNode(Opcodes.FRETURN));
        list.add(ifne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityArmorStand", "cachedHeightResult", "F"));
        list.add(new InsnNode(Opcodes.FRETURN));
        return list;
    }
}
