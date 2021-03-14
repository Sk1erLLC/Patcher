package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RenderWitherTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderWither"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("preRenderCallback") || methodName.equals("func_77041_b")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("scale") || methodInsnName.equals("func_179152_a")) {
                            method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), dynamicShadowScale());
                            break;
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList dynamicShadowScale() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/RenderWither", "field_76989_e", "F"));
        return list;
    }
}
