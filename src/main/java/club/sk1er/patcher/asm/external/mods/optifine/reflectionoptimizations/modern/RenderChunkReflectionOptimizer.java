package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.modern;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RenderChunkReflectionOptimizer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.chunk.RenderChunk"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            if (methodName.equals("rebuildChunk") || methodName.equals("func_178581_b")) {
                int enumWorldBlockLayerIndex = -1;
                int blockIndex = -1;
                for (LocalVariableNode localVar : method.localVariables) {
                    if (localVar.name.equals("enumworldblocklayer1")) {
                        enumWorldBlockLayerIndex = localVar.index;
                    } else if (localVar.name.equals("block")) {
                        blockIndex = localVar.index;
                    }
                }

                // todo: replace Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, enumworldblocklayer1);
                //  I couldn't figure it out cause there's also Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, null);
                //  so any attempt at asm i made was too confusing to read & to work with.
                //  The main issue anyway was the reflection call to canRenderInLayer created 800mb worth of booleans super quickly,
                //  but removing all reflection here is a good plan.
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (((MethodInsnNode) next).name.equals("exists") && next.getPrevious() instanceof FieldInsnNode) {
                            FieldInsnNode previous = (FieldInsnNode) next.getPrevious();
                            String fieldName = previous.name;
                            if (fieldName.equals("ForgeBlock_canRenderInLayer")) {
                                method.instructions.remove(previous);
                                method.instructions.insertBefore(next, new InsnNode(Opcodes.ICONST_1));
                                method.instructions.remove(next);
                            } else if (fieldName.equals("ForgeHooksClient_setRenderLayer")) {
                                method.instructions.remove(previous);
                                method.instructions.insertBefore(next, new InsnNode(Opcodes.ICONST_1));
                                method.instructions.remove(next);
                            }
                        } else if (((MethodInsnNode) next).name.equals("blockHasTileEntity")) {
                            method.instructions.insertBefore(next.getPrevious(), new VarInsnNode(Opcodes.ALOAD, blockIndex));
                            method.instructions.insertBefore(next, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block",
                                "hasTileEntity", "(Lnet/minecraft/block/state/IBlockState;)Z", false));
                            method.instructions.remove(next);
                        }
                    } else if (next instanceof FieldInsnNode) {
                        String fieldName = ((FieldInsnNode) next).name;
                        if (fieldName.equals("ForgeBlock_canRenderInLayer") && next.getPrevious() instanceof VarInsnNode) {
                            for (int nodes = 0; nodes < 7; nodes++) {
                                method.instructions.remove(next.getNext());
                            }

                            method.instructions.insertBefore(next, callCanRenderInLayer(enumWorldBlockLayerIndex));
                            method.instructions.remove(next);
                        }
                    }
                }
            }
        }
    }

    private InsnList callCanRenderInLayer(int enumWorldBlockLayerIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, enumWorldBlockLayerIndex));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", "canRenderInLayer", "(Lnet/minecraft/util/EnumWorldBlockLayer;)Z", false));
        return list;
    }
}
