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

package club.sk1er.patcher.asm

import club.sk1er.hookinjection.injectInstructions
import club.sk1er.patcher.hooks.FallbackResourceManagerHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.tree.ClassNode

class FallbackResourceManagerTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.resources.FallbackResourceManager")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getResource" || methodName == "func_110536_a"
        }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(FallbackResourceManagerHook::getCachedResource)
                into(this@apply)
                params(0, 1)
                keepReturns
            }
        }
    }
}