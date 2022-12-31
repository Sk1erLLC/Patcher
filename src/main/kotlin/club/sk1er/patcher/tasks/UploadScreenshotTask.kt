package club.sk1er.patcher.tasks

import club.sk1er.patcher.Patcher
import club.sk1er.patcher.coroutines.MCDispatchers
import club.sk1er.patcher.imgur.Imgur
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.screenshot.AsyncScreenshots
import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import java.io.File

object UploadScreenshotTask {
    private val client = Imgur("649f2fb48e59767")

    fun execute(file: File?) {
        try {
            if (file != null) {
                ChatUtilities.sendNotification("Screenshot Manager", "&aUploading screenshot...")

                MCDispatchers.PATCHER_SCOPE.launch(Dispatchers.IO) {
                    val link = client.upload(file)
                    val message = UTextComponent("${AsyncScreenshots.prefix}${ChatColor.GREEN}Screenshot was uploaded to $link.")
                    message.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, link)
                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(message)
                }
            } else {
                ChatUtilities.sendNotification(
                    "Screenshot Manager",
                    "&cFailed to upload screenshot, maybe the file was moved/deleted?"
                )
            }
        } catch (e: Exception) {
            ChatUtilities.sendNotification("Screenshot Manager", "&cFailed to upload screenshot. ${e.message}")
            Patcher.instance.logger.error("Failed to upload screenshot.", e)
        }
    }
}