package club.sk1er.patcher.asm.client

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import org.objectweb.asm.tree.ClassNode

class EnchantmentTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.enchantment.Enchantment")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getTranslatedName" || methodName == "func_77316_c"
        }?.apply {
            instructions.insertBefore(instructions.first, `get Numerical Name`())
        }
    }

    // oooooh spooky space in function name ooooh
    private fun `get Numerical Name`() = assembleBlock {
        getstatic("club/sk1er/patcher/config/PatcherConfig", "romanNumerals", boolean)
        ifeq(L["1"])
        new(StringBuilder::class)
        dup
        invokespecial(StringBuilder::class, "<init>", void)
        aload_0
        invokevirtual("net/minecraft/enchantment/Enchantment", "func_77320_a", String::class)
        invokestatic("net/minecraft/util/StatCollector", "func_74838_a", String::class, String::class)
        invokevirtual(StringBuilder::class, "append", StringBuilder::class, String::class)
        ldc(" ")
        invokevirtual(StringBuilder::class, "append", StringBuilder::class, String::class)
        iload_1
        invokevirtual(StringBuilder::class, "append", StringBuilder::class, int)
        invokevirtual(StringBuilder::class, "toString", String::class)
        areturn
        +L["1"]
    }.first
}