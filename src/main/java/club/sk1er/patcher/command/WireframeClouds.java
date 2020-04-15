package club.sk1er.patcher.command;

import club.sk1er.patcher.util.cloud.CloudRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class WireframeClouds extends CommandBase {
    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "patcher_wireframe";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender
     * @param args
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        CloudRenderer.WIREFRAME = !CloudRenderer.WIREFRAME;
        FovChangerCommand.sendMessage("Cloud Wireframe set to " + CloudRenderer.WIREFRAME);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
