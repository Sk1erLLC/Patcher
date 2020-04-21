package club.sk1er.patcher.util.screenshot.imgur;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Imgur implements Runnable {

    public static String link;
    private final String clientId;
    private final File uploadFile;

    public Imgur(String clientId, File uploadFile) {
        this.clientId = clientId;
        this.uploadFile = uploadFile;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("https://api.imgur.com/3/image");
            connection = (HttpURLConnection) url.openConnection();
            BufferedImage image;
            File file = uploadFile;
            file.mkdir();
            image = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            byte[] byteImage = byteArrayOutputStream.toByteArray();

            String dataImage = Base64.encodeBase64String(byteImage);
            String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Client-ID " + clientId);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonObject imgurJson = parser.parse(bufferedReader).getAsJsonObject();
            JsonObject dataJson = imgurJson.getAsJsonObject("data");
            link = dataJson.get("link").getAsString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
