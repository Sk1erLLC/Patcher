package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ArmorStandRendererTransformer implements CommonTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.ArmorStandRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("canRenderName") || methodName.equals("func_177070_b")) {
                method.instructions.insert(modifyNametagRenderState(false));
                break;
            }
        }
    }
}
