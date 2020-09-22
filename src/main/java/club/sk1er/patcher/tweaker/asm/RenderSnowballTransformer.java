package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class RenderSnowballTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderSnowball"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof FieldInsnNode && node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode((FieldInsnNode) node);
                        if (fieldName.equals("playerViewX") || fieldName.equals("field_78732_j")) {
                            method.instructions.insertBefore(node.getPrevious().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "club/sk1er/patcher/tweaker/asm/optifine/RenderTransformer", "checkPerspective",
                                "()F", false));
                            method.instructions.insertBefore(node.getNext(), new InsnNode(Opcodes.FMUL));
                            break;
                        }
                    }
                }

                break;
            }
        }
    }
}
