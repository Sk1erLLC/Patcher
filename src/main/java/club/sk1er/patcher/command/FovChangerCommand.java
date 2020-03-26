package club.sk1er.patcher.command;

import club.sk1er.mods.core.util.MinecraftUtils;
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
      sendMessage("Too many arguments. Usage: " + getCommandUsage(iCommandSender));
    } else if (args.length < 1) {
      sendMessage("Too little arguments. Usage: " + getCommandUsage(iCommandSender));
    } else if (args[0].equals("0")) {
      sendMessage("Changing your fov to 0 is disabled due to breaking the game.");
    } else {
      try {
        float fovAmount = Float.parseFloat(args[0]);

        if (fovAmount == 0) {
          sendMessage("Changing your fov to 0 is disabled due to breaking the game.");
          return;
        }

        sendMessage("Fov changed from " + EnumChatFormatting.YELLOW + Minecraft
            .getMinecraft().gameSettings.fovSetting + EnumChatFormatting.RESET + " to "
            + EnumChatFormatting.GREEN + fovAmount);
        Minecraft.getMinecraft().gameSettings.fovSetting = fovAmount;
      } catch (NumberFormatException e) {
        sendMessage("You cannot use a letter.");
      }
    }
  }

  private void sendMessage(String message) {
    MinecraftUtils.sendMessage(EnumChatFormatting.YELLOW + "[Patcher] ", message);
  }

  @Override
  public int getRequiredPermissionLevel() {
    return -1;
  }
}
