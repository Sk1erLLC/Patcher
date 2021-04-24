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

package club.sk1er.patcher.imgur

import club.sk1er.patcher.Patcher
import club.sk1er.patcher.util.chat.ChatUtilities
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class Imgur(private val clientId: String) {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun upload(file: File): String {
        val fileContent = withContext(Dispatchers.IO) { file.readBytes() }
        val data = Base64.getEncoder().encodeToString(fileContent)
        val encodedParams = "image=" + withContext(Dispatchers.IO) { URLEncoder.encode(data, "UTF-8") }

        return withContext(Dispatchers.IO) {
            val connection = URL("https://api.imgur.com/3/image").openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.doInput = true
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Client-ID $clientId")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.connect()

            connection.outputStream.bufferedWriter().use { it.write(encodedParams) }
            if (connection.responseCode != 200) {
                ChatUtilities.sendNotification(
                    "Screenshot Manager",
                    "&cImgur responded with ${connection.responseCode}. Perhaps you're uploading too quickly?"
                )
                Patcher.instance.logger.error("Failed to upload image, Imgur responded with {}", connection.responseCode)
            }

            connection.inputStream.reader().use {
                val imgurJson = JsonParser().parse(it).asJsonObject
                val dataJson = imgurJson.getAsJsonObject("data")
                dataJson.get("link").asString
            }
        }
    }
}
