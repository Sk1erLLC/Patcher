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

import club.sk1er.hookinjection.getInstructions
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
                                        "${hooksPackage}GuiPlayerTabOverlayHook",
                                        "getNewColor",
                                        int,
                                        int
                                    )
                                }.first)
                            }
                        } else if (insn is FieldInsnNode && insn.opcode == Opcodes.GETSTATIC) {
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

                    it.instructions.insertBefore(it.instructions.first, moveDownInstructions(it, true))
                    it.instructions.insertBefore(it.instructions.last.previous, moveDownInstructions(it, false))
                }

                "drawPing", "func_175245_a" -> {
                    it.instructions.insertBefore(it.instructions.first, createNumberPing(it))
                }
            }
        }
    }

    private fun addPlayerHat(goto: LabelNode, ifeq: LabelNode): InsnList {
        val list = InsnList()
        val ifnonnull = LabelNode()
        list.add(JumpInsnNode(Opcodes.IFNONNULL, ifnonnull))
        list.add(FieldInsnNode(Opcodes.GETSTATIC, patcherConfigClass, "layersInTab", "Z"))
        list.add(JumpInsnNode(Opcodes.IFEQ, ifeq))
        list.add(JumpInsnNode(Opcodes.GOTO, goto))
        list.add(ifnonnull)
        return list
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
            keepReturns
        })
        list.add(ifeq)
        return list
    }
}