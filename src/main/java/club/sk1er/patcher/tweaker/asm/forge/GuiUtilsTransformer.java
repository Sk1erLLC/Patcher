package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiUtilsTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.client.config.GuiUtils"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if ("drawHoveringText".equals(methodName)) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                            if (((MethodInsnNode) next).name.equals("disableDepth") || ((MethodInsnNode) next).name.equals("func_179097_i")) {
                                iterator.remove();
                            }
                        }
                    } else if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) next).var == 17) {
                        methodNode.instructions.insert(next, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179097_i", "()V", false));
                    }
                }
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getMoveForward());
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), getReset());
            }
        }
    }

    private AbstractInsnNode getReset() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179121_F", // translate
                "()V", false);
    }

    private InsnList getMoveForward() {
        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179094_E", "()V", false)); //Push matrix
        insnList.add(new LdcInsnNode(-250F));
        insnList.add(new LdcInsnNode(0F));
        insnList.add(new LdcInsnNode(-1F));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179109_b", // translate
                "(FFF)V", false));
        return insnList;
    }
}
