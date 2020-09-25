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

package club.sk1er.patcher.tweaker.asm.util;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Used to make all methods within a class public to avoid {@link IllegalAccessException} when inlining hooks.
 * Typically only ever used in the case of a lambda, as those are inner classes that are set to private.
 */
public class ForcePublicTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{
            "club.sk1er.patcher.hooks.ModelLoaderHook"
        };
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.access & Opcodes.ACC_PUBLIC) == 0) {
                methodNode.access = (((methodNode.access & ~Opcodes.ACC_PRIVATE) & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC);
            }
        }
    }
}
