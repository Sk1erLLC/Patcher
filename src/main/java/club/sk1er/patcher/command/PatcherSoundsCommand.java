package club.sk1er.patcher.command;

import club.sk1er.mods.core.ModCore;
import club.sk1er.patcher.Patcher;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class PatcherSoundsCommand extends CommandBase {

  @Override
  public String getCommandName() {
    return "patchersounds";
  }

  @Override
  public String getCommandUsage(ICommandSender iCommandSender) {
    return "/" + getCommandName();
  }

  @Override
  public void processCommand(ICommandSender iCommandSender, String[] strings) {
    ModCore.getInstance().getGuiHandler().open(Patcher.instance.getPatcherSoundConfig().gui());
  }

  @Override
  public int getRequiredPermissionLevel() {
    return -1;
  }

  @Override
  public List<String> getCommandAliases() {
    return Arrays.asList("psounds", "soundconfig", "ps", "sounds");
  }
}
