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

package club.sk1er.patcher.asm.external.forge.loader;

import club.sk1er.patcher.tweaker.PatcherTweaker;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class FMLClientHandlerTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.client.FMLClientHandler"};
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
            switch (methodNode.name) {
                case "stripSpecialChars":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(fasterSpecialChars());
                    break;
                case "addModAsResource": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) next).name.equals("loadLanguagesFor")) {
                            for (int i = 0; i < 4; i++) {
                                methodNode.instructions.remove(next.getPrevious());
                            }

                            methodNode.instructions.remove(next);
                            break;
                        }
                    }
                    break;
                }
                case "finishMinecraftLoading": {
                    if (!PatcherTweaker.resourcepackManagerDetected) {
                        return;
                    }

                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("refreshResources") || methodInsnName.equals("func_110436_a")) {
                                methodNode.instructions.insertBefore(next, new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    getHookClass("FallbackResourceManagerHook"),
                                    "clearCache",
                                    "()V",
                                    false
                                ));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList fasterSpecialChars() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("FMLClientHandlerHook"), "stripSpecialChars", "(Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
