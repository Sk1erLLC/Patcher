package club.sk1er.patcher.util.world.sound.audioswitcher;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;

import java.util.List;

@SuppressWarnings("unused")
public class LibraryLWJGLOpenALImpl {
    public static void createAL() throws LWJGLException {
        try {
            if (AL.isCreated()) AL.destroy();

            AudioSwitcher audioSwitcher = Patcher.instance.getAudioSwitcher();
            List<String> devices = audioSwitcher.getDevices();
            if (devices.isEmpty()) {
                AL.create();

                audioSwitcher.fetchAvailableDevicesUncached();
                devices = audioSwitcher.getDevices();

                AL.destroy();
            }

            String selectedAudioDevice = PatcherConfig.selectedAudioDevice;
            if (devices.contains(selectedAudioDevice)) {
                AL.create(selectedAudioDevice, 44100, 60, false);
            } else {
                AL.create();
            }
        } catch (Exception e) {
            Patcher.instance.getLogger().error("Failed to create device, using system default.", e);
            AL.destroy();
            AL.create();
        }
    }
}
