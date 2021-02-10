package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VertexLighterSmoothAoTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("updateLightmap")) {
                clearInstructions(method);
                method.instructions.insert(fasterLightmapCalculation());
                break;
            }
        }
    }

    private InsnList fasterLightmapCalculation() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/VertexLighterSmoothAo", "blockInfo", "Lnet/minecraftforge/client/model/pipeline/BlockInfo;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new VarInsnNode(Opcodes.FLOAD, 4));
        list.add(new VarInsnNode(Opcodes.FLOAD, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("VertexLighterSmoothAoHook"), "calcLightmap", "(Lnet/minecraftforge/client/model/pipeline/BlockInfo;[FFFF)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
