package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.*;

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
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), fixInternalScoreboardNpe());
                break;
            }
        }
    }

    private InsnList fixInternalScoreboardNpe() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "patchInternalErrors", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(ALOAD, 1));
        LabelNode ifNonNull = new LabelNode();
        list.add(new JumpInsnNode(IFNONNULL, ifNonNull));
        list.add(new InsnNode(RETURN));
        list.add(ifNonNull);

        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/scoreboard/ScorePlayerTeam", "func_96661_b", // getRegisteredName
            "()Ljava/lang/String;", false));
        LabelNode ifNull = new LabelNode();
        list.add(new JumpInsnNode(IFNULL, ifNull));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/scoreboard/Scoreboard", "field_96542_e", // teams
            "Ljava/util/Map;"));
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/scoreboard/ScorePlayerTeam", "func_96661_b", // getRegisteredName
            "()Ljava/lang/String;", false));
        list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        list.add(new InsnNode(POP));
        list.add(ifNull);

        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/scoreboard/ScorePlayerTeam", "func_96670_d", // getMembershipCollection
            "()Ljava/util/Collection;", false));
        list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true));
        list.add(new VarInsnNode(ASTORE, 2));

        LabelNode gotoInsn = new LabelNode();
        list.add(gotoInsn);
        list.add(new VarInsnNode(ALOAD, 2));
        list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true));
        LabelNode ifeq1 = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, ifeq1));
        list.add(new VarInsnNode(ALOAD, 2));
        list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true));
        list.add(new TypeInsnNode(CHECKCAST, "java/lang/String"));
        list.add(new VarInsnNode(ASTORE, 3));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/scoreboard/Scoreboard", "field_96540_f", // teamMemberships
            "Ljava/util/Map;"));
        list.add(new VarInsnNode(ALOAD, 3));
        list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        list.add(new InsnNode(POP));
        list.add(new JumpInsnNode(GOTO, gotoInsn));
        list.add(ifeq1);

        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/scoreboard/Scoreboard", "func_96513_c", // no mcp name
            "(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V", false));
        list.add(new InsnNode(RETURN));
        list.add(ifeq);
        return list;
    }
}
