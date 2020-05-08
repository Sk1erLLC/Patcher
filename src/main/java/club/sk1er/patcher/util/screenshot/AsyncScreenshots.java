package club.sk1er.patcher.util.screenshot;

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.mods.core.util.ModCoreDesktop;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.chat.ChatUtilities;
import club.sk1er.patcher.util.screenshot.imgur.Imgur;
import club.sk1er.patcher.util.screenshot.viewer.Viewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsyncScreenshots implements Runnable {

    private final int width, height;
    private final int[] pixelValues;
    private final Framebuffer framebuffer;
    private final File screenshotDirectory;
    private static BufferedImage image;
    private static File screenshot;

    public static final String prefix = ChatColor.translateAlternateColorCodes('&', "&e[Patcher] &r");

    public AsyncScreenshots(int width, int height, int[] pixelValues, Framebuffer framebuffer, File screenshotDirectory) {
        this.width = width;
        this.height = height;
        this.pixelValues = pixelValues;
        this.framebuffer = framebuffer;
        this.screenshotDirectory = screenshotDirectory;
    }

    @Override
    public void run() {
        processPixelValues(pixelValues, width, height);
        screenshot = getTimestampedPNGFileForDirectory(screenshotDirectory);

        try {
            if (OpenGlHelper.isFramebufferEnabled()) {
                image = new BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1);

                int tHeight;

                for (int heightSize = tHeight = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight; tHeight < framebuffer.framebufferTextureHeight; ++tHeight) {
                    for (int widthSize = 0; widthSize < framebuffer.framebufferWidth; ++widthSize) {
                        image.setRGB(widthSize, tHeight - heightSize, pixelValues[tHeight * framebuffer.framebufferTextureWidth + widthSize]);
                    }
                }
            } else {
                image = new BufferedImage(width, height, 1);
                image.setRGB(0, 0, width, height, pixelValues, 0, width);
            }

            ImageIO.write(image, "png", screenshot);

            if (!PatcherConfig.screenshotNoFeedback) {
                sendChatMessages(screenshot);
            }

            if (PatcherConfig.screenshotPreview) {
                Viewer.getInstance().newCapture(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(prefix + "Couldn't capture screenshot."));
        }
    }

    private void sendChatMessages(File screenshot) throws IOException {
        IChatComponent chatComponent;
        if (!PatcherConfig.compactScreenshotResponse) {
            chatComponent = new ChatComponentText(prefix + "Screenshot saved to " + screenshot.getName() +
                " (" + screenshot.length() / 1024 + "kb)");
        } else {
            chatComponent = new ChatComponentText(prefix + "Screenshot saved.");
        }

        chatComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openfolder"));
        chatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7Open screenshot"))));

        IChatComponent favoriteComponent = new ChatComponentText(ChatColor.YELLOW.toString() + ChatColor.BOLD +
            (PatcherConfig.compactScreenshotResponse ? "FAV" : "FAVORITE"));
        favoriteComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/favorite"));
        favoriteComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7This'll save the screenshot to a new folder called\n" +
                    "&afavorite_screenshots &7in your Minecraft directory.\n" +
                    "&cThis cannot be done once a new screenshot is taken."))));

        IChatComponent deleteComponent = new ChatComponentText(ChatColor.RED.toString() + ChatColor.BOLD +
            (PatcherConfig.compactScreenshotResponse ? "DEL" : "DELETE"));
        deleteComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/delete"));
        deleteComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7This'll delete the screenshot from your screenshots folder.\n" +
                    "&cThis is not recoverable and cannot be deleted once a\n" +
                    "&cnew screenshot is taken, or favorited."))));

        IChatComponent imgurComponent = new ChatComponentText(ChatColor.GREEN.toString() + ChatColor.BOLD +
            (PatcherConfig.compactScreenshotResponse ? "UPL" : "UPLOAD"));
        imgurComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/upload"));
        imgurComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7Upload the screenshot to Imgur, a picture sharing site.\n" +
                    "&cYour game might freeze for a bit uploading this.\n" +
                    "&cThis cannot be uploaded once a new screenshot is taken, favorited, or deleted."))));

        IChatComponent copyComponent = new ChatComponentText(ChatColor.AQUA.toString() + ChatColor.BOLD +
            (PatcherConfig.compactScreenshotResponse ? "CPY" : "COPY"));
        copyComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copyss"));
        copyComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7Copy this image to your system clipboard.\n" +
                    "&cThis cannot be copied once a new screenshot is taken, favorited, or deleted."))));

        IChatComponent folderComponent = new ChatComponentText(ChatColor.BLUE.toString() + ChatColor.BOLD +
            (PatcherConfig.compactScreenshotResponse ? "DIR" : "FOLDER"));
        folderComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotDirectory.getCanonicalPath()));
        folderComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
            ChatColor.translateAlternateColorCodes('&',
                "&7Open your screenshots folder."))));

        IChatComponent controlsMessage = new ChatComponentText("");
        controlsMessage.appendSibling(favoriteComponent);
        controlsMessage.appendText(" ");
        controlsMessage.appendSibling(deleteComponent);
        controlsMessage.appendText(" ");
        controlsMessage.appendSibling(imgurComponent);
        controlsMessage.appendText(" ");
        controlsMessage.appendSibling(copyComponent);
        controlsMessage.appendText(" ");
        controlsMessage.appendSibling(folderComponent);

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponent);
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(controlsMessage, 32251);
    }

    private static void processPixelValues(int[] pixels, int displayWidth, int displayHeight) {
        int[] xValues = new int[displayWidth];

        for (int yValues = displayHeight / 2, val = 0; val < yValues; ++val) {
            System.arraycopy(pixels, val * displayWidth, xValues, 0, displayWidth);
            System.arraycopy(pixels, (displayHeight - 1 - val) * displayWidth, pixels, val * displayWidth, displayWidth);
            System.arraycopy(xValues, 0, pixels, (displayHeight - 1 - val) * displayWidth, displayWidth);
        }
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
        String dateFormatting = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        int screenshotCount = 1;
        File screenshot;

        while (true) {
            screenshot = new File(gameDirectory, dateFormatting + ((screenshotCount == 1) ? "" : ("_" + screenshotCount)) + ".png");
            if (!screenshot.exists()) {
                break;
            }

            ++screenshotCount;
        }

        return screenshot;
    }

    public static class ScreenshotsFolder extends CommandBase {

        @Override
        public String getCommandName() {
            return "openfolder";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/openfolder";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            try {
                ModCoreDesktop.INSTANCE.open(screenshot.getCanonicalFile());
            } catch (Exception e) {
                ChatUtilities.sendMessage("Unfortunately, we were unable to open the folder. Please report this to us at https://discord.gg/sk1er.");
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }
    }

    public static class FavoriteScreenshot extends CommandBase {

        @Override
        public String getCommandName() {
            return "favorite";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/favorite";
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            try {
                File favorited_screenshots = getTimestampedPNGFileForDirectory(new File("./favorite_screenshots"));
                screenshot.delete();

                if (!favorited_screenshots.exists()) {
                    favorited_screenshots.mkdirs();
                }

                ImageIO.write(image, "png", favorited_screenshots);
                ChatUtilities.sendMessage("&e" + screenshot.getName() + " has been favorited.");
            } catch (Throwable e) {
                ChatUtilities.sendMessage("&cFailed to favorite screenshot, maybe the file was moved/deleted?");
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }
    }

    public static class DeleteScreenshot extends CommandBase {
        @Override
        public String getCommandName() {
            return "delete";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/delete";
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (screenshot.exists()) {
                ChatUtilities.sendMessage("&c" + screenshot.getName() + " has been deleted.");
                screenshot.delete();

                // not on disk, remove from memory
                screenshot = null;
            } else {
                ChatUtilities.sendMessage("&cCouldn't find " + screenshot.getName());
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }
    }

    public static class UploadScreenshot extends CommandBase {

        @Override
        public String getCommandName() {
            return "upload";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/upload";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            try {
                if (screenshot != null) {
                    new Imgur("649f2fb48e59767", screenshot).run();
                    if (Imgur.link == null) {
                        ChatUtilities.sendMessage("&cFailed to upload screenshot, link returned null. Maybe the file was moved/deleted?");
                    } else {
                        IChatComponent uploadedComponent = new ChatComponentText(ChatColor.GREEN + "Screenshot was uploaded to " + Imgur.link);
                        uploadedComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Imgur.link));
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(uploadedComponent);
                    }
                } else {
                    ChatUtilities.sendMessage("&cFailed to upload screenshot, maybe the file was moved/deleted?");
                }
            } catch (Throwable e) {
                ChatUtilities.sendMessage("&cFailed to upload screenshot, maybe the file was moved/deleted?");
                e.printStackTrace();
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }
    }

    public static class CopyScreenshot extends CommandBase {

        @Override
        public String getCommandName() {
            return "copyss";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/copyss";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            try {
                ImageSelection sel = new ImageSelection(image);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);

                IChatComponent uploadedComponent = new ChatComponentText(ChatColor.GREEN + "Screenshot has been copied to your clipboard");
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(uploadedComponent);
            } catch (Exception e) {
                ChatUtilities.sendMessage("&cFailed to copy the screenshot: " + e.getCause());
                e.printStackTrace();
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
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
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }


    }
}