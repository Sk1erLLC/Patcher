package club.sk1er.patcher.command;

import club.sk1er.mods.core.gui.notification.Notifications;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class SkinCacheRefresh extends CommandBase {

    private static boolean failedInMenu;

    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "refreshskin";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/refreshskin";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender
     * @param args
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        refreshSkin();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    public static void refreshSkin() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            SkinManager skinManager = mc.getSkinManager();

            GameProfile gameProfile = mc.getSession().getProfile();
            skinManager.loadProfileTextures(gameProfile, (type, location, profile) -> {
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(EntityPlayer.getUUID(gameProfile));
                    info.locationSkin = location;
                    info.skinType = profile.getMetadata("model");

                    if (info.skinType == null) {
                        info.skinType = "default";
                    }
                }
            }, true);

            Notifications.INSTANCE.pushNotification("Skin Cache", "Successfully refreshed skin cache.");
        } catch (Exception e) {
            Notifications.INSTANCE.pushNotification("Skin Cache", "Failed to refresh skin cache.");
            e.printStackTrace();
        }
    }
}
