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

import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class FovChangerCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "fov";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/fov <number>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length > 1) {
            ChatUtilities.sendMessage("Too many arguments. Usage: " + getCommandUsage(iCommandSender));
        } else if (args.length < 1) {
            ChatUtilities.sendMessage("Too little arguments. Usage: " + getCommandUsage(iCommandSender));
        } else if (args[0].equals("0")) {
            ChatUtilities.sendMessage("Changing your fov to 0 is disabled due to breaking the game.");
        } else {
            try {
                float fovAmount = Float.parseFloat(args[0]);

                if (fovAmount == 0) {
                    ChatUtilities.sendMessage("Changing your fov to 0 is disabled due to breaking the game.");
                    return;
                }

                ChatUtilities.sendMessage("Fov changed from " + EnumChatFormatting.YELLOW + Minecraft
                    .getMinecraft().gameSettings.fovSetting + EnumChatFormatting.RESET + " to "
                    + EnumChatFormatting.GREEN + fovAmount);
                Minecraft.getMinecraft().gameSettings.fovSetting = fovAmount;
            } catch (NumberFormatException e) {
                ChatUtilities.sendMessage("You cannot use a letter.");
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
