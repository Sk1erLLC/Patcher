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

package club.sk1er.patcher.asm.network;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class MinecraftServerTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.server.MinecraftServer"};
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
                case "addFaviconToStatusResponse":
                case "func_147138_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();
                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                            String methodInsnName = mapMethodNameFromNode(methodInsnNode);
                            if (methodInsnName.equals("setFavicon") || methodInsnName.equals("func_151320_a")) {
                                methodNode.instructions.insertBefore(methodInsnNode.getNext(), releaseIcon());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "<init>":
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), createMetricsData());
                    break;
                case "tick":
                case "func_71217_p": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof InsnNode && next.getOpcode() == Opcodes.LASTORE) {
                            methodNode.instructions.insertBefore(next.getNext(), pushSample());
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList pushSample() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHookClass("MinecraftServerHook"), "metricsData", "Lclub/sk1er/patcher/screen/render/overlay/metrics/MetricsData;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false));
        list.add(new VarInsnNode(Opcodes.LLOAD, 1));
        list.add(new InsnNode(Opcodes.LSUB));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData", "pushSample", "(J)V", false));
        return list;
    }

    private InsnList createMetricsData() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("MinecraftServerHook"), "metricsData", "Lclub/sk1er/patcher/screen/render/overlay/metrics/MetricsData;"));
        return list;
    }

    private InsnList releaseIcon() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/netty/buffer/ByteBuf", "release", "()Z", false));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }
}
