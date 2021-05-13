package club.sk1er.patcher.asm.world.entity;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityPlayerTransformer implements CommonTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.player.EntityPlayer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("getDisplayName") || methodName.equals("func_145748_c_")) {
                cachePlayerHoverEvents(method);
                break;
            }
        }
    }
}
