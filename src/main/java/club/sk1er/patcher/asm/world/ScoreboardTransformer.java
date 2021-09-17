package club.sk1er.patcher.asm.world;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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

            switch (methodName) {
                case "removeTeam":
                case "func_96511_d": {
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
                    break;
                }
                case "createTeam":
                case "func_96527_f": {
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
                    break;
                }
                case "removeObjective":
                case "func_96519_k": {
                    methodNode.instructions.insert(checkNullTeam());
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            final String fieldInsnName = mapFieldNameFromNode(next);
                            if (fieldInsnName.equals("scoreObjectives") || fieldInsnName.equals("field_96545_a")) {
                                LabelNode ifnull = new LabelNode();
                                methodNode.instructions.insertBefore(next.getPrevious(), checkNullName(ifnull));

                                for (int i = 0; i < 4; i++) {
                                    next = next.getNext();
                                }

                                methodNode.instructions.insert(next, ifnull);
                            }
                        }
                    }
                    break;
                }

                case "removePlayerFromTeam": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof InsnNode && next.getOpcode() == Opcodes.ATHROW) {
                            InsnList list = new InsnList();
                            list.add(new InsnNode(Opcodes.POP));
                            list.add(new InsnNode(Opcodes.RETURN));
                            methodNode.instructions.insertBefore(next, list);
                            methodNode.instructions.remove(next);
                            break;
                        }
                    }

                    break;
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

    private InsnList checkNullName(LabelNode ifnull) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/scoreboard/ScoreObjective", "func_96679_b", "()Ljava/lang/String;", false));
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
