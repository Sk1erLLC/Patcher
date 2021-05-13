package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class RenderTNTPrimedTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderTNTPrimed"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("bindEntityTexture") || methodInsnName.equals("func_180548_c")) {
                            method.instructions.insertBefore(next.getNext().getNext(), rotateEntity());
                            break;
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList rotateEntity() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(-90.0F));
        list.add(new LdcInsnNode(0.0F));
        list.add(new LdcInsnNode(1.0F));
        list.add(new LdcInsnNode(0.0F));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179114_b", "(FFFF)V", false));
        return list;
    }
}
