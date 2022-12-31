package club.sk1er.patcher.util.screenshot;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.render.ScreenshotPreview;
import club.sk1er.patcher.tasks.UploadScreenshotTask;
import club.sk1er.patcher.util.chat.ChatUtilities;
import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import cc.polyfrost.oneconfig.libs.universal.UDesktop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsyncScreenshots implements Runnable {

    public static final String prefix = ChatUtilities.translate("&e[Patcher] &r");
    private static BufferedImage image;
    private static File screenshot;
    private final int width, height;
    private final int[] pixelValues;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final File screenshotDirectory;

    public AsyncScreenshots(int width, int height, int[] pixelValues, File screenshotDirectory) {
        this.width = width;
        this.height = height;
        this.pixelValues = pixelValues;
        this.screenshotDirectory = screenshotDirectory;
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
        String dateFormatting = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        int screenshotCount = 1;
        File screenshot;

        while (true) {
            screenshot = new File(
                gameDirectory,
                dateFormatting + ((screenshotCount == 1) ? "" : ("_" + screenshotCount)) + ".png"
            );
            if (!screenshot.exists()) {
                break;
            }

            ++screenshotCount;
        }

        return screenshot;
    }

    @Override
    public void run() {
        processPixelValues(pixelValues, width, height);
        screenshot = getTimestampedPNGFileForDirectory(screenshotDirectory);

        try {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, width, height, pixelValues, 0, width);
            ImageIO.write(image, "png", screenshot);

            if (!PatcherConfig.screenshotNoFeedback) {
                sendChatMessages(screenshot);
            }

            if (PatcherConfig.screenshotPreview) {
                ScreenshotPreview.INSTANCE.newCapture(image);
            }

            if (PatcherConfig.autoCopyScreenshot) {
                CopyScreenshot.copyScreenshot(mc.thePlayer != null);
            }
        } catch (Exception e) {
            ChatUtilities.sendNotification("Screenshot Manager", "Failed to capture screenshot. " + e.getMessage());
            Patcher.instance.getLogger().error("Failed to capture screenshot.", e);
        }
    }

    private void sendChatMessages(File screenshot) throws IOException {
        final boolean compact = PatcherConfig.compactScreenshotResponse;
        IChatComponent chatComponent;
        if (!compact) {
            chatComponent = new ChatComponentText(prefix + "Screenshot saved to " + screenshot.getName() +
                " (" + screenshot.length() / 1024 + "kb)");
        } else {
            chatComponent = new ChatComponentText(prefix + "Screenshot saved.");
        }

        final IChatComponent favoriteComponent = new ChatComponentText(ChatColor.YELLOW.toString() + ChatColor.BOLD +
            (compact ? "FAV" : "FAVORITE"));
        favoriteComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$favorite"));
        favoriteComponent.getChatStyle()
            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                ChatUtilities.translate("&7This will save the screenshot to a new folder called\n" +
                    "&afavorite_screenshots &7in your Minecraft directory.\n" +
                    "&cThis cannot be done once a new screenshot is taken."))));

        final IChatComponent deleteComponent = new ChatComponentText(ChatColor.RED.toString() + ChatColor.BOLD +
            (compact ? "DEL" : "DELETE"));
        deleteComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$delete"));
        deleteComponent.getChatStyle()
            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                ChatUtilities.translate("&7This will delete the screenshot from your screenshots folder.\n" +
                    "&cThis is not recoverable and cannot be deleted once a\n" +
                    "&cnew screenshot is taken or made favorite."))));

        final IChatComponent imgurComponent = new ChatComponentText(ChatColor.GREEN.toString() + ChatColor.BOLD +
            (compact ? "UPL" : "UPLOAD"));
        imgurComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$upload"));
        imgurComponent.getChatStyle()
            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                ChatUtilities.translate("&7Upload the screenshot to Imgur, an image hosting website.\n" +
                    "&cThis cannot be uploaded once a new screenshot\n" +
                    "&cis taken, made favorite, or deleted."))));

        final IChatComponent copyComponent = new ChatComponentText(ChatColor.AQUA.toString() + ChatColor.BOLD +
            (compact ? "CPY" : "COPY"));
        copyComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$copyss"));
        copyComponent.getChatStyle()
            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                ChatUtilities.translate("&7Copy this image to your system clipboard.\n" +
                    "&cThis cannot be copied once a new screenshot\n" +
                    "&cis taken, made favorite, or deleted."))));

        final IChatComponent folderComponent = new ChatComponentText(ChatColor.BLUE.toString() + ChatColor.BOLD +
            (compact ? "DIR" : "FOLDER"));
        folderComponent.getChatStyle()
            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotDirectory.getCanonicalPath()));
        folderComponent.getChatStyle()
            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                ChatUtilities.translate("&7Open your screenshots folder."))));

        final IChatComponent controlsMessage = new ChatComponentText("");
        if (PatcherConfig.favoriteScreenshot) controlsMessage.appendSibling(favoriteComponent);
        if (PatcherConfig.deleteScreenshot) controlsMessage.appendText(" ").appendSibling(deleteComponent);
        if (PatcherConfig.uploadScreenshot) controlsMessage.appendText(" ").appendSibling(imgurComponent);
        if (PatcherConfig.copyScreenshot) controlsMessage.appendText(" ").appendSibling(copyComponent);
        if (PatcherConfig.openScreenshotsFolder) controlsMessage.appendText(" ").appendSibling(folderComponent);

        final GuiNewChat chat = mc.ingameGUI.getChatGUI();
        chat.printChatMessage(chatComponent);
        chat.printChatMessageWithOptionalDeletion(controlsMessage, 33000);
    }

    private void processPixelValues(int[] pixels, int displayWidth, int displayHeight) {
        final int[] xValues = new int[displayWidth];
        for (int yValues = displayHeight >> 1, val = 0; val < yValues; ++val) {
            System.arraycopy(pixels, val * displayWidth, xValues, 0, displayWidth);
            System.arraycopy(
                pixels,
                (displayHeight - 1 - val) * displayWidth,
                pixels,
                val * displayWidth,
                displayWidth
            );
            System.arraycopy(xValues, 0, pixels, (displayHeight - 1 - val) * displayWidth, displayWidth);
        }
    }

    @Command("$openfolder")
    public static class ScreenshotsFolder {
        @Main
        public void handle() {
            try {
                UDesktop.open(new File("./screenshots"));
            } catch (Exception e) {
                ChatUtilities.sendMessage("Unfortunately, we were unable to open the screenshots folder. " +
                    "Contact the support Discord at https://polyfrost.cc/discord if this issue persists.");
            }
        }
    }

    @Command("$favorite")
    public static class FavoriteScreenshot {

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Main
        public void handle() {
            try {
                final File favoritedScreenshots = getTimestampedPNGFileForDirectory(new File("./favorite_screenshots"));
                screenshot.delete();

                if (!favoritedScreenshots.exists()) {
                    favoritedScreenshots.mkdirs();
                }

                ImageIO.write(image, "png", favoritedScreenshots);
                ChatUtilities.sendNotification("Screenshot Manager", "&e" + screenshot.getName() + " has been favorited.");
            } catch (Throwable e) {
                ChatUtilities.sendNotification("Screenshot Manager", "&cFailed to favorite screenshot, maybe the file was moved/deleted?");
            }
        }
    }

    @Command("$delete")
    public static class DeleteScreenshot {
        @Main
        public void handle() {
            try {
                if (screenshot.exists() && screenshot.delete()) {
                    ChatUtilities.sendNotification("Screenshot Manager", "&c" + screenshot.getName() + " has been deleted.");
                    screenshot = null;
                } else {
                    ChatUtilities.sendNotification("Screenshot Manager", "&cCouldn't find " + screenshot.getName());
                }
            } catch (NullPointerException e) {
                ChatUtilities.sendNotification("Screenshot Manager", "&cFailed to delete screenshot, maybe the file was moved/deleted?");
            }
        }
    }

    @Command("$upload")
    public static class UploadScreenshot {
        @Main
        public void handle() {
            UploadScreenshotTask.INSTANCE.execute(screenshot);
        }
    }

    @Command("$copyss")
    public static class CopyScreenshot {

        @Main
        public void handle() {
            try {
                copyScreenshot(true);
            } catch (HeadlessException e) {
                ChatUtilities.sendNotification("Screenshot Manager", "&cFailed to copy screenshot to clipboard.");
                Patcher.instance.getLogger().error("Failed to copy screenshot to clipboard.", e);
            }
        }

        public static void copyScreenshot(boolean message) throws HeadlessException {
            final ImageSelection sel = new ImageSelection(image);
            Multithreading.runAsync(() -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null));

            if (message) {
                ChatUtilities.sendMessage("&aScreenshot has been copied to your clipboard.");
            }
        }
    }

    static class ImageSelection implements Transferable {
        private final Image image;

        ImageSelection(Image image) {
            this.image = image;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor == flavor;
        }

        // Returns image
        @NotNull
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (DataFlavor.imageFlavor != flavor) {
                throw new UnsupportedFlavorException(flavor);
            }

            return image;
        }
    }
}
