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

package club.sk1er.patcher.command;

import net.modcore.api.gui.Notifications;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.koin.java.KoinJavaComponent;

public class SkinCacheRefresh extends CommandBase {

    private static boolean failedInMenu;

    private static final Notifications notifications = KoinJavaComponent.get(Notifications.class);

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

            notifications.push("Skin Cache", "Successfully refreshed skin cache.");
        } catch (Exception e) {
            notifications.push("Skin Cache", "Failed to refresh skin cache.");
            e.printStackTrace();
        }
    }

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
}
