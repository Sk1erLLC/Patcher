package club.sk1er.patcher.asm.external.forge.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiUtilsTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.client.config.GuiUtils"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            final String methodName = mapMethodName(classNode, methodNode);
            if ("drawHoveringText".equals(methodName)) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("disableDepth") || methodInsnName.equals("func_179097_i")) {
                                iterator.remove();
                            }
                        }
                    } else if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) next).var == 17) {
                        methodNode.instructions.insert(next, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "disableDepth" : "func_179097_i", "()V", false));
                    }
                }

                methodNode.instructions.insert(getMoveForward());
                methodNode.instructions.insertBefore(
                    methodNode.instructions.getLast().getPrevious(),
                    new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "popMatrix" : "func_179121_F", "()V", false)
                );
            }
        }
    }

    private InsnList getMoveForward() {
        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "pushMatrix" : "func_179094_E", "()V", false));
        insnList.add(new LdcInsnNode(0F));
        insnList.add(new LdcInsnNode(0F));
        insnList.add(new LdcInsnNode(-1F));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", isDevelopment() ? "translate" : "func_179109_b", "(FFF)V", false));
        return insnList;
    }
}
