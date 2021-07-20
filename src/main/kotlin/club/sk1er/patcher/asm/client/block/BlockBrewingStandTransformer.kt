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

package club.sk1er.patcher.asm.client.block

import club.sk1er.hookinjection.injectInstructions
import club.sk1er.patcher.hooks.BlockBrewingStandHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.tree.ClassNode

class BlockBrewingStandTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.block.BlockBrewingStand")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "randomDisplayTick" || methodName == "func_180655_c"
        }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(BlockBrewingStandHook::randomDisplayTick)
                into(this@apply)
                params(1, 2, 4)
                keepReturns
            }
        }
    }
}