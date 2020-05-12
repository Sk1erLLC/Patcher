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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class S0EPacketSpawnObjectTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.network.play.server.S0EPacketSpawnObject"};
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
            String methodDesc = mapMethodDesc(methodNode);
            if (methodNode.name.equals("<init>") && methodDesc.equals("(Lnet/minecraft/entity/Entity;II)V")) {
                changeJumpNode(methodNode);
                break;
            }
        }
    }

    public static void changeJumpNode(MethodNode methodNode) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

        while (iterator.hasNext()) {
            AbstractInsnNode next = iterator.next();

            if (next instanceof JumpInsnNode && next.getOpcode() == Opcodes.IFLE) {
                methodNode.instructions.insertBefore(next.getNext(), new JumpInsnNode(Opcodes.IFLT, ((JumpInsnNode) next).label));
                methodNode.instructions.remove(next);
                break;
            }
        }
    }
}
