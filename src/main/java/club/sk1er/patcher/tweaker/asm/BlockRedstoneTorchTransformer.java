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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ListIterator;

public class BlockRedstoneTorchTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.block.BlockRedstoneTorch"};
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
            if (methodNode.name.equals("<clinit>")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("newHashMap")) {
                        methodNode.instructions.remove(next);
                        break;
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), fixTorchMemoryLeak());
                break;
            }
        }
    }

    private InsnList fixTorchMemoryLeak() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/WeakHashMap"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/WeakHashMap", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC,
            "net/minecraft/block/BlockRedstoneTorch",
            "field_150112_b", // toggles
            "Ljava/util/Map;"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
