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

package club.sk1er.patcher.tasks

import club.sk1er.mods.core.universal.ChatColor
import club.sk1er.mods.core.universal.UDesktop
import club.sk1er.patcher.Patcher
import club.sk1er.patcher.coroutines.MCDispatchers
import club.sk1er.patcher.imgur.Imgur
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.screenshot.AsyncScreenshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.modcore.api.ModCoreAPI
import java.io.File
import java.net.URI

object UploadScreenshotTask {
    private val client = Imgur("649f2fb48e59767")

    fun execute(file: File?) {
        try {
            if (file != null) {
                ChatUtilities.sendNotification("Screenshot Manager", "&aUploading screenshot...")

                MCDispatchers.PATCHER_SCOPE.launch(Dispatchers.IO) {
                    val link = client.upload(file)
                    if (ModCoreAPI.getConfig().disableAllNotifications) {
                        val message = ChatComponentText("${AsyncScreenshots.prefix}${EnumChatFormatting.GREEN}Screenshot was uploaded to $link.")
                        message.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, link)
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(message)
                    } else {
                        ModCoreAPI.getNotifications().push(
                            "Screenshot Manager",
                            "${ChatColor.GREEN}Screenshot was uploaded to $link"
                        ) {
                            UDesktop.browse(URI(link))
                        }
                    }
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