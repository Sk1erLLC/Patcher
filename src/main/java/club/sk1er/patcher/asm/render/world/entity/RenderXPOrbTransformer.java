package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class RenderXPOrbTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderXPOrb"};
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

            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(180.0f)) {
                        methodNode.instructions.insertBefore(next, fixRenderHeight());
                    } else if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(next);
                        if (fieldName.equals("playerViewX") || fieldName.equals("field_78732_j")) {
                            methodNode.instructions.insertBefore(next.getPrevious().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "club/sk1er/patcher/asm/external/mods/optifine/RenderTransformer", "checkPerspective",
                                "()F", false));
                            methodNode.instructions.insertBefore(next.getNext().getNext(), new InsnNode(Opcodes.FMUL));
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList fixRenderHeight() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new LdcInsnNode(0.1f));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraft/client/renderer/GlStateManager",
            isDevelopment() ? "translate" : "func_179109_b",
            "(FFF)V",
            false));
        return list;
    }
}
