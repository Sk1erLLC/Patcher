package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.modern;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ItemModelMesherReflectionOptimizer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ItemModelMesher"};
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
            final String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("getItemModel") || methodName.equals("func_178089_a")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("exists")) {
                        for (int i = 0; i < 13; ++i) {
                            methodNode.instructions.remove(next.getNext());
                        }

                        methodNode.instructions.remove(next.getPrevious());
                        methodNode.instructions.insertBefore(next, iSmartItemModelReflectionOptimization());
                        methodNode.instructions.remove(next);
                        break;
                    }
                }

                break;
            }
        }
    }

    private InsnList iSmartItemModelReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraftforge/client/model/ISmartItemModel"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraftforge/client/model/ISmartItemModel"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
            "net/minecraftforge/client/model/ISmartItemModel",
            "handleItemState",
            "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;",
            true));
        list.add(new VarInsnNode(Opcodes.ASTORE, 3));
        list.add(ifeq);
        return list;
    }
}
