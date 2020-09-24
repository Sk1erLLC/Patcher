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

import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class FovChangerCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "fov";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/fov <amount>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 1) {
            ChatUtilities.sendNotification("FOV Changer", "Too many arguments were provided. Usage: " + getCommandUsage(sender) + ".");
        } else if (args.length < 1) {
            ChatUtilities.sendNotification("FOV Changer", "Too little arguments were provided. Usage: " + getCommandUsage(sender) + ".");
        } else if (args[0].equals("0")) {
            ChatUtilities.sendNotification("FOV Changer", "Changing your FOV to 0 is disabled due to game-breaking visual bugs.");
        } else {
            try {
                final float fovAmount = Float.parseFloat(args[0]);

                if (fovAmount == 0) {
                    ChatUtilities.sendNotification("FOV Changer", "Changing your FOV to 0 is disabled due to game-breaking visual bugs.");
                    return;
                }

                ChatUtilities.sendNotification(
                    "FOV Changer",
                    "FOV changed from &e" + Minecraft.getMinecraft().gameSettings.fovSetting + "&r to &a" + fovAmount + "."
                );
                Minecraft.getMinecraft().gameSettings.fovSetting = fovAmount;
            } catch (NumberFormatException e) {
                ChatUtilities.sendNotification("FOV Changer", "You cannot use a letter as your FOV.");
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
