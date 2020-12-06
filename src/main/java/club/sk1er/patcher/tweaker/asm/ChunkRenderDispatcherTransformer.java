package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ChunkRenderDispatcherTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.chunk.ChunkRenderDispatcher"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("getNextChunkUpdate") || methodName.equals("func_178511_d")) {
                method.instructions.insert(limitChunkUpdates());
                break;
            }
        }
    }

    private InsnList limitChunkUpdates() {
        InsnList list = new InsnList();
        LabelNode gotoInsn = new LabelNode();
        list.add(gotoInsn);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/chunk/RenderChunk", "field_178592_a", "I"));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "chunkUpdateLimit", "I"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "limitChunks", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new LdcInsnNode(50L));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(label);
        return list;
    }
}
