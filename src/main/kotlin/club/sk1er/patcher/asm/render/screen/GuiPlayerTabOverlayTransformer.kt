package club.sk1er.patcher.asm.render.screen

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GuiPlayerTabOverlayTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.gui.GuiPlayerTabOverlay")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.forEach {
            when (mapMethodName(classNode, it)) {
                "renderPlayerlist", "func_175249_a" -> {
                    it.instructions.iterator().forEach { insn ->
                        if (insn is FieldInsnNode && insn.opcode == Opcodes.GETSTATIC) {
                            if (mapFieldNameFromNode(insn) == "HAT"
                                && insn.previous.opcode == Opcodes.ALOAD
                                && (insn.previous as VarInsnNode).`var` == 27
                                && insn.next?.next?.next is LabelNode
                                && insn.next?.next?.opcode == Opcodes.IFEQ
                            ) {
                                it.instructions.insertBefore(
                                    insn.previous?.previous,
                                    addPlayerHat(
                                        (insn.next?.next?.next as LabelNode),
                                        (insn.next?.next as JumpInsnNode).label
                                    )
                                )
                                it.instructions.remove(insn.previous?.previous)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addPlayerHat(goto: LabelNode, ifeq: LabelNode): InsnList {
        val list = InsnList()
        val ifnonnull = LabelNode()
        list.add(JumpInsnNode(Opcodes.IFNONNULL, ifnonnull))
        list.add(getPatcherSetting("layersInTab", "Z"))
        list.add(JumpInsnNode(Opcodes.IFEQ, ifeq))
        list.add(JumpInsnNode(Opcodes.GOTO, goto))
        list.add(ifnonnull)
        return list
    }
}