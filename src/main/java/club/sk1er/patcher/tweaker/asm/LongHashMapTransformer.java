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

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LongHashMapTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.util.LongHashMap"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        if (!ClassTransformer.optifineVersion.equals("NONE")) {
            System.out.println("OptiFine detected, not optimizing LongHashMap.");
            return;
        } else {
            System.out.println("OptiFine not detected, optimizing LongHashMap.");
        }

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("getHashedKey") || methodName.equals("func_76155_g")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getFasterHashedKey());
                break;
            }
        }
    }

    private InsnList getFasterHashedKey() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.LLOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/hash/FastHashedKey", "getFasterHashedKey", "(J)I", false));
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }
}
