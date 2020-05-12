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

import club.sk1er.mods.core.ModCore;
import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.patcher.screen.ScreenHistory;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class NameHistoryCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "names";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/names <username>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        switch (strings.length) {
            case 0:
                ModCore.getInstance().getGuiHandler().open(new ScreenHistory());
                break;

            case 1:
                ModCore.getInstance().getGuiHandler().open(new ScreenHistory(strings[0], false));
                break;

            default:
                ChatUtilities.sendMessage("Usage: /name <username>");
                break;
        }
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("namehistory", "name", "username");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
