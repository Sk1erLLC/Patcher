package club.sk1er.patcher.hooks;

import club.sk1er.patcher.tweaker.asm.NetHandlerPlayClientTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.objectweb.asm.tree.ClassNode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Used by {@link NetHandlerPlayClientTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class NetHandlerPlayClientHook {

    public static boolean validateResourcePackUrl(NetHandlerPlayClient client, String url, String hash) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            boolean isLevelProtocol = "level".equals(scheme);

            if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol) {
                client.getNetworkManager().sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                System.out.println("Malicious server tried to access " + url);
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                if (player != null) {
                    player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + EnumChatFormatting.BOLD.toString() +
                            "[WARNING] The current server has attempted to be malicious but we have stopped them."));
                }

                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }
            System.out.println("RET TRUE");
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("RET FF");
        return false;
    }
}
