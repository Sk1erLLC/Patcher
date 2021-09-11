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

package club.sk1er.patcher.asm.world.entity;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityTransformer implements CommonTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.Entity"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "displayNameCacheTime", "J", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "displayNameCache", "Lnet/minecraft/util/IChatComponent;", null, null));
        for (MethodNode methodNode : classNode.methods) {
            final String methodName = mapMethodName(classNode, methodNode);
            // todo: port this to a mixin
            if (methodName.equals("getDisplayName") || methodName.equals("func_145748_c_")) {
                cachePlayerHoverEvents(methodNode);
            }
        }
    }
}
