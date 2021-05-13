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

package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiScreenResourcePacksTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreenResourcePacks"};
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

            if ((methodName.equals("drawScreen") || methodName.equals("func_73863_a")) && !Loader.isModLoaded("ResourcePackOrganizer")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 77) {
                        ((IntInsnNode) next).operand = 102;
                        break;
                    }
                }
            } else if (methodName.equals("actionPerformed") || methodName.equals("func_146284_a")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("saveOptions") || methodInsnName.equals("func_74303_b")) {
                                methodNode.instructions.insertBefore(next.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    getHookClass("FallbackResourceManagerHook"),
                                    "clearCache",
                                    "()V",
                                    false));
                                break;
                            }
                        } else if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                            if (((MethodInsnNode) next).name.equals("reverse")) {
                                methodNode.instructions.insertBefore(next.getPrevious(), new MethodInsnNode(
                                    Opcodes.INVOKESTATIC, getHookClass("GuiScreenResourcePacksHook"), "clearHandles", "()V", false)
                                );
                            }
                        }
                    }
                }
            } else if (methodName.equals("initGui") || methodName.equals("func_73866_w_")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("getRepositoryEntriesAll") || methodInsnName.equals("func_110609_b")) {
                            next = next.getPrevious();

                            for (int i = 0; i < 14; i++) {
                                methodNode.instructions.remove(next.getNext());
                            }

                            methodNode.instructions.insertBefore(next, replaceList());
                            methodNode.instructions.remove(next);
                        }
                    }
                }
            }
        }
    }

    private InsnList replaceList() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashSet"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/resources/ResourcePackRepository", "func_110609_b", "()Ljava/util/List;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashSet", "<init>", "(Ljava/util/Collection;)V", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 50));
        list.add(new VarInsnNode(Opcodes.ALOAD, 50));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/resources/ResourcePackRepository", "func_110613_c", "()Ljava/util/List;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashSet", "removeAll", "(Ljava/util/Collection;)Z", false));
        list.add(new InsnNode(Opcodes.POP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 50));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashSet", "iterator", "()Ljava/util/Iterator;", false));
        return list;
    }
}
