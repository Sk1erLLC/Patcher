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

package club.sk1er.patcher.util.screenshot.imgur;

import club.sk1er.patcher.Patcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Upload task for uploading images to https://imgur.com, an image sharing service.
 * TODO: Runs on the same thread as client, fix this.
 */
public class Imgur implements Runnable {

    public static String link = "";
    private final String clientId;
    private final File uploadFile;

    public Imgur(String clientId, File uploadFile) {
        this.clientId = clientId;
        this.uploadFile = uploadFile;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            URL url = new URL("https://api.imgur.com/3/image");
            connection = (HttpURLConnection) url.openConnection();
            BufferedImage image;
            File file = uploadFile;
            file.mkdir();
            image = ImageIO.read(file);
            ImageIO.write(image, "png", baos);
            byte[] byteImage = baos.toByteArray();

            String dataImage = Base64.encodeBase64String(byteImage);
            String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Client-ID " + clientId);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
                outputStreamWriter.write(data);
            }

            JsonParser parser = new JsonParser();

            if (connection.getResponseCode() != 200) {
                JsonObject errorJson = parser.parse(new InputStreamReader(connection.getErrorStream())).getAsJsonObject();
                Patcher.instance.getLogger().error("Response code returned {} : {}", connection.getResponseCode(), errorJson);
            }

            JsonObject imgurJson = parser.parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            JsonObject dataJson = imgurJson.getAsJsonObject("data");
            link = dataJson.get("link").getAsString();
        } catch (IOException e) {
            Patcher.instance.getLogger().error("Failed uploading screenshot to Imgur.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
