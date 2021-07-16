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
import codes.som.anthony.koffee.insns.jvm.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class GuiPlayerTabOverlayTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.gui.GuiPlayerTabOverlay")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.forEach {
            when (mapMethodName(classNode, it)) {
                "renderPlayerlist", "func_175249_a" -> {
                    it.instructions.iterator().forEach { insn ->
                        if (insn is MethodInsnNode && insn.opcode == Opcodes.INVOKESTATIC) {
                            val methodName = mapMethodNameFromNode(insn)

                            if (methodName == "drawRect" || methodName == "func_73734_a") {
                                it.instructions.insertBefore(insn, assembleBlock {
                                    invokestatic(
                                        getHookClass("GuiPlayerTabOverlayHook"),
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

                    it.instructions.insert(moveDownInstructions(it, true))
                    it.instructions.insertBefore(it.instructions.last.previous, moveDownInstructions(it, false))
                }

                "drawPing", "func_175245_a" -> {
                    it.instructions.iterator().forEach { insn ->
                        if (insn is MethodInsnNode && insn.opcode == Opcodes.INVOKESTATIC) {
                            val methodInsnName = mapMethodNameFromNode(insn)
                            if (methodInsnName == "color" || methodInsnName == "func_179131_c") {
                                it.instructions.insertBefore(insn.previous?.previous?.previous?.previous, createNumberPing())
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

    private fun createNumberPing() = assembleBlock {
        getstatic("club/sk1er/patcher/config/PatcherConfig", "numberPing", boolean)
        ifeq(L["1"])
        iload_1
        iload_2
        iload_3
        aload(4)
        invokestatic(
            getHookClass("GuiPlayerTabOverlayHook"),
            "drawPatcherPing",
            void,
            int,
            int,
            int,
            "net/minecraft/client/network/NetworkPlayerInfo"
        )
        _return
        +L["1"]
    }.first
}