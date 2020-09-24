package club.sk1er.patcher.asm.forge

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import com.google.common.collect.SetMultimap
import org.objectweb.asm.tree.ClassNode

class ASMDataTableTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraftforge.fml.common.discovery.ASMDataTable")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            it.name == "getAnnotationsFor"
        }?.apply {
            clearInstructions(this)
            this.instructions.insert(getOptimizedSearch())
        }
    }

    private fun getOptimizedSearch() = assembleBlock {
        invokestatic("club/sk1er/patcher/discovery/DataTableSearch", "getInstance", "club/sk1er/patcher/discovery/DataTableSearch")
        aload_1
        aload_0
        getfield("net/minecraftforge/fml/common/discovery/ASMDataTable", "containers", List::class)
        aload_0
        getfield(
            "net/minecraftforge/fml/common/discovery/ASMDataTable",
            "globalAnnotationData",
            SetMultimap::class
        )
        invokevirtual(
            "club/sk1er/patcher/discovery/DataTableSearch",
            "getAnnotationsFor",
            SetMultimap::class,
            "net/minecraftforge/fml/common/ModContainer",
            List::class,
            SetMultimap::class
        )
        areturn
    }.first
}