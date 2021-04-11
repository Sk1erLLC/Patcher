/*
 * Copyright © 2021 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.screen

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.input.UITextInput
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.*
import club.sk1er.patcher.Patcher
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.name.NameFetcher
import club.sk1er.vigilance.gui.VigilancePalette
import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import me.kbrewster.mojangapi.MojangAPI
import me.kbrewster.mojangapi.profile.Model
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.util.*

class ScreenHistory @JvmOverloads constructor(
    name: String? = null,
    focus: Boolean = name == null
) : WindowScreen() {
    private val nameFetcher = NameFetcher()
    private var uuid: UUID? = null
    private val skin = initialSkin

    private val background by UIRoundedRectangle(5f).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        height = 75.percent()
        width = 50.percent()
        color = VigilancePalette.BACKGROUND.toConstraint()
    } childOf window

    private val textHolder by UIBlock(VigilancePalette.DARK_HIGHLIGHT).constrain {
        x = CenterConstraint()
        y = 10.pixels()
        width = 80.percent()
        height = ChildBasedSizeConstraint() + 6.pixels()
    } childOf background effect OutlineEffect(VigilancePalette.DIVIDER, 0.75f)

    private val textInput by UITextInput("Enter a name").constrain {
        x = 3.pixels()
        y = 3.pixels()
        width = basicWidthConstraint { textHolder.getWidth() }
    }.onMouseClick {
        grabWindowFocus()
    }.onKeyType { _, keyCode ->
        if (keyCode == UKeyboard.KEY_ENTER) {
            getNameHistory((this as UITextInput).getText())
        }
    } as UITextInput

    init {
        textInput childOf textHolder

        if (name != null) {
            textInput.setText(name)
            getNameHistory(name)
        }

        if (focus) {
            textInput.grabWindowFocus()
        }
    }

    private val playerHolder by UIRoundedRectangle(3f).constrain {
        y = CenterConstraint() - 5.pixels()
        width = 33.percent()
        x = 7.percent()
        height = 70.percent()
        color = VigilancePalette.DARK_HIGHLIGHT.toConstraint()
    } childOf background

    private val skinText by UIText("Skin of $name").constrain {
        x = CenterConstraint()
        y = 1.pixel()
        color = VigilancePalette.BRIGHT_TEXT.toConstraint()
    } childOf playerHolder

    private val player by UIPlayer().constrain {
        y = CenterConstraint()
        x = CenterConstraint()
        height = 75.percent()
        width = 75.percent()
    } childOf playerHolder

    private val buttonContainer by UIContainer().constrain {
        x = basicXConstraint { playerHolder.getLeft() }
        y = basicYConstraint { playerHolder.getBottom() + 3 }
        width = basicWidthConstraint { playerHolder.getWidth() }
        height = 7.5f.percent()
    } childOf background

    private val changeSkinButton by UIRoundedRectangle(5f).constrain {
        width = 80.percent()
        height = 100.percent()
        x = CenterConstraint()
        y = 0.pixels()
        color = VigilancePalette.ACCENT.toConstraint()
    } childOf buttonContainer


    private val buttonBody by UIRoundedRectangle(5f).constrain {
        width = basicWidthConstraint { changeSkinButton.getWidth() - 4 }
        height = basicHeightConstraint { changeSkinButton.getHeight() - 4 }
        y = CenterConstraint()
        x = CenterConstraint()
        color = VigilancePalette.DARK_HIGHLIGHT.toConstraint()
    } childOf changeSkinButton

    init {
        UIText("Apply This Skin!").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = VigilancePalette.BRIGHT_TEXT.toConstraint()
        } childOf changeSkinButton

        changeSkinButton.onMouseEnter {
            buttonBody.animate {
                setColorAnimation(Animations.OUT_EXP, .3f, VigilancePalette.ACCENT.toConstraint())
            }
        }.onMouseLeave {
            buttonBody.animate {
                setColorAnimation(Animations.OUT_EXP, .3f, VigilancePalette.DARK_HIGHLIGHT.toConstraint())
            }
        }.onMouseClick {
            try {
                // TODO take in the players model type as well instead of just steve
                MojangAPI.changeSkin(mc.session.token, mc.thePlayer.uniqueID, Model.STEVE, skin.url)
            } catch (e: Exception) {
                ChatUtilities.sendNotification("Name History", "Failed to change your skin.")
                Patcher.instance.logger.error("Failed to change players skin through name history.", e)
                return@onMouseClick
            }

            ChatUtilities.sendNotification("Name History", "Successfully changed your skin!")
        }
    }

    private val historyScroller by ScrollComponent().constrain {
        x = 50.percent()
        y = basicYConstraint { playerHolder.getTop() }
        height = basicHeightConstraint { playerHolder.getHeight() }
        width = "WWWWWWWWWWWWWWWW » 99/99/9999".width().pixels()
    } childOf background

    init {
        HistoryComponent().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
        } childOf historyScroller
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (uuid != nameFetcher.uuid) {
            uuid = nameFetcher.uuid
            // update gui info
            if (uuid == null) {
                // set blank
                player.player = FakePlayer(downloadPatcher)
            } else {
                // uuid is the only thing that matters here
                player.player = FakePlayer(GameProfile(uuid, "DownloadPatcher"))
                val info = player.player.playerInfo
                if (info != null) {
                    val url = MojangAPI.getProfile(uuid).textures.textures.skin.url
                    println(url)
                    val rl = mc.skinManager.loadSkin(MinecraftProfileTexture(url, null), MinecraftProfileTexture.Type.SKIN)
                    player.player.playerInfo.locationSkin = rl
                    skin.take(rl to url)
                    skinText.setText("Skin of ${nameFetcher.name}")
                }
            }
        }
        super.onDrawScreen(mouseX, mouseY, partialTicks)
    }

    private fun getNameHistory(username: String) {
        nameFetcher.execute(username)
    }

    override fun doesGuiPauseGame(): Boolean = false

    private inner class UIPlayer : UIComponent() {
        var player: FakePlayer = FakePlayer(downloadPatcher)

        override fun draw() {
            UGraphics.pushMatrix()
            UGraphics.enableLighting()
            UGraphics.enableAlpha()
            UGraphics.shadeModel(GL11.GL_FLAT)
            UGraphics.enableDepth()
            UGraphics.translate(0f, 0f, 200f)

            val posX = getLeft().toInt()
            val posY = getTop().toInt()
            val h = (getHeight().toInt() / 2)
            val w = getWidth().toInt()
            val scale2 = if (h > w) w else h

            val posX2 = UResolution.scaledWidth
            val posY2 = UResolution.scaledHeight

            val yaw = posX2 - UMouse.getTrueX()
            val pitch = -(posY2 - UMouse.getTrueY())

            GuiInventory.drawEntityOnScreen(posX + scale2 / 2, posY + scale2 * 2, scale2, yaw.toFloat(), pitch.toFloat(), player)

            UGraphics.depthFunc(GL11.GL_LEQUAL)
            UGraphics.popMatrix()

            super.draw()
        }
    }

    private inner class HistoryComponent : UIComponent() {
        override fun draw() {
            UGraphics.pushMatrix()

            val i = 12f + (nameFetcher.names.size * 10)
            if (getHeight() != i) {
                setHeight(i.pixels())
            }

            UIRoundedRectangle.drawRoundedRectangle(
                getLeft(),
                getTop(),
                getRight(),
                getTop() + i,
                3f,
                VigilancePalette.DARK_HIGHLIGHT
            )

            val j = (getWidth() / 2 + getLeft()).toInt()
            drawCenteredString(
                fontRendererObj, "Name History", j, getTop().toInt() + 2, VigilancePalette.BRIGHT_TEXT.rgb
            )
            for (k in nameFetcher.names.indices) {
                drawCenteredString(
                    fontRendererObj,
                    nameFetcher.names[k],
                    j,
                    getTop().toInt() + 2 + (10 * (k + 1)),
                    VigilancePalette.BRIGHT_TEXT.rgb
                )
            }

            UGraphics.popMatrix()

            super.draw()
        }
    }

    private class FakePlayer(gameProfile: GameProfile) : AbstractClientPlayer(UMinecraft.getWorld(), gameProfile) {
        init {
            playerInfo = NetworkPlayerInfo(gameProfile)
        }

        override fun getPlayerInfo(): NetworkPlayerInfo = playerInfo
    }

    private data class SelectableSkin(var resourceLocation: ResourceLocation, var url: String) {
        fun take(pair: Pair<ResourceLocation, String>) {
            resourceLocation = pair.first
            url = pair.second
        }
    }

    companion object {
        private val downloadPatcher: GameProfile = GameProfile(UUID.fromString("ec6081b2-19d0-47d0-8c67-be166f4dec5e"), "DownloadPatcher")
        private val initialSkin: SelectableSkin = SelectableSkin(DefaultPlayerSkin.getDefaultSkinLegacy(), "https://textures.minecraft.net/texture/1a4af718455d4aab528e7a61f86fa25e6a369d1768dcb13f7df319a713eb810b")
    }
}