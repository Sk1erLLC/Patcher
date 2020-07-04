package club.sk1er.patcher.asm

import club.sk1er.patcher.asm.utils.injectInstructions
import club.sk1er.patcher.tweaker.ClassTransformer
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import club.sk1er.patcher.util.hash.FastHashedKey
import org.objectweb.asm.tree.ClassNode

class LongHashMapTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.util.LongHashMap")

    override fun transform(classNode: ClassNode, name: String) {
        if (ClassTransformer.optifineVersion != "NONE") {
            println("OptiFine detected, not optimizing LongHashMap.")
            return
        } else {
            println("OptiFine not detected, optimizing LongHashMap.")
        }

        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getHashedKey" || methodName == "func_76155_g"
        }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(FastHashedKey::getFasterHashedKey)
                into(this@apply)
                param(0)
            }
        }
    }
}