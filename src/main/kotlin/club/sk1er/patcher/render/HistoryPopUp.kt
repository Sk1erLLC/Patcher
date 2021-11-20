package club.sk1er.patcher.render

import club.sk1er.patcher.screen.ScreenHistory
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.name.NameFetcher
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.UResolution
import gg.essential.universal.USound
import gg.essential.vigilance.gui.VigilancePalette
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Mouse
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

object HistoryPopUp {
    private val window = Window()
    private val fetchers = ConcurrentLinkedQueue<NameFetcher>()

    init {
        UIContainer() childOf window
    }

    @SubscribeEvent
    fun render(event: RenderGameOverlayEvent.Post) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && Minecraft.getMinecraft().currentScreen !is ScreenHistory) {
            window.draw()
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val x = fetchers.poll() ?: return
            PopUp(x) childOf window
        }
    }

    @SubscribeEvent
    fun onClick(event: GuiScreenEvent.MouseInputEvent.Post) {
        val mouseX = Mouse.getEventX() * event.gui.width / UResolution.windowWidth
        val mouseY = event.gui.height - Mouse.getEventY() * event.gui.height / UResolution.windowHeight - 1
        val eventButton = Mouse.getEventButton()
        if (Mouse.getEventButtonState()) {
            window.mouseClick(mouseX.toDouble(), mouseY.toDouble(), eventButton)
        } else if (eventButton != -1) {
            window.mouseRelease()
        }
    }

    fun addPopUp(player: String) {
        Multithreading.runAsync {
            val nf = NameFetcher()
            nf.execute(player, false)
            if (nf.uuid != null) {
                fetchers.add(nf)
            } else {
                ChatUtilities.sendMessage("&cFailed to get name history of $player... is this a real player?")
            }
        }
    }

    private class PopUp(val fetcher: NameFetcher) : UIBlock(VigilancePalette.getDarkBackground()) {
        private val img by UIImage.ofURL(URL("https://cravatar.eu/helmavatar/${fetcher.uuid.toString().replace("-", "")}")).constrain {
            x = 5.percent()
            y = CenterConstraint()
            width = 25.percent()
            height = basicHeightConstraint { it.getWidth() }
        }
        private val timerBar by UIBlock(VigilancePalette.getAccent()).constrain {
            x = 0.pixels()
            y = 0.pixels(true)
            height = 2.pixels()
            width = 0.percent()
        }
        private var animatingOut: Boolean = false

        init {
            fun animateOut() {
                if (!animatingOut) {
                    animatingOut = true
                    animate {
                        setXAnimation(Animations.OUT_EXP, 1f, 0.pixels(alignOpposite = true, alignOutside = true)).onComplete {
                            window.removeChild(this@PopUp)
                        }
                    }
                }
            }

            if (fetcher.uuid != null) {
                constrain {
                    x = 10.pixels(true)
                    y = SiblingConstraint(10f)
                    height = 80.pixels()
                    width = 200.pixels()
                }

                img childOf this

                onMouseClick {
                    // todo add thing that shows what each mouse button does
                    USound.playSoundStatic(ResourceLocation("gui.button.press"), .25f, 1f)
                    when (it.mouseButton) {
                        0 -> {
                            EssentialAPI.getGuiUtil().openScreen(ScreenHistory(fetcher.name))
                            window.removeChild(this@PopUp)
                        }
                        1 -> animateOut()
                        else -> if (!animatingOut) {
                            window.removeChild(this@PopUp)
                        }
                    }
                }

                timerBar childOf this
            }

            timer(5000L) {
                animateOut()
            }
        }

        override fun afterInitialization() {
            UIText(fetcher.name, false).constrain {
                x = 35.percent()
                y = basicYConstraint { img.getTop() }
                textScale = 1.1f.pixels()
                color = VigilancePalette.getBrightText().toConstraint()
            } childOf this

            var j = 9 * 1.1f + 2

            fun mkText(str: String, i: Int): UIText = UIText(str, false).constrain {
                x = 35.percent()
                y = SiblingConstraint(if (i == 0) 3f else 1f)
                textScale = .8f.pixels()
                color = when (i) {
                    0 -> VigilancePalette.getBrightText()
                    1 -> VigilancePalette.getMidText()
                    else -> VigilancePalette.getDarkText()
                }.toConstraint()
            }

            fetcher.names.reverse()
            for (i in fetcher.names.indices) {
                if (i < 5) {
                    mkText(fetcher.names[i], i) childOf this
                    j += 9.1f
                } else {
                    mkText("...", 4) childOf this
                    break
                }
            }

            timerBar.animate {
                setWidthAnimation(Animations.LINEAR, 5f, 100.percent())
            }

            super.afterInitialization()
        }
    }
}