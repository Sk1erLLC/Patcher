package club.sk1er.patcher.screen

import club.sk1er.patcher.util.NameFetcher
import com.mojang.authlib.GameProfile
import gg.essential.api.EssentialAPI
import gg.essential.api.gui.EmulatedPlayerBuilder
import gg.essential.api.utils.Multithreading
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.ObservableClearEvent
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import gg.essential.vigilance.gui.VigilancePalette
import java.awt.Color
import java.util.*

@Suppress("unused", "UNUSED_PARAMETER")
class ScreenHistory @JvmOverloads constructor(
    val name: String? = null,
    focus: Boolean = name == null
) : WindowScreen(newGuiScale = EssentialAPI.getGuiUtil().getGuiScale()) {

    // Sk1erLLC UUID
    private val defaultGameProfile = GameProfile(UUID.fromString("1db6a87e-47fb-47fe-8617-a19c2fd44d75"), "Steve")
    private val nameFetcher = NameFetcher()

    private val blockContainer by UIContainer().constrain {
        y = CenterConstraint()
        width = 100.percent()
        height = 79.percent()
    } childOf window

    private val informationContainer by UIBlock().constrain {
        x = CenterConstraint()
        y = SiblingConstraint()
        width = 57.percent()
        height = FillConstraint()
        color = VigilancePalette.getBackground().toConstraint()
    } effect OutlineEffect(VigilancePalette.getDivider(), 1.0f) childOf blockContainer

    private val contentContainer by UIContainer().constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 100.percent() - 64.pixels()
        height = 100.percent() - 64.pixels()
    } childOf informationContainer

    private val rightContainer by UIContainer().constrain {
        x = 0.pixels(alignOpposite = true)
        y = CenterConstraint()
        width = 48.percent()
        height = 100.percent()
    } childOf contentContainer

    private val playerContainer by UIBlock().constrain {
        x = 0.pixels()
        y = CenterConstraint()
        width = 48.percent()
        height = 100.percent()
        color = VigilancePalette.getDarkHighlight().toConstraint()
    } childOf contentContainer

    private val searchContainer by UIBlock().constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = 9.percent()
        color = VigilancePalette.getDarkHighlight().toConstraint()
    } childOf rightContainer

    private val searchText by UITextInput(placeholder = "Search...", shadow = false).constrain {
        x = 15.pixels()
        y = CenterConstraint()
        width = 100.percent() - 10.pixels()
        height = 18.pixels()
        textScale = 2.pixels()
        color = VigilancePalette.getBrightText().toConstraint()
    }.onMouseClick {
        grabWindowFocus()
    }.onKeyType { _, keyCode ->
        if (keyCode == UKeyboard.KEY_ENTER) {
            nameFetcher.execute((this as UITextInput).getText())
            releaseWindowFocus()
        }
    } as UITextInput

    private val namesContainer by UIBlock().constrain {
        x = 0.pixels()
        y = 0.pixels(alignOpposite = true)
        width = 100.percent()
        height = 89.percent()
        color = VigilancePalette.getDarkHighlight().toConstraint()
    } childOf rightContainer

    private val gameProfileState: State<GameProfile?> = BasicState(defaultGameProfile)

    private val player by EmulatedPlayerBuilder().apply {
        showCape = false
        profileState = gameProfileState
        renderNameTag = false
    }.build().constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        height = 75.percent()
        width = 75.percent()
    } childOf playerContainer

    private val gradient by GradientComponent(
        VigilancePalette.getBackground().withAlpha(0f),
        VigilancePalette.getDarkBackground().withAlpha(.25f),
    ).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf namesContainer

    private val scrollBar by UIBlock(VigilancePalette.getScrollBar()).constrain {
        x = 2.5.pixels(true)
        y = 5.pixels
        width = 3.pixels
        height = 100.percent - 10.pixels
    } childOf namesContainer

    private val names by ScrollComponent().constrain {
        x = 15.pixels
        y = 0.pixels(true)
        width = 100.percent - 15.pixels
        height = 100.percent - 10.pixels
    } childOf namesContainer

    init {
        names.setScrollBarComponent(scrollBar, hideWhenUseless = true, isHorizontal = false)
    }

    override fun afterInitialization() {
        nameFetcher.names.addObserver { _, arg ->
            if (arg is ObservableClearEvent<*>) {
                names.clearChildren()
            }
        }

        nameFetcher.callback = {
            if (nameFetcher.names.isEmpty()) {
                val card = NameCardComponent(
                    VigilancePalette.getMidText(),
                    VigilancePalette.getDarkText(),
                    "Unknown", "Unknown"
                ).constrain {
                    y = SiblingConstraint(2.5f)
                    width = 90.percent
                    height = 20.pixels
                }
                names.insertChildAt(card, 0)
            } else {
                nameFetcher.names.forEachIndexed { i, name ->
                    Window.enqueueRenderOperation {
                        val card = NameCardComponent(
                            if (i == nameFetcher.names.size - 1) VigilancePalette.getAccent() else VigilancePalette.getMidText(),
                            if (i == nameFetcher.names.size - 1) VigilancePalette.getBrightText() else VigilancePalette.getDarkText(),
                            when (i) {
                                0 -> "Original"
                                nameFetcher.names.size - 1 -> "Current"
                                else -> nameFetcher.getDate(i)
                            },
                            name.name!!
                        ).constrain {
                            y = SiblingConstraint(2.5f)
                            width = 90.percent
                            height = 20.pixels
                        }
                        names.insertChildAt(card, 0)
                    }
                }
            }

            Multithreading.runAsync {
                val profile = GameProfile(nameFetcher.uuid ?: defaultGameProfile.id, name)
                UMinecraft.getMinecraft().sessionService.fillProfileProperties(profile, true)
                Window.enqueueRenderOperation { this.gameProfileState.set(profile) }
            }
        }
    }

    init {
        searchText childOf searchContainer

        if (focus) {
            searchText.grabWindowFocus()
        }

        if (name != null) {
            searchText.setText(name)
            nameFetcher.execute(name)
        }

        if (EssentialAPI.getMinecraftUtil().isDevelopment()) {
            Inspector(window).constrain {
                x = 5.pixels(true)
                y = 5.pixels(true)
            } childOf window
        }
    }

    override fun doesGuiPauseGame() = false

    inner class NameCardComponent(
        private val dateBlockColor: Color = VigilancePalette.getMidText(),
        private val dateTextColor: Color = VigilancePalette.getDarkText(),
        dateText: String = "Unknown Date",
        name: String = "Unknown Username"
    ) : UIContainer() {
        private val dateBlock by UIBlock().constrain {
            x = 0.pixels()
            y = CenterConstraint()
            width = 90.pixels()
            height = 20.pixels()
            color = dateBlockColor.toConstraint()
        } childOf this

        private val date by UIText(dateText, shadow = false).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = dateTextColor.toConstraint()
        } childOf dateBlock

        private val username by UIText(name, shadow = false).constrain {
            x = SiblingConstraint(6f)
            y = CenterConstraint()
            color = VigilancePalette.getMidText().toConstraint()
        } childOf this

        init {
            onMouseEnter {
                date.hide(true)
                dateBlock.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }
            }

            onMouseLeave {
                date.unhide(true)
                dateBlock.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 90.pixels()) }
            }
        }
    }
}
