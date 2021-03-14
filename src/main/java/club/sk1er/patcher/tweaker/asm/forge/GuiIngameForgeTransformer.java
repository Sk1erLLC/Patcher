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

package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiIngameForgeTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
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
            switch (mapMethodName(classNode, methodNode)) {
                case "renderGameOverlay":
                case "func_175180_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            if (((MethodInsnNode) next).name.equals("renderExperience")) {
                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), toggleAlpha(true));
                                methodNode.instructions.insertBefore(next.getNext(), toggleAlpha(false));
                                break;
                            }
                        }
                    }
                    break;
                }
                case "renderCrosshairs": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 775) {
                            LabelNode ifne = new LabelNode();
                            InsnList list = new InsnList();
                            methodNode.instructions.insertBefore(next, createToggle(ifne, list));

                            for (int i = 0; i < 5; ++i) {
                                next = next.getNext();
                            }

                            methodNode.instructions.insertBefore(next, ifne);
                            break;
                        }
                    }
                    break;
                }
                case "renderChat": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) next).name.equals("post")) {
                            if (((MethodInsnNode) next).owner.equals("net/minecraftforge/fml/common/eventhandler/EventBus")) {
                                methodNode.instructions.insertBefore(next.getNext().getNext(), fixProfilerSection());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "renderRecordOverlay": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("drawString") || methodInsnName.equals("func_78276_b")) {
                                for (int i = 0; i < 18; i++) {
                                    methodNode.instructions.remove(next.getPrevious());
                                }

                                methodNode.instructions.remove(next.getNext());
                                methodNode.instructions.insertBefore(next, drawCustomActionbar());
                                methodNode.instructions.remove(next);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList drawCustomActionbar() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "field_73838_g", "Ljava/lang/String;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 6));
        list.add(new VarInsnNode(Opcodes.ILOAD, 5));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 24));
        list.add(new InsnNode(Opcodes.ISHL));
        list.add(new InsnNode(Opcodes.IOR));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("GuiIngameForgeHook"), "drawActionbarText", "(Ljava/lang/String;I)V", false));
        return list;
    }

    private InsnList fixProfilerSection() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "field_73839_d", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71424_I", "Lnet/minecraft/profiler/Profiler;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/profiler/Profiler", "func_76322_c", "()Ljava/lang/String;", false));
        list.add(new LdcInsnNode("chat"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "field_73839_d", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71424_I", "Lnet/minecraft/profiler/Profiler;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/profiler/Profiler", "func_76319_b", "()V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList createToggle(LabelNode ifne, InsnList list) {
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeInvertFromCrosshair", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList toggleAlpha(boolean state) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraft/client/renderer/GlStateManager",
            state ? "func_179141_d" : "func_179118_c", // enableAlpha | disableAlpha
            "()V",
            false));
        return list;
    }
}
