package club.sk1er.patcher.asm

import club.sk1er.patcher.asm.utils.getInstructions
import club.sk1er.patcher.hooks.GuiMultiplayerHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.tree.ClassNode

class GuiMultiplayerTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.gui.GuiMultiplayer")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "keyTyped" || methodName == "func_73869_a"
        }?.apply {
            instructions.insertBefore(instructions.first, getInstructions {
                of(GuiMultiplayerHook::keyTyped)
                target(this@apply)
                before(this@apply.instructions.first)
                param(0)
            })
        }
    }
}