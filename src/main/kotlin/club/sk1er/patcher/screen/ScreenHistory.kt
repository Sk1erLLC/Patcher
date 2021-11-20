package club.sk1er.patcher.screen

import club.sk1er.patcher.Patcher
import club.sk1er.patcher.mixins.accessors.AbstractClientPlayerAccessor
import club.sk1er.patcher.mixins.accessors.NetworkPlayerInfoAccessor
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.name.NameFetcher
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.mojang.Model
import gg.essential.api.utils.mojang.Profile
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.UIRoundedRectangle.Companion.drawRoundedRectangle
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.*
import gg.essential.vigilance.gui.VigilancePalette
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.util.*

class ScreenHistory @JvmOverloads constructor(
    val name: String? = null,
    focus: Boolean = name == null
) : WindowScreen(newGuiScale = GuiScale.scaleForScreenSize().ordinal) {
    private val nameFetcher = NameFetcher()
    private var uuid: UUID? = null
    private val skin = initialSkin

    private val background by UIRoundedRectangle(5f).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        height = 75.percent()
        width = 50.percent()
        color = VigilancePalette.getBackground().toConstraint()
    } effect ScissorEffect() childOf window

    private val textHolder by UIBlock(VigilancePalette.getDarkHighlight()).constrain {
        x = CenterConstraint()
        y = 10.pixels()
        width = 80.percent()
        height = ChildBasedSizeConstraint() + 6.pixels()
    } childOf background effect OutlineEffect(VigilancePalette.getDivider(), 0.75f)

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
        color = VigilancePalette.getDarkHighlight().toConstraint()
    } childOf background

    private val skinText by UIText("Skin of $name").constrain {
        x = CenterConstraint()
        y = 1.pixel()
        color = VigilancePalette.getBrightText().toConstraint()
    } childOf playerHolder

    // todo: replace this with the essential player object
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

    private val applySkinButton by BigButton(true, "Apply this Skin!") {
        ChangeSkinConfirmationModal() childOf window
    }.constrain {
        width = 80.percent()
        height = 100.percent()
        x = CenterConstraint()
        y = 0.pixels()
    } childOf buttonContainer

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

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (uuid != nameFetcher.uuid) {
            uuid = nameFetcher.uuid
            // update gui info
            val uuid = uuid
            if (uuid == null) {
                // set blank
                player.player = FakePlayer(defaultProfile)
            } else {
                // uuid is the only thing that matters here
                player.player = FakePlayer(GameProfile(uuid, name))
                val info = (player.player as AbstractClientPlayerAccessor).playerInfo
                if (info != null) {
                    val profile = EssentialAPI.getMojangAPI().getProfile(uuid)
                    if (profile != null) {
                        val url = profile.textures.textures?.skin?.url
                        if (url != null) {
                            val alex = profile.isAlex()
                            val rl = mc.skinManager.loadSkin(
                                MinecraftProfileTexture(
                                    url,
                                    if (alex) mapOf("model" to "slim") else null
                                ), MinecraftProfileTexture.Type.SKIN
                            )
                            ((player.player as AbstractClientPlayerAccessor).playerInfo as NetworkPlayerInfoAccessor).setLocationSkin(rl)
                            skin.take(Triple(rl, url, if (alex) Model.ALEX else Model.STEVE))
                            skinText.setText("Skin of ${nameFetcher.name}")
                        }
                    }
                }
            }
        }
        super.onDrawScreen(mouseX, mouseY, partialTicks)
    }

    override fun onTick() {
        val diff = System.currentTimeMillis() - lastSkinChange
        if (diff < 60000L) {
            if (applySkinButton.enabled) {
                applySkinButton.disable()
            }

            applySkinButton.setText("Wait ${60 - (diff / 1000L)}s...")
        } else if (!applySkinButton.enabled) {
            applySkinButton.enable()
            applySkinButton.setText("Apply this Skin!")
        }
        super.onTick()
    }

    private fun getNameHistory(username: String) {
        nameFetcher.execute(username)
    }

    private fun Profile.isAlex(): Boolean {
        try {
            val decodedJson =
                JsonParser().parse(String(Base64.getDecoder().decode(properties?.get(0)?.value))).asJsonObject
            val skin = decodedJson["textures"]!!.asJsonObject["SKIN"]!!.asJsonObject
            if (skin.has("metadata")) {
                val metadataElement = skin["metadata"]
                if (metadataElement.isJsonObject) {
                    val metadata = metadataElement.asJsonObject
                    if (metadata.has("model")) {
                        return metadata["model"].asString == "slim"
                    }
                }
            }
            return false
        } catch (e: Exception) {
            Patcher.instance.logger.error("Failed to determine if model was of Alex format.", e)
            return false
        }
    }

    override fun doesGuiPauseGame(): Boolean = false

    private inner class UIPlayer : UIComponent() {
        val uuid: UUID? = try {
            EssentialAPI.getMojangAPI().getUUID(name ?: "")?.get()
        } catch (e: Exception) {
            Patcher.instance.logger.error("Failed to fetch UUID.", e)
            EssentialAPI.getNotifications().push("Name History", "Failed to fetch UUID.")
            null
        }
        var player: FakePlayer = FakePlayer(GameProfile(uuid ?: defaultProfile.id, name))

        override fun draw() {
            UGraphics.GL.pushMatrix()
            UGraphics.enableLighting()
            UGraphics.enableAlpha()
            UGraphics.shadeModel(GL11.GL_FLAT)
            UGraphics.enableDepth()
            UGraphics.GL.translate(0f, 0f, 200f)

            val posX = getLeft().toInt()
            val posY = getTop().toInt()
            val h = (getHeight().toInt() / 2)
            val w = getWidth().toInt()
            val scale2 = if (h > w) w else h

            val posX2 = UResolution.scaledWidth
            val posY2 = UResolution.scaledHeight

            val yaw = posX2 - UMouse.getTrueX()
            val pitch = -(posY2 - UMouse.getTrueY())

            GuiInventory.drawEntityOnScreen(
                posX + scale2 / 2,
                posY + scale2 * 2,
                scale2,
                yaw.toFloat(),
                pitch.toFloat(),
                player
            )

            UGraphics.depthFunc(GL11.GL_LEQUAL)
            UGraphics.GL.popMatrix()

            super.draw()
        }
    }

    private inner class HistoryComponent : UIComponent() {
        override fun draw() {
            UGraphics.GL.pushMatrix()

            val maxHeight = 12f + (nameFetcher.names.size * 10)
            if (getHeight() != maxHeight) {
                setHeight(maxHeight.pixels())
            }

            drawRoundedRectangle(getLeft(), getTop(), getRight(), getTop() + maxHeight, 3f,
                VigilancePalette.getDarkHighlight()
            )

            val x = (getWidth() / 2 + getLeft()).toInt()
            drawCenteredString(
                fontRendererObj, "Name History", x, getTop().toInt() + 2, VigilancePalette.getBrightText().rgb
            )
            for (nameIndex in nameFetcher.names.indices) {
                drawCenteredString(
                    fontRendererObj,
                    nameFetcher.names[nameIndex],
                    x,
                    getTop().toInt() + 2 + (10 * (nameIndex + 1)),
                    VigilancePalette.getBrightText().rgb
                )
            }

            UGraphics.GL.popMatrix()
            super.draw()
        }
    }

    private inner class BigButton(
        var enabled: Boolean,
        buttonText: String,
        buttonAction: () -> Unit
    ) : UIRoundedRectangle(5f) {
        private val buttonTextState = BasicState(buttonText)

        init {
            constrain {
                color = VigilancePalette.getAccent().toConstraint()
            }
        }

        private val buttonBody by UIRoundedRectangle(5f).constrain {
            width = basicWidthConstraint { this@BigButton.getWidth() - 4 }
            height = basicHeightConstraint { this@BigButton.getHeight() - 4 }
            y = CenterConstraint()
            x = CenterConstraint()
            color = VigilancePalette.getDarkHighlight().toConstraint()
        } childOf this

        private val buttonTextComponent by UIText().bindText(buttonTextState).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = VigilancePalette.getBrightText().toConstraint()
        } childOf this

        init {
            onMouseEnter {
                if (enabled) {
                    buttonBody.animate {
                        setColorAnimation(Animations.OUT_EXP, .3f, VigilancePalette.getAccent().toConstraint())
                    }
                }
            }.onMouseLeave {
                buttonBody.animate {
                    setColorAnimation(Animations.OUT_EXP, .3f, VigilancePalette.getDarkHighlight().toConstraint())
                }
            }.onMouseClick {
                if (enabled) {
                    USound.playSoundStatic(ResourceLocation("gui.button.press"), .25f, 1f)
                    buttonAction()
                }
            }
        }

        fun setText(newButtonText: String): Unit = buttonTextState.set(newButtonText)

        fun enable() {
            enabled = true
            if (buttonBody.isHovered()) {
                buttonBody.setColor(VigilancePalette.getAccent().toConstraint())
            }
            buttonTextComponent.setColor(VigilancePalette.getBrightText().toConstraint())
            setColor(VigilancePalette.getAccent().toConstraint())
        }

        fun disable() {
            enabled = false
            buttonBody.setColor(VigilancePalette.getDarkHighlight().toConstraint())
            buttonTextComponent.setColor(VigilancePalette.getMidText().toConstraint())
            setColor(VigilancePalette.getHighlight().toConstraint())
        }
    }

    private inner class ChangeSkinConfirmationModal : UIBlock(VigilancePalette.getModalBackground().withAlpha(0)) {
        init {
            constrain {
                x = 0.pixels()
                y = 0.pixels()
                height = 100.percent()
                width = 100.percent()
            }

            onMouseClick {
                closeModal()
            }
        }

        private val actualModal by UIBlock(VigilancePalette.getDarkBackground()).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            height = 1.pixel()
            width = 1.pixel()
        }.onMouseClick { e -> e.stopPropagation() } effect ScissorEffect() childOf this

        init {
            UIText("Are you sure?").constrain {
                color = VigilancePalette.getBrightText().toConstraint()
                x = CenterConstraint()
                y = 25.percent()
                textScale = 1.2f.pixels()
            } childOf actualModal

            UIWrappedText("You can only change your skin once per minute!", centered = true).constrain {
                color = VigilancePalette.getMidText().toConstraint()
                width = 70.percent()
                x = CenterConstraint()
                y = SiblingConstraint(3f)
            } childOf actualModal
        }

        val modalBigButtonContainer by UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            height = basicHeightConstraint { buttonContainer.getHeight() }
            width = basicWidthConstraint { buttonContainer.getWidth() }
        } childOf actualModal

        init {
            BigButton(true, "Yes, I'm sure!") {
                try {
                    EssentialAPI.getMojangAPI()
                        .changeSkin(mc.session.token, mc.thePlayer.uniqueID, skin.model, skin.url)
                } catch (e: Exception) {
                    ChatUtilities.sendNotification("Name History", "Failed to change your skin.")
                    Patcher.instance.logger.error("Failed to change players skin through name history.", e)
                    closeModal()
                    return@BigButton
                }

                ChatUtilities.sendNotification("Name History", "Successfully changed your skin!")
                lastSkinChange = System.currentTimeMillis()
                closeModal()
            }.constrain {
                width = 80.percent()
                height = 100.percent()
                x = CenterConstraint()
                y = 0.pixels()
            } childOf modalBigButtonContainer

            UIText("Wait, go back!").constrain {
                x = CenterConstraint()
                y = SiblingConstraint(5f)
                color = VigilancePalette.getMidText().toConstraint()
            }.onMouseEnter {
                animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, VigilancePalette.getMidText().brighter().toConstraint())
                }
            }.onMouseLeave {
                animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, VigilancePalette.getMidText().toConstraint())
                }
            }.onMouseClick {
                closeModal()
            } childOf actualModal
        }

        private fun closeModal() {
            animate {
                setColorAnimation(
                    Animations.OUT_EXP,
                    .5f,
                    VigilancePalette.getModalBackground().withAlpha(0).toConstraint()
                )
            }

            actualModal.animate {
                setHeightAnimation(Animations.OUT_EXP, .25f, 1.pixel()).onComplete {
                    actualModal.animate {
                        setWidthAnimation(Animations.OUT_EXP, .25f, 1.pixel()).onComplete {
                            window.removeChild(this@ChangeSkinConfirmationModal)
                        }
                    }
                }
            }
        }

        override fun afterInitialization() {
            animate {
                setColorAnimation(Animations.OUT_EXP, .5f, VigilancePalette.getModalBackground().toConstraint())
            }

            actualModal.animate {
                setWidthAnimation(Animations.OUT_EXP, .25f, 35.percent()).onComplete {
                    actualModal.animate {
                        setHeightAnimation(Animations.OUT_EXP, .25f, 30.percent())
                    }
                }
            }
            super.afterInitialization()
        }

        override fun draw() {
            // I hate this.
            UGraphics.GL.pushMatrix()
            UGraphics.GL.translate(0f, 0f, 500f)
            super.draw()
            UGraphics.GL.popMatrix()
        }
    }

    private class FakePlayer(gameProfile: GameProfile) : AbstractClientPlayer(UMinecraft.getWorld(), gameProfile) {
        init {
            @Suppress("CAST_NEVER_SUCCEEDS")
            (this as AbstractClientPlayerAccessor).playerInfo = NetworkPlayerInfo(gameProfile)
        }
    }

    private data class SelectableSkin(
        var resourceLocation: ResourceLocation,
        var url: String,
        var model: Model
    ) {
        fun take(triple: Triple<ResourceLocation, String, Model>) {
            resourceLocation = triple.first
            url = triple.second
            model = triple.third
        }
    }

    companion object {
        private val defaultProfile: GameProfile =
            GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve")
        private val initialSkin: SelectableSkin = SelectableSkin(
            DefaultPlayerSkin.getDefaultSkinLegacy(),
            "https://textures.minecraft.net/texture/1a4af718455d4aab528e7a61f86fa25e6a369d1768dcb13f7df319a713eb810b",
            Model.STEVE
        )
        private var lastSkinChange: Long = 0L
    }
}
