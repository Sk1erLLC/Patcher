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

package club.sk1er.patcher.command

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
import java.io.File

object UploadScreenshotTask {
    private val client = Imgur("649f2fb48e59767")

    fun execute(file: File?) {
        try {
            if (file != null) {
                ChatUtilities.sendMessage("&aUploading screenshot...")

                MCDispatchers.PATCHER_SCOPE.launch(Dispatchers.IO) {
                    val link = client.upload(file)
                    val message = ChatComponentText("${AsyncScreenshots.prefix}${EnumChatFormatting.GREEN}Screenshot was uploaded to $link.")
                    message.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, link)
                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(message)
                }
            } else {
                ChatUtilities.sendMessage("&cFailed to upload screenshot, maybe the file was moved/deleted?")
            }
        } catch (t: Throwable) {
            ChatUtilities.sendMessage("&cFailed to upload screenshot. ${t.message}")
            t.printStackTrace()
        }
    }
}