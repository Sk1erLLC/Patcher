/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ModelLoaderTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.ModelLoader"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.interfaces.add("club/sk1er/patcher/hooks/IModelLoader");

        MethodNode callLoadBlocks = new MethodNode(Opcodes.ACC_PUBLIC, "callLoadBlocks", "()V", null, null);
        callLoadBlocks.instructions.add(createCallLoadBlocks());
        classNode.methods.add(callLoadBlocks);

        MethodNode callLoadItems = new MethodNode(Opcodes.ACC_PUBLIC, "callLoadItems", "()V", null, null);
        callLoadItems.instructions.add(createCallLoadItems());
        classNode.methods.add(callLoadItems);

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodNode.name.equals("onPostBakeEvent")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof FieldInsnNode && ((FieldInsnNode) next).name.equals("isLoading")) {
                        methodNode.instructions.insertBefore(next.getPrevious(), clearMemory());
                        break;
                    }
                }
            } else if (methodName.equals("setupModelRegistry") || methodName.equals("func_177570_a")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getAsyncLoader());
            }
        }
    }

    private InsnList getAsyncLoader() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/ModelLoader", "isLoading", "Z"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "missingModel", "Lnet/minecraftforge/client/model/IModel;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "stateModels", "Ljava/util/Map;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "textures", "Ljava/util/Set;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/ModelLoaderHook", "setupModelRegistry", "(Lnet/minecraftforge/client/model/ModelLoader;Lnet/minecraftforge/client/model/IModel;Ljava/util/Map;Ljava/util/Set;)Lnet/minecraft/util/IRegistry;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList createCallLoadItems() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/client/model/ModelLoader", "loadItems", "()V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList createCallLoadBlocks() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/client/model/ModelLoader", "loadBlocks", "()V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList clearMemory() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "loadingExceptions", "Ljava/util/Map;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Map", "clear", "()V", true));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader", "missingVariants", "Ljava/util/Set;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "clear", "()V", true));
        return list;
    }
}
