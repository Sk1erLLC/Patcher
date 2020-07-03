package club.sk1er.patcher.asm

import club.sk1er.patcher.asm.utils.getInstructions
import club.sk1er.patcher.hooks.TexturedQuadHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class TexturedQuadTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.model.TexturedQuad")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "draw" || methodName == "func_178765_a"
        }?.apply {
            clearInstructions(this)
            instructions.insert(draw(this))
        }
    }

    // todo: fix
    private fun draw(methodNode: MethodNode): InsnList {
        val list = InsnList()
        list.add(getInstructions {
            of(TexturedQuadHook::draw)
            target(methodNode)
            params(0, 1, 2)
        })
        return list
    }
}