package club.sk1er.patcher.asm

import club.sk1er.patcher.asm.utils.getInstructions
import club.sk1er.patcher.hooks.GuiPlayerTabOverlayHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.invokestatic
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GuiPlayerTabOverlayTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.gui.GuiPlayerTabOverlay")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.forEach {
            when (mapMethodName(classNode, it)) {
                "renderPlayerlist", "func_175249_a" -> {
                    for (insn in it.instructions.iterator()) {
                        if (insn is MethodInsnNode && insn.opcode == Opcodes.INVOKESTATIC) {
                            val methodName = mapMethodNameFromNode(insn)

                            if (methodName == "drawRect" || methodName == "func_73734_a") {
                                it.instructions.insertBefore(insn, assembleBlock {
                                    invokestatic(
                                        "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                                        "getNewColor",
                                        int,
                                        int
                                    )
                                }.first)
                            }
                        }
                    }



                    it.instructions.insertBefore(it.instructions.first, moveDownInstructions(it, true))
                    it.instructions.insertBefore(it.instructions.last.previous, moveDownInstructions(it, false))
                }

                "drawPing", "func_175245_a" -> {
                    it.instructions.insertBefore(it.instructions.first, createNumberPing(it))
                }
            }
        }
    }

    private fun moveDownInstructions(method: MethodNode, push: Boolean): InsnList {
        val list = InsnList()
        list.add(getInstructions {
            if (push) {
                of(GuiPlayerTabOverlayHook::moveTabDownPushMatrix)
            } else {
                of(GuiPlayerTabOverlayHook::moveTabDownPopMatrix)
            }

            target(method)

            if (push) {
                before(method.instructions.first)
            } else {
                before(method.instructions.last.previous)
            }

            remapReturns
        })
        return list
    }

    private fun createNumberPing(method: MethodNode): InsnList {
        val list = InsnList()
        list.add(FieldInsnNode(Opcodes.GETSTATIC, patcherConfigClass, "numberPing", "Z"))
        val ifeq = LabelNode()
        list.add(JumpInsnNode(Opcodes.IFEQ, ifeq))
        list.add(getInstructions {
            of(GuiPlayerTabOverlayHook::drawPatcherPing)
            target(method)
            before(method.instructions.first)
            params(1, 2, 3, 4)
        })
        list.add(ifeq)
        return list
    }
}