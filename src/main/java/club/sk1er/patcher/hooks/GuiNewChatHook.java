package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Queues;
import gg.essential.universal.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Deque;

@SuppressWarnings("unused")
public class GuiNewChatHook {

    public static final Deque<IChatComponent> messageQueue = Queues.newArrayDeque();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static long lastMessageAddedTime = 0L;

    public static void processMessageQueue() {
        if (!messageQueue.isEmpty()) {
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - lastMessageAddedTime) >= getChatDelayMillis()) {
                mc.ingameGUI.getChatGUI().printChatMessage(messageQueue.remove());
                lastMessageAddedTime = currentTime;
            }
        }
    }

    public static void queueMessage(IChatComponent message) {
        if (PatcherConfig.chatDelay <= 0.0D) {
            mc.ingameGUI.getChatGUI().printChatMessage(message);
        } else {
            final long currentTime = System.currentTimeMillis();
            if ((currentTime - lastMessageAddedTime) >= getChatDelayMillis()) {
                mc.ingameGUI.getChatGUI().printChatMessage(message);
                lastMessageAddedTime = currentTime;
            } else {
                messageQueue.add(message);
            }
        }
    }

    public static void drawMessageQueue() {
        final int chatWidth = MathHelper.ceiling_float_int(mc.ingameGUI.getChatGUI().getChatWidth() / mc.ingameGUI.getChatGUI().getChatScale());

        if (!messageQueue.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 50);
            Gui.drawRect(0, 0, chatWidth + 4, 9, 2130706432);
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, 50);
            mc.fontRendererObj.drawStringWithShadow(ChatColor.GRAY + "[+" + messageQueue.size() + " pending lines]", 0, 1, -1);
            GlStateManager.popMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public static void mouseClicked() {
        if (!messageQueue.isEmpty()) {
            mc.ingameGUI.getChatGUI().printChatMessage(messageQueue.remove());
            lastMessageAddedTime = System.currentTimeMillis();
        }
    }

    public static void processChatMessage(S02PacketChat packet, IChatComponent message) {
        if (packet.getType() == 0) {
            queueMessage(message);
        } else if (packet.getType() == 1) {
            mc.ingameGUI.getChatGUI().printChatMessage(message);
        }
    }

    private static long getChatDelayMillis() {
        return (long) (PatcherConfig.chatDelay * 1000.0D);
    }
}
