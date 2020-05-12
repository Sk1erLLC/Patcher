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

public class ForgeChunkManagerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.common.ForgeChunkManager"};
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
            String methodName = methodNode.name;

            switch (methodName) {
                case "<clinit>":
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && ((FieldInsnNode) next).name.equals("forcedChunks")) {
                            for (int i = 0; i < 5; ++i) {
                                methodNode.instructions.remove(next.getPrevious());
                            }

                            methodNode.instructions.insertBefore(next, assignForcedChunks());
                            break;
                        }
                    }
                    break;
                case "unloadWorld":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), removeWorld());
                    break;
                case "getPersistentChunksFor":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(getHookedChunksMethod());
                    break;
            }
        }
    }

    private InsnList getHookedChunksMethod() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
            new FieldInsnNode(
                Opcodes.GETSTATIC,
                "net/minecraftforge/common/ForgeChunkManager",
                "forcedChunks",
                "Ljava/util/Map;"));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/hooks/ForgeChunkManagerHook",
                "getPersistentChunksFor",
                "(Lnet/minecraft/world/World;Ljava/util/Map;)Lcom/google/common/collect/ImmutableSetMultimap;",
                false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList removeWorld() {
        InsnList list = new InsnList();
        list.add(
            new FieldInsnNode(
                Opcodes.GETSTATIC,
                "net/minecraftforge/common/ForgeChunkManager",
                "forcedChunks",
                "Ljava/util/Map;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKEINTERFACE,
                "java/util/Map",
                "remove",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                true));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }

    private InsnList assignForcedChunks() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/WeakHashMap"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(
            new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/WeakHashMap", "<init>", "()V", false));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "java/util/Collections",
                "synchronizedMap",
                "(Ljava/util/Map;)Ljava/util/Map;",
                false));
        return list;
    }
}
