package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RenderGlobalTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
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

            switch (methodName) {
                case "renderClouds":
                case "func_180447_b":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), patcherCloudRenderer());
                    break;
                case "preRenderDamagedBlocks":
                case "func_180443_s": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(-3.0F)) {
                            if (next.getNext() instanceof LdcInsnNode) {
                                ((LdcInsnNode) next).cst = -1.0F;
                            } else {
                                ((LdcInsnNode) next).cst = -10.0F;
                            }
                        }
                    }
                    break;
                }
                case "setupTerrain":
                case "func_174970_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEINTERFACE) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                            if (methodInsnName.equals("isBoundingBoxInFrustum") || methodInsnName.equals("func_78546_a")) {
                                methodNode.instructions.insertBefore(next, checkYLevel());
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList checkYLevel() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 19));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/util/BlockPos",
            "func_177956_o", // getY
            "()I",
            false));
        LabelNode ifle = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLE, ifle));
        list.add(new LdcInsnNode(Double.POSITIVE_INFINITY));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifle);
        list.add(new LdcInsnNode(Double.NEGATIVE_INFINITY));
        list.add(gotoInsn);
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/util/AxisAlignedBB",
            "func_72321_a", // addCoord
            "(DDD)Lnet/minecraft/util/AxisAlignedBB;",
            false));
        return list;
    }

    private InsnList patcherCloudRenderer() {
        InsnList list = new InsnList();
        list.add(
            new FieldInsnNode(
                Opcodes.GETSTATIC,
                "club/sk1er/patcher/Patcher",
                "instance",
                "Lclub/sk1er/patcher/Patcher;"));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "club/sk1er/patcher/Patcher",
                "getCloudHandler",
                "()Lclub/sk1er/patcher/util/cloud/CloudHandler;",
                false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
            new FieldInsnNode(
                Opcodes.GETFIELD,
                "net/minecraft/client/renderer/RenderGlobal",
                "field_72773_u", // cloudTickCounter
                "I"));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "club/sk1er/patcher/util/cloud/CloudHandler",
                "renderClouds",
                "(IF)Z",
                false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
