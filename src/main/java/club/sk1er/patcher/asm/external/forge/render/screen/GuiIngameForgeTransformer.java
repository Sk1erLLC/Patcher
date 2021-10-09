package club.sk1er.patcher.asm.external.forge.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiIngameForgeTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
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
            switch (mapMethodName(classNode, methodNode)) {
                case "renderHealth": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    boolean areDrawingAbsorption = false;
                    int foundCalls = 0;
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (!areDrawingAbsorption && next.getOpcode() == Opcodes.FLOAD && ((VarInsnNode) next).var == 18) {
                            areDrawingAbsorption = true;
                        }
                        if (areDrawingAbsorption) {
                            if (foundCalls < 2) {
                                if (next instanceof IntInsnNode) {
                                    IntInsnNode intI = (IntInsnNode) next;
                                    if ((intI.operand == 153 || intI.operand == 144) && next.getNext().getOpcode() == Opcodes.IADD) {
                                        foundCalls++;
                                        methodNode.instructions.insert(next.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("GuiIngameForgeHook"), "fixHealthMargin", "(I)I", false));
                                    }
                                }
                            } else break;
                        }
                    }
                    break;
                }
            }
        }
    }
}
