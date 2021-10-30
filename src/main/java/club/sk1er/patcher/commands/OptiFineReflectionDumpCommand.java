package club.sk1er.patcher.commands;

import club.sk1er.patcher.optifine.OptiFineReflectorScraper;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class OptiFineReflectionDumpCommand extends Command {
    public OptiFineReflectionDumpCommand() {
        super("dumpreflectiondata");
    }

    @DefaultHandler
    public void handle() {
        OptiFineReflectorScraper.dumpInfo();
    }
}
