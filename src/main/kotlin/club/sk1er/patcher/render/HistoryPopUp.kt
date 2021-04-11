package club.sk1er.patcher.render

import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.UResolution
import club.sk1er.patcher.screen.ScreenHistory
import club.sk1er.patcher.util.name.NameFetcher
import club.sk1er.vigilance.gui.VigilancePalette
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.modcore.api.ModCoreAPI
import net.modcore.api.utils.Multithreading
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
        Multithreading.runAsync(Runnable {
            val nf = NameFetcher()
            nf.execute(player, false)
            fetchers.add(nf)
        })
    }

    private class PopUp(fetcher: NameFetcher) : UIBlock(VigilancePalette.DARK_BACKGROUND) {
        init {
            if (fetcher.uuid != null) {
                constrain {
                    x = 10.pixels(true)
                    y = SiblingConstraint(12f)
                    height = ChildBasedSizeConstraint()
                    width = 150.pixels()
                }

                this effect OutlineEffect(VigilancePalette.ACCENT, 2f)

                UIImage.ofURL(URL("https://cravatar.eu/helmavatar/${fetcher.uuid.toString().replace("-", "")}")).constrain {
                    x = 3.pixels()
                    y = 3.pixels()
                    height = 15.pixels()
                    width = 15.pixels()
                } childOf this

                UIText(fetcher.name, false).constrain {
                    x = 21.pixels()
                    y = 3.pixels()
                    textScale = 1.1f.pixels()
                    color = VigilancePalette.BRIGHT_TEXT.toConstraint()
                } childOf this

                fun mkText(str: String, b: Boolean = false): UIText = UIText(str, false).constrain {
                    x = CenterConstraint()
                    y = SiblingConstraint(if (b) 3f else 1f)
                    color = VigilancePalette.BRIGHT_TEXT.toConstraint()
                }

                fetcher.names.reverse()
                for (i in fetcher.names.indices) {
                    if (i < 5) {
                        mkText(fetcher.names[i], i == 0) childOf this
                    } else {
                        mkText("...") childOf this
                        break
                    }
                }

                onMouseClick {
                    // todo add thing that shows what each mouse button does
                    when (it.mouseButton) {
                        0 -> {
                            ModCoreAPI.getGuiUtil().openScreen(ScreenHistory(fetcher.name))
                            window.removeChild(this@PopUp)
                        }
                        1 -> {
                            // todo animate this
                            window.removeChild(this@PopUp)
                        }
                        else -> window.removeChild(this@PopUp)
                    }
                }
            }

            timer(5000L) {
                window.removeChild(this)
            }
        }
    }
}