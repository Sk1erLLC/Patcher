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

import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class NameHistoryCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "pnames";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/pnames <username>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        ChatUtilities.sendMessage("&cThis command has moved to &b/patcher name [username]&c. This message will be removed in Patcher 1.4.");
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("pnamehistory", "pname", "pusername");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
