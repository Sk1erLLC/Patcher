package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                LabelNode _goto = new LabelNode();
                LabelNode ifne = new LabelNode();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof TypeInsnNode && next.getOpcode() == Opcodes.NEW && ((TypeInsnNode) next).desc.equals("net/minecraftforge/client/model/ModelLoader")) {
                        method.instructions.insertBefore(next, loadVanillaModelBakery(_goto));
                    } else if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) next).owner.equals("net/minecraftforge/client/model/ModelLoader") && ((MethodInsnNode) next).name.equals("<init>")) {
                            method.instructions.insertBefore(next.getNext(), _goto);
                        } else if (next.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) next).name.equals("onModelBake")) {
                            method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious().getPrevious(), checkOptionBeforePosting(ifne));
                            method.instructions.insertBefore(next.getNext(), ifne);
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList checkOptionBeforePosting(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "replaceModelLoader", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList loadVanillaModelBakery(LabelNode _goto) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "replaceModelLoader", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/client/resources/model/ModelBakery"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/model/ModelManager", "field_174956_b", "Lnet/minecraft/client/renderer/texture/TextureMap;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/model/ModelManager", "field_174957_c", "Lnet/minecraft/client/renderer/BlockModelShapes;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/resources/model/ModelBakery", "<init>", "(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/TextureMap;Lnet/minecraft/client/renderer/BlockModelShapes;)V", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, _goto));
        list.add(ifeq);
        return list;
    }
}
