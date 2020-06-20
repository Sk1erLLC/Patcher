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

import club.sk1er.patcher.imgur.Imgur
import club.sk1er.patcher.util.chat.ChatUtilities
import club.sk1er.patcher.util.screenshot.AsyncScreenshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import java.io.File

class UploadScreenshotTask {
    fun upload(file: File?) {
        try {
            if (file != null) {
                ChatUtilities.sendMessage("&aUploading screenshot...")

                runBlocking {
                    withContext(Dispatchers.Default) {
                        Imgur("649f2fb48e59767").upload(file)
                    }
                }

                val uploadedComponent =
                    ChatComponentText("${AsyncScreenshots.prefix}${EnumChatFormatting.GREEN}Screenshot was uploaded to ${Imgur.link}.")
                uploadedComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, Imgur.link)
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(uploadedComponent)
            } else {
                ChatUtilities.sendMessage("&cFailed to upload screenshot, maybe the file was moved/deleted?")
            }
        } catch (t: Throwable) {
            ChatUtilities.sendMessage("&cFailed to upload screenshot. ${t.message}")
            t.printStackTrace()
        }
    }
}