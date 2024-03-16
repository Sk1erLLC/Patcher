package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RenderPlayerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderPlayer"};
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

            MethodInsnNode disableBlend = new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "disableBlend" : "func_179084_k", "()V", false);
            switch (methodName) {
                //#if MC==10809
                case "renderRightArm":
                case "func_177138_b": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode node = iterator.next();

                        if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESPECIAL) {
                            String methodInsnName = mapMethodNameFromNode(node);
                            if (methodInsnName.equals("setModelVisibilities") || methodInsnName.equals("func_177137_d")) {
                                methodNode.instructions.insertBefore(node.getNext(), enableBlend());
                            }
                        }
                    }

                    methodNode.instructions.insertBefore(
                        methodNode.instructions.getLast().getPrevious(),
                        disableBlend
                    );
                    break;
                }

                case "doRender":
                case "func_76986_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("doRender") || methodInsnName.equals("func_76986_a")) {
                                methodNode.instructions.insertBefore(next.getNext(), disableBlend);
                            } else if (methodInsnName.equals("setModelVisibilities") || methodInsnName.equals("func_177137_d")) {
                                methodNode.instructions.insertBefore(next.getNext(), enableBlend());
                            }
                        }
                    }

                    break;
                }
                //#endif

                case "func_177137_d":
                case "setModelVisibilities": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            final String fieldName = mapFieldNameFromNode(next);
                            if ((fieldName.equals("bipedHeadwear") || fieldName.equals("field_178720_f")) && next.getNext().getOpcode() == Opcodes.ICONST_1) {
                                methodNode.instructions.remove(next.getNext());
                                methodNode.instructions.insertBefore(next.getNext(), checkHatLayer());
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList checkHatLayer() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/entity/player/EnumPlayerModelParts", "HAT", "Lnet/minecraft/entity/player/EnumPlayerModelParts;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/AbstractClientPlayer", isDevelopment() ? "func_175148_a" : "isWearing", "(Lnet/minecraft/entity/player/EnumPlayerModelParts;)Z", false));
        return list;
    }

    public InsnList enableBlend() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "enableBlend" : "func_179147_l", "()V", false));
        list.add(new IntInsnNode(Opcodes.SIPUSH, GL11.GL_SRC_ALPHA));
        list.add(new IntInsnNode(Opcodes.SIPUSH, GL11.GL_ONE_MINUS_SRC_ALPHA));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "tryBlendFuncSeparate" : "func_179120_a", "(IIII)V", false));
        return list;
    }
}
