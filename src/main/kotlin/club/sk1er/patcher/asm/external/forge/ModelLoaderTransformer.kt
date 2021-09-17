package club.sk1er.patcher.asm.external.forge

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.aload_0
import codes.som.anthony.koffee.insns.jvm.getfield
import codes.som.anthony.koffee.insns.jvm.invokeinterface
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode

class ModelLoaderTransformer : PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    override fun getClassName(): Array<String> {
        return arrayOf("net.minecraftforge.client.model.ModelLoader")
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.forEach {
            if (it.name == "onPostBakeEvent") {
                for (insn in it.instructions) {
                    if (insn is FieldInsnNode && insn.name == "isLoading") {
                        it.instructions.insertBefore(insn.previous, clearMemory())
                        break
                    }
                }
            }
        }
    }

    private fun clearMemory() = assembleBlock {
        aload_0
        getfield("net/minecraftforge/client/model/ModelLoader", "loadingExceptions", Map::class)
        invokeinterface(Map::class, "clear", void)
        aload_0
        getfield("net/minecraftforge/client/model/ModelLoader", "missingVariants", Set::class)
        invokeinterface(Set::class, "clear", void)
    }.first
}