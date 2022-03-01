package club.sk1er.patcher.asm.render.world;

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
        list.add(getPatcherSetting("playerVoidRendering", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        return list;
    }
}
