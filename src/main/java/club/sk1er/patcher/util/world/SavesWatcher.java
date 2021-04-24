package club.sk1er.patcher.util.world;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSelectWorld;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavesWatcher {

    private WatchService watchService;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Path savesFolder = new File(mc.mcDataDir, "saves").toPath();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void watch() {
        this.executor.execute(() -> {
            try {
                this.watchService = FileSystems.getDefault().newWatchService();
                this.savesFolder.register(this.watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

                WatchKey key;
                while ((key = this.watchService.take()) != null) {
                    for (WatchEvent<?> ignored : key.pollEvents()) {
                        if (this.mc.currentScreen instanceof GuiSelectWorld) {
                            this.mc.addScheduledTask(() -> {
                                if (this.mc.currentScreen instanceof GuiSelectWorld) {
                                    this.mc.displayGuiScreen(new GuiSelectWorld(((GuiSelectWorld) mc.currentScreen).parentScreen));
                                }
                            });
                        }
                    }

                    key.reset();
                }
            } catch (Exception e) {
                Patcher.instance.getLogger().error("Failed to watch for new saves.", e);
            }
        });
    }
}
