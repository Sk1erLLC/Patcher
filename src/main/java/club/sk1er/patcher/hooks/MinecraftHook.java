package club.sk1er.patcher.hooks;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.KeyBindingAccessor;
import club.sk1er.patcher.mixins.accessors.MinecraftAccessor;
import club.sk1er.patcher.screen.render.overlay.metrics.MetricsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;

@SuppressWarnings("unused")
public class MinecraftHook {
    public static final MinecraftHook INSTANCE = new MinecraftHook();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final MinecraftAccessor minecraftAccessor = (MinecraftAccessor) mc;
    private boolean lastFullscreen = false;
    public static MetricsData metricsData;

    //#if MC==10809
    public static void updateKeyBindState() {
        for (KeyBinding keybinding : KeyBindingAccessor.getKeybindArray()) {
            try {
                final int keyCode = keybinding.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, keyCode < 256 && Keyboard.isKeyDown(keyCode));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }
    //#endif

    public static boolean fullscreen() {
        if (!PatcherConfig.instantFullscreen || !PatcherConfig.windowedFullscreen || Util.getOSType() != Util.EnumOS.WINDOWS) {
            return false;
        }

        minecraftAccessor.setFullScreen(!mc.isFullScreen());

        boolean grabbed = Mouse.isGrabbed();
        if (grabbed)
            Mouse.setGrabbed(false);
        try {
            DisplayMode displayMode = Display.getDesktopDisplayMode();
            if (mc.isFullScreen()) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setDisplayMode(displayMode);
                Display.setLocation(0, 0);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                displayMode = new DisplayMode(minecraftAccessor.getTempDisplayWidth(), minecraftAccessor.getTempDisplayHeight());
                Display.setDisplayMode(displayMode);
                displayCommon();
            }
            Display.setFullscreen(false);

            mc.displayWidth = displayMode.getWidth();
            mc.displayHeight = displayMode.getHeight();
            if (mc.currentScreen != null) {
                mc.resize(mc.displayWidth, mc.displayHeight);
            } else {
                minecraftAccessor.invokeUpdateFramebufferSize();
            }
            INSTANCE.lastFullscreen = mc.isFullScreen(); //Forward so both behavior isn't ran
            mc.updateDisplay();
            Mouse.setCursorPosition((Display.getX() + Display.getWidth()) >> 1, (Display.getY() + Display.getHeight()) >> 1);
            if (grabbed)
                Mouse.setGrabbed(true);
            Display.setResizable(false);
            Display.setResizable(true);
            return true;
        } catch (LWJGLException e) {
            Patcher.instance.getLogger().error("Failed to toggle fullscreen.", e);
        }
        return false;
    }

    private static void displayCommon() {
        Display.setResizable(false);
        Display.setResizable(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - Display.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - Display.getHeight()) / 2);
        Display.setLocation(x, y);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PatcherConfig.windowedFullscreen)
            return;
        boolean fullScreenNow = Minecraft.getMinecraft().isFullScreen();
        if (lastFullscreen != fullScreenNow) {
            fix(fullScreenNow);
            lastFullscreen = fullScreenNow;
        }
    }

    public void fix(boolean fullscreen) {
        try {
            if (fullscreen) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                Display.setLocation(0, 0);
                Display.setFullscreen(false);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Display.setDisplayMode(new DisplayMode(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight));
                displayCommon();
            }

            Display.setResizable(!fullscreen);
        } catch (LWJGLException e) {
            Patcher.instance.getLogger().error("Failed to update screen type.", e);
        }
    }
}
