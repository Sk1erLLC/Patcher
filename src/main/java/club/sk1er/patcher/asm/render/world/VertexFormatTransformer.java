package club.sk1er.patcher.asm.render.world;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VertexFormatTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.vertex.VertexFormat"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "cachedHashcode", "I", null, null));

        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            String methodDesc = mapMethodDesc(method);

            if ((methodName.equals("<init>") && methodDesc.equals("(Lnet/minecraft/client/renderer/vertex/VertexFormat;)V"))
                || (methodName.equals("addElement") || methodName.equals("func_181721_a"))) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), assignCache());
            } else if (methodName.equals("hashCode")) {
                method.instructions.insert(returnCache());
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), reassignCache());
            }
        }
    }

    private InsnList reassignCache() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/vertex/VertexFormat", "cachedHashcode", "I"));
        return list;
    }

    private InsnList returnCache() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/vertex/VertexFormat", "cachedHashcode", "I"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/vertex/VertexFormat", "cachedHashcode", "I"));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList assignCache() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/vertex/VertexFormat", "cachedHashcode", "I"));
        return list;
    }
}
