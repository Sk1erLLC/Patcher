package club.sk1er.patcher.util.world.sound.audioswitcher;

import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ALCHelper {

    private List<String> devices = new ArrayList<>();

    public List<String> getAvailableDevices(boolean useCache) {
        if (!useCache || this.devices.isEmpty()) {
            String[] availableDevices = this.getAvailableDevicesString();

            if (availableDevices == null) {
                this.devices = new ArrayList<>();
            } else {
                this.devices = Arrays.stream(availableDevices).distinct().collect(Collectors.toList());
            }
        }

        return this.devices;
    }

    @Nullable
    private String[] getAvailableDevicesString() {
        try {
            return ALC10.alcGetString(null, ALC11.ALC_ALL_DEVICES_SPECIFIER).split("\0");
        } catch (Exception ignored) {
            return ALC10.alcGetString(null, ALC10.ALC_DEVICE_SPECIFIER).split("\0");
        }
    }
}
