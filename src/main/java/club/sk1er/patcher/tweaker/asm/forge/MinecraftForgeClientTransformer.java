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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MinecraftForgeClientTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.MinecraftForgeClient"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode clearRenderCache = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "clearRenderCache", "()V", null, null);
        clearRenderCache.instructions.add(clearRenderCacheInstructions());
        classNode.methods.add(clearRenderCache);
    }

    private InsnList clearRenderCacheInstructions() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/client/MinecraftForgeClient", "regionCache", "Lcom/google/common/cache/LoadingCache;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/google/common/cache/LoadingCache", "invalidateAll", "()V", true));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/client/MinecraftForgeClient", "regionCache", "Lcom/google/common/cache/LoadingCache;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/google/common/cache/LoadingCache", "cleanUp", "()V", true));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
