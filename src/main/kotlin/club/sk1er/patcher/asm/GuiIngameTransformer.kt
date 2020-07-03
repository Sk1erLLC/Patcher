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

import club.sk1er.patcher.tweaker.ClassTransformer
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

class GuiIngameTransformer : PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    override fun getClassName() = arrayOf("net.minecraft.client.gui.GuiIngame")

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    override fun transform(classNode: ClassNode, name: String) {
        for (method in classNode.methods) {
            when (mapMethodName(classNode, method)) {
                "showCrosshair", "func_175183_b" -> {
                    method.instructions.insertBefore(method.instructions.first, disableCrosshairRendering())
                }
                "renderScoreboard", "func_180475_a" -> {
                    if (ClassTransformer.optifineVersion == "L5" || ClassTransformer.optifineVersion == "NONE") {
                        var foundOneDrawRect = false
                        for (node in method.instructions) {
                            if (node.opcode == Opcodes.ICONST_0 && node.next.opcode == Opcodes.ISTORE) {
                                method.instructions.insert(node.next, assembleBlock {
                                    iload(10) // l1
                                    iconst_2
                                    isub
                                    iload(8) // j1
                                    aload(4) // collection
                                    invokeinterface(Collection::class, "size", int)
                                    aload_0
                                    invokevirtual(
                                        "net/minecraft/client/gui/GuiIngame", "func_175179_f", //getFontRenderer
                                        "net/minecraft/client/gui/FontRenderer"
                                    )
                                    getfield(
                                        "net/minecraft/client/gui/FontRenderer", "field_78288_b", //FONT_HEIGHT
                                        int
                                    )
                                    imul
                                    isub
                                    iconst_1
                                    isub
                                    aload_2
                                    invokevirtual(
                                        "net/minecraft/client/gui/ScaledResolution", "func_78326_a", //getScaledWidth
                                        int
                                    )
                                    iload(9) // k1
                                    isub
                                    iconst_2
                                    iadd
                                    iload(8) // j1
                                    ldc(1342177280)
                                    invokestatic(
                                        "net/minecraft/client/gui/Gui", "func_73734_a", //drawRect
                                        void, int, int, int, int, int
                                    )
                                }.first)
                            } else if (node is MethodInsnNode && node.name in listOf("drawRect", "func_73734_a")) {
                                if (!foundOneDrawRect) {
                                    foundOneDrawRect = true
                                    continue
                                }
                                while (node.previous.opcode != Opcodes.ISTORE) {
                                    method.instructions.remove(node.previous)
                                }
                                method.instructions.remove(node)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun disableCrosshairRendering() = assembleBlock {
        getstatic(patcherConfigClass, "crosshairPerspective", boolean)
        ifeq(L["1"])
        aload_0
        getfield("net/minecraft/client/gui/GuiIngame", "field_73839_d", "net/minecraft/client/Minecraft")
        getfield("net/minecraft/client/Minecraft", "field_71474_y", "net/minecraft/client/settings/GameSettings")
        getfield("net/minecraft/client/settings/GameSettings", "field_74320_O", int)
        ifne(L["2"])
        +L["1"]
        getstatic(patcherConfigClass, "inventoryCrosshair", boolean)
        ifeq(L["3"])
        aload_0
        getfield("net/minecraft/client/gui/GuiIngame", "field_73839_d", "net/minecraft/client/Minecraft")
        getfield("net/minecraft/client/Minecraft", "field_71462_r", "net/minecraft/client/gui/GuiScreen")
        instanceof("net/minecraft/client/gui/inventory/GuiContainer")
        ifeq(L["3"])
        +L["2"]
        iconst_0
        ireturn
        +L["3"]
    }.first
}