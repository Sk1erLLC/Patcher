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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ScoreboardTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.scoreboard.Scoreboard"};
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

            if (methodName.equals("removeTeam") || methodName.equals("func_147194_f")) {
                methodNode.instructions.insert(checkNullTeam());
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                        String fieldInsnName = mapFieldNameFromNode(next);

                        if (fieldInsnName.equals("teams") || fieldInsnName.equals("field_96542_e")) {
                            LabelNode ifnull = new LabelNode();
                            methodNode.instructions.insertBefore(next.getPrevious(), checkNullRegisteredName(ifnull));

                            for (int i = 0; i < 4; i++) {
                                next = next.getNext();
                            }

                            methodNode.instructions.insertBefore(next.getNext(), ifnull);
                        }
                    }
                }
            } else if (methodName.equals("createTeam") || methodName.equals("func_96527_f")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("A team with the name '")) {
                        for (int i = 0; i < 5; i++) {
                            next = next.getPrevious();
                        }

                        for (int i = 0; i < 13; i++) {
                            methodNode.instructions.remove(next.getNext());
                        }

                        methodNode.instructions.insertBefore(next, returnExistingTeam());
                        methodNode.instructions.remove(next);
                        break;
                    }
                }
            }
        }
    }

    private InsnList checkNullTeam() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        LabelNode ifnonnull = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNONNULL, ifnonnull));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifnonnull);
        return list;
    }

    private InsnList checkNullRegisteredName(LabelNode ifnull) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/scoreboard/ScorePlayerTeam", "func_96661_b", "()Ljava/lang/String;", false));
        list.add(new JumpInsnNode(Opcodes.IFNULL, ifnull));
        return list;
    }

    private InsnList returnExistingTeam() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
