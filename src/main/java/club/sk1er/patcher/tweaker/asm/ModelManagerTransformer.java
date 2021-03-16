package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ListIterator;

public class ModelManagerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.resources.model.ModelManager"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("onResourceManagerReload") || methodName.equals("func_110549_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof TypeInsnNode && next.getOpcode() == Opcodes.NEW && ((TypeInsnNode) next).desc.equals("net/minecraftforge/client/model/ModelLoader")) {
                        method.instructions.remove(next.getNext());
                        method.instructions.remove(next);
                    }
                }

                iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                        final String fieldName = mapFieldNameFromNode(next);
                        if ((fieldName.equals("modelProvider") || fieldName.equals("field_174957_c")) && next.getNext().getOpcode() == Opcodes.INVOKESPECIAL) {
                            method.instructions.remove(next.getNext());
                            method.instructions.insert(next,
                                new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("ModelManagerHook"), "createModelBakery",
                                    "(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/TextureMap;" +
                                        "Lnet/minecraft/client/renderer/BlockModelShapes;)Lnet/minecraft/client/resources/model/ModelBakery;", false)
                            );
                        }
                    }
                }

                iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("onModelBake")) {
                        LabelNode ifne = new LabelNode();
                        method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious().getPrevious(), checkOptionBeforePosting(ifne));
                        method.instructions.insertBefore(next.getNext(), ifne);
                    }
                }
            }
        }
    }

    private InsnList checkOptionBeforePosting(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "replaceModelLoader", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }
}
