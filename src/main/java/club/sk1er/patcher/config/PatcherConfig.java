package club.sk1er.patcher.config;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;

import java.io.File;

@SuppressWarnings("unused")
public class PatcherConfig extends Vigilant {

    public static PatcherConfig instance = new PatcherConfig();

    public PatcherConfig() {
        super(new File(Minecraft.getMinecraft().mcDataDir, "patcher.toml"));
    }

    @Property(
            type = PropertyType.SWITCH,
            name = "Fullscreen Fix",
            description = "Allow the screen to be resized after toggling fullscreen.",
            category = "Fixes",
            subcategory = "Screen"
    )
    public static boolean fullscreenFix = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Item Searching",
            description = "Minecraft doesn't stop searching for other items to combine despite being a full stack.",
            category = "Performance",
            subcategory = "Items"
    )
    public static boolean searchingOptimizationFix = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Stop Forcibly Closing Chat",
            description = "Remove the ability for servers to forcibly close chat.",
            category = "Fixes",
            subcategory = "Chat"
    )
    public static boolean stopForciblyClosingChat = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Reset Death Timers",
            description = "Allow for respawning when toggling fullscreen on the \"You Died!\" menu.",
            category = "Fixes",
            subcategory = "Screen"
    )
    public static boolean resetDeathTimers = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Command Handling",
            description = "Fix Forge's command handler not checking for a '/' at the start of a command.",
            category = "Fixes",
            subcategory = "Chat"
    )
    public static boolean forgeCommandHandling = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Inventory Position",
            description = "Stop potion effects from shifting your inventory to the right.",
            category = "Quality of Life",
            subcategory = "Inventory"
    )
    public static boolean inventoryPosition = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mouse Delay Fix",
            description = "Stop your crosshair being a tick ahead of your actual position.",
            category = "Quality of Life",
            subcategory = "Movement"
    )
    public static boolean mouseDelayFix = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Arm Position",
            description = "Reset the player state properly once mounting an entity.",
            category = "Quality of Life",
            subcategory = "Movement"
    )
    public static boolean armPosition = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Head Rotations",
            description = "Properly rotate the users head while mounting an entity.",
            category = "Quality of Life",
            subcategory = "Movement"
    )
    public static boolean headRotation = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Fullbright",
            description = "Remove lighting updates, increasing visibility.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean fullbright = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Sky Height",
            description = "Set the sky height to 0, removing void flickering.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean skyHeight = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Instant World Swapping",
            description = "Remove waiting times between swapping worlds.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean instantWorldSwapping = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Nausea Effect",
            description = "Remove the nether portal appearing when clearing nausea.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean nauseaEffect = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Internal Errors",
            description = "Fix several internal errors.",
            category = "Fixes",
            subcategory = "General"
    )
    public static boolean patchInternalErrors = true;
}
