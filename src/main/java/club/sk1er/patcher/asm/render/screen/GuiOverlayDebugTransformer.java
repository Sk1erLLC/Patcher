package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.optifine.OptiFineGenerations;
import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiOverlayDebugTransformer implements PatcherTransformer {

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiOverlayDebug"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        final String optifineVersion = ClassTransformer.optifineVersion;
        final OptiFineGenerations generations = ClassTransformer.generations;
        final boolean compatibleVersion = generations.getLGeneration().contains(optifineVersion)
            || generations.getMGeneration().contains(optifineVersion)
            || generations.getFutureGeneration().contains(optifineVersion);

        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("getDebugInfoRight") || methodName.equals("func_175238_c")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("addAll")) {
                        method.instructions.insertBefore(next.getNext(), insertPatcher());
                        break;
                    }
                }
            } else if (methodName.equals("call") && compatibleVersion) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("/")) {
                        AbstractInsnNode prev = next;
                        for (int i = 0; i < 8; i++) prev = prev.getPrevious();
                        JumpInsnNode iflt = (JumpInsnNode) prev;
                        method.instructions.insert(iflt, jumpIfNormalFpsCounter(iflt.label));
                        break;
                    }
                }
            }
        }
    }

    private InsnList insertPatcher() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 9));
        list.add(new LdcInsnNode("Patcher " + Patcher.VERSION));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }

    private InsnList jumpIfNormalFpsCounter(LabelNode labelNode) {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("normalFpsCounter", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        return list;
    }
}
