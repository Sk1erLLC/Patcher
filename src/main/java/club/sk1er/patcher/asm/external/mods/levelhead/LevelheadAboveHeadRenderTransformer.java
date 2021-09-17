package club.sk1er.patcher.asm.external.mods.levelhead;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import net.minecraft.scoreboard.ScoreObjective;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class LevelheadAboveHeadRenderTransformer implements CommonTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"club.sk1er.mods.levelhead.renderer.LevelheadAboveHeadRender"};
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
            if (methodNode.name.equals("renderName")) {
                methodNode.instructions.insert(modifyNametagRenderState(true));
                makeNametagTransparent(methodNode);
            } else if (methodNode.name.equals("render")) {
                makeNametagShadowed(methodNode);

                int scoreObjectiveIndex = -1;
                for (LocalVariableNode variable : methodNode.localVariables) {
                    if (variable.name.equals("scoreObjective")) {
                        scoreObjectiveIndex = variable.index;
                        break;
                    }
                }

                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof InsnNode && next.getOpcode() == Opcodes.DCONST_0) {
                        methodNode.instructions.insert(next, moveNametag(scoreObjectiveIndex));
                        methodNode.instructions.remove(next);
                        break;
                    }
                }
            }
        }
    }

    private InsnList moveNametag(int scoreObjectiveIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, scoreObjectiveIndex));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/asm/external/mods/levelhead/LevelheadAboveHeadRenderTransformer", "getPatcherOffset", "(Lnet/minecraft/scoreboard/ScoreObjective;)D", false));
        return list;
    }

    @SuppressWarnings("unused")
    public static double getPatcherOffset(ScoreObjective scoreObjective) {
        return PatcherConfig.showOwnNametag ? (scoreObjective == null ? 0.3 : 0.6) : 0;
    }
}
