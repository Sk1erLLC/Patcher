package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LayerSpiderEyesTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerSpiderEyes"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), fixEyeDepth());
                break;
            }
        }
    }

    private InsnList fixEyeDepth() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179132_a", "(Z)V", false));
        return list;
    }
}
