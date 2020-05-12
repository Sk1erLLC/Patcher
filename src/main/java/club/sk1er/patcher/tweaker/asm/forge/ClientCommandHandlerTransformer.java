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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ClientCommandHandlerTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.ClientCommandHandler"};
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

            if (methodName.equals("executeCommand") || methodName.equals("func_71556_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof InsnNode && node.getOpcode() == Opcodes.AALOAD) {
                        methodNode.instructions.insertBefore(node.getNext(), makeLowercase());
                        break;
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), checkForSlash());
                break;
            }
        }
    }

    public static InsnList makeLowercase() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false));
        return list;
    }

    private InsnList checkForSlash() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "forgeCommandHandling", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
        list.add(new LdcInsnNode("/"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        list.add(ifne);
        return list;
    }
}
