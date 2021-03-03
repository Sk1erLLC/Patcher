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

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import org.objectweb.asm.tree.ClassNode

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
        classNode.methods.forEach {
            when (mapMethodName(classNode, it)) {
                "showCrosshair", "func_175183_b" -> it.instructions.insert(disableCrosshairRendering())
            }
        }
    }

    private fun disableCrosshairRendering() = assembleBlock {
        aload_0
        getfield("net/minecraft/client/gui/GuiIngame", "field_73839_d", "net/minecraft/client/Minecraft")
        getfield("net/minecraft/client/Minecraft", "field_71462_r", "net/minecraft/client/gui/GuiScreen")
        ifnull(L["1"])
        getstatic(patcherConfigClass, "guiCrosshair", boolean)
        ifne(L["2"])
        +L["1"]
        aload_0
        getfield("net/minecraft/client/gui/GuiIngame", "field_73839_d", "net/minecraft/client/Minecraft")
        getfield("net/minecraft/client/Minecraft", "field_71474_y", "net/minecraft/client/settings/GameSettings")
        getfield("net/minecraft/client/settings/GameSettings", "field_74320_O", int)
        ifeq(L["3"])
        getstatic(patcherConfigClass, "crosshairPerspective", boolean)
        ifeq(L["3"])
        +L["2"]
        iconst_0
        ireturn
        +L["3"]
    }.first
}
