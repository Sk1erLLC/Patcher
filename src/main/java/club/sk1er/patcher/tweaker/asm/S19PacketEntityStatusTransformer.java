package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class S19PacketEntityStatusTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.network.play.server.S19PacketEntityStatus"};
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

            if (methodName.equals("getEntity") || methodName.equals("func_149381_a")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(
                    S14PacketEntityTransformer.getFixedEntity(
                        "net/minecraft/network/play/server/S19PacketEntityStatus",
                        "field_149164_a")
                );
                break;
            }
        }
    }
}
