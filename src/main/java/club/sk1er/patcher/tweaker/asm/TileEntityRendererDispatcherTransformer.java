package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class TileEntityRendererDispatcherTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher"};
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

            if (methodName.equals("renderTileEntity") || methodName.equals("func_180546_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            if (((MethodInsnNode) next).name.equals("hasFastRenderer")) {
                                methodNode.instructions.insertBefore(
                                    next.getNext().getNext(),
                                    new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        "net/minecraft/client/renderer/RenderHelper",
                                        "func_74519_b",
                                        "()V",
                                        false));
                                break;
                            }
                        }
                    }
                }

                break;
            }
        }
    }
}
