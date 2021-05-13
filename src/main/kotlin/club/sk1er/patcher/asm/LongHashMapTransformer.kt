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
import club.sk1er.patcher.tweaker.ClassTransformer
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import club.sk1er.patcher.util.enhancement.hash.FastHashedKey
import org.objectweb.asm.tree.ClassNode

class LongHashMapTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.util.LongHashMap")

    override fun transform(classNode: ClassNode, name: String) {
        if (ClassTransformer.optifineVersion != "NONE") {
            return
        }

        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getHashedKey" || methodName == "func_76155_g"
        }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(FastHashedKey::mix64)
                into(this@apply)
                param(0)
                keepReturns
            }
        }
    }
}