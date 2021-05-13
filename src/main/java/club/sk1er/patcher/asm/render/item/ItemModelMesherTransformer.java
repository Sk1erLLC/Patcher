package club.sk1er.patcher.asm.render.item;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ItemModelMesherTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ItemModelMesher"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            final String methodDesc = mapMethodDesc(method);
            if ((methodName.equals("getItemModel") || methodName.equals("func_178089_a")) && methodDesc.equals("(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("getItem") || methodInsnName.equals("func_77973_b")) {
                            method.instructions.insertBefore(next.getNext().getNext(), returnMissingModel());
                            break;
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList returnMissingModel() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        LabelNode ifnonnull = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNONNULL, ifnonnull));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ItemModelMesher", "field_178090_d", "Lnet/minecraft/client/resources/model/ModelManager;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/resources/model/ModelManager", "func_174951_a", "()Lnet/minecraft/client/resources/model/IBakedModel;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(ifnonnull);
        return list;
    }
}
