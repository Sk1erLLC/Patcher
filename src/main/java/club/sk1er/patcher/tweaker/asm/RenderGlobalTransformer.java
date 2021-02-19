/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
                case "func_180447_b": {
                    addCloudTransparency(methodNode.instructions.iterator(), methodNode);
                    methodNode.instructions.insert(patcherCloudRenderer());
                    break;
                }

                case "renderCloudsFancy":
                case "func_180445_c": {
                    addCloudTransparency(methodNode.instructions.iterator(), methodNode);
                    break;
                }

                case "preRenderDamagedBlocks":
                case "func_180443_s": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(-3.0F)) {
                            ((LdcInsnNode) next).cst = next.getNext() instanceof LdcInsnNode ? -1.0F : -10.0F;
                        }
                    }
                    break;
                }

                case "getVisibleFacings":
                case "func_174978_c": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) next).var == 2) {
                            methodNode.instructions.insert(next, getSetLimited());
                            break;
                        }
                    }
                    break;
                }

                case "renderSky":
                case "func_174976_a": {
                    if (methodNode.desc.equals("(FI)V")) {
                        boolean shouldLook = false;
                        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                        while (iterator.hasNext()) {
                            AbstractInsnNode node = iterator.next();
                            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                String invokeName = mapMethodNameFromNode(node);
                                if (invokeName.equals("getPositionEyes") || invokeName.equals("func_174824_e")) {
                                    shouldLook = true;
                                }
                            } else if (shouldLook && node.getOpcode() == Opcodes.DCONST_0 && node.getNext().getOpcode() == Opcodes.DCMPG && node.getNext().getNext().getOpcode() == Opcodes.IFGE) {
                                JumpInsnNode jumpInsnNode = (JumpInsnNode) node.getNext().getNext();
                                methodNode.instructions.insert(jumpInsnNode, fixVoidRendering(jumpInsnNode.label));
                            }
                        }
                    }

                    break;
                }

                case "func_180449_a":
                case "renderWorldBorder": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                if (methodInsnName.equals("getClosestDistance") || methodInsnName.equals("func_177729_b")) {
                                    methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(),
                                        new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179106_n", "()V", false));
                                }
                            } else if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                                if ((methodInsnName.equals("depthMask") || methodInsnName.equals("func_179132_a")) && next.getPrevious().getOpcode() == Opcodes.ICONST_1) {
                                    methodNode.instructions.insert(next,
                                        new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179127_m", "()V", false)
                                    );
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList fixVoidRendering(LabelNode labelNode) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "playerVoidRendering", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        return list;
    }

    private void addCloudTransparency(ListIterator<AbstractInsnNode> iterator, MethodNode methodNode) {
        LabelNode ifne = new LabelNode();
        while (iterator.hasNext()) {
            AbstractInsnNode next = iterator.next();

            if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                String methodInsnName = mapMethodNameFromNode(next);

                switch (methodInsnName) {
                    case "func_179147_l":
                    case "enableBlend":
                        methodNode.instructions.insertBefore(next, checkConfig(ifne));
                        break;
                    case "func_179120_a":
                    case "tryBlendFuncSeparate":
                        methodNode.instructions.insertBefore(next.getNext(), addLabel(ifne));
                        break;
                    case "func_179084_k":
                    case "disableBlend":
                        LabelNode disableIfne = new LabelNode();
                        methodNode.instructions.insertBefore(next, checkConfig(disableIfne));
                        methodNode.instructions.insertBefore(next.getNext(), addLabel(disableIfne));
                        break;
                }
            }
        }
    }

    private InsnList addLabel(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(ifne);
        return list;
    }

    private InsnList checkConfig(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeCloudTransparency", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList getSetLimited() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/chunk/VisGraph", "patcherLimitScan", "Z"));
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
                "()Lclub/sk1er/patcher/util/world/cloud/CloudHandler;",
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
                "club/sk1er/patcher/util/world/cloud/CloudHandler",
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
