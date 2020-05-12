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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class SoundManagerTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.audio.SoundManager"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        FieldNode pausedSounds = new FieldNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "pausedSounds", "Ljava/util/List;", null, null);
        classNode.fields.add(pausedSounds);

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "playSound":
                case "func_148611_c": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("Unable to play unknown soundEvent: {}")) {
                            next = next.getPrevious().getPrevious();

                            for (int i = 0; i < 10; ++i) {
                                methodNode.instructions.remove(next.getNext());
                            }

                            methodNode.instructions.remove(next);
                            break;
                        }
                    }
                    break;
                }
                case "func_148610_e":
                case "pauseAllSounds": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            if (((MethodInsnNode) next).name.equals("pause")) {
                                methodNode.instructions.insert(next, addToPausedSounds());
                                break;
                            }
                        }
                    }
                    break;
                }
                case "func_148604_f":
                case "resumeAllSounds": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            String fieldInsnName = mapFieldNameFromNode((FieldInsnNode) next);

                            if (fieldInsnName.equals("playingSounds") || fieldInsnName.equals("field_148629_h")) {
                                methodNode.instructions.remove(next.getNext());
                                methodNode.instructions.remove(next.getNext());
                                methodNode.instructions.insertBefore(next, iteratePausedSounds());
                                methodNode.instructions.remove(next);
                                break;
                            }
                        }
                    }

                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), clearPausedSounds());
                    break;
                }
                case "<init>":
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), createPausedSounds());
                    break;
            }
        }
    }

    private InsnList iteratePausedSounds() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/SoundManager", "pausedSounds", "Ljava/util/List;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true));
        return list;
    }

    private InsnList clearPausedSounds() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/SoundManager", "pausedSounds", "Ljava/util/List;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "clear", "()V", true));
        return list;
    }

    private InsnList addToPausedSounds() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/SoundManager", "pausedSounds", "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }

    private InsnList createPausedSounds() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/audio/SoundManager", "pausedSounds", "Ljava/util/List;"));
        return list;
    }
}
