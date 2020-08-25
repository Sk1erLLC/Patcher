package club.sk1er.patcher.asm

import club.sk1er.patcher.asm.utils.injectInstructions
import club.sk1er.patcher.hooks.ItemLayerModelHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.tree.ClassNode

class ItemLayerModelTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraftforge.client.model.ItemLayerModel")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first { it.name == "getQuadsForSprite" }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(ItemLayerModelHook::getQuadsForSprite)
                into(this@apply)
                params(1, 2, 3, 4)
            }
        }
    }
}