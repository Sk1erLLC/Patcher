package club.sk1er.patcher.config;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

@SuppressWarnings("unused")
public class PatcherConfig extends Vigilant {
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
            name = "Case Insensitive Commands",
            description = "Allow for case insensitivity.",
            category = "Fixes",
            subcategory = "Chat"
    )
    public static boolean caseInsensitiveCommands = true;

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
            category = "Fixes",
            subcategory = "Movement"
    )
    public static boolean mouseDelayFix = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Arm Position",
            description = "Reset the player state properly once mounting an entity.",
            category = "Fixes",
            subcategory = "Movement"
    )
    public static boolean armPosition = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Head Rotations",
            description = "Properly rotate the users head while mounting an entity.",
            category = "Fixes",
            subcategory = "Movement"
    )
    public static boolean headRotation = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Fullbright",
            description = "Remove lighting updates, increasing visibility.",
            category = "Quality of Life",
            subcategory = "World"
    )
    public static boolean fullbright = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Sky Height",
            description = "Set the sky height to 0, removing void flickering.",
            category = "Quality of Life",
            subcategory = "World"
    )
    public static boolean skyHeight = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Instant World Swapping",
            description = "Remove waiting times between swapping worlds.",
            category = "Quality of Life",
            subcategory = "World"
    )
    public static boolean instantWorldSwapping = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Nausea Effect",
            description = "Remove the nether portal appearing when clearing nausea.",
            category = "Quality of Life",
            subcategory = "Rendering"
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

    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Achievements",
            description = "Remove achievement notifications.",
            category = "Quality of Life",
            subcategory = "Rendering"
    )
    public static boolean disableAchievements = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Container Backgrounds",
            description = "Remove the dark background inside a container.",
            category = "Quality of Life",
            subcategory = "Inventory"
    )
    public static boolean disableTransparentBackgrounds = false;

    @Property(
            type = PropertyType.SLIDER,
            name = "Fire Height",
            description = "Change the height at which fire renders at.",
            category = "Quality of Life",
            subcategory = "Rendering",
            min = -200,
            max = 200
    )
    public static int fireHeight;

    @Property(
            type = PropertyType.SLIDER,
            name = "Chat History Length",
            description = "Change how many messages save in chat.",
            category = "Quality of Life",
            subcategory = "Chat",
            min = 100,
            max = 10000
    )
    public static int chatHistoryLength = 100;

    // todo refresh resourcepack image when toggled
    @Property(
            type = PropertyType.SWITCH,
            name = "Pack Images",
            description = "Change all pack icons to 64x64 to improve memory usage.",
            category = "Performance",
            subcategory = "Packs"
    )
    public static boolean packImageOptimization = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Toggle Tab",
            description = "Hold tab open with a single keypress.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean toggleTab;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crosshair Perspective",
            description = "Remove the crosshair when in third person.",
            category = "Quality of Life",
            subcategory = "General"
    )
    public static boolean crosshairPerspective;

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Tall Grass",
        description = "Stop tall grass/double tall plants from rendering.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean removeTallGrass;

    @Property(
        type = PropertyType.SWITCH,
        name = "Transparent Chat",
        description = "Remove the background from chat.",
        category = "Quality of Life",
        subcategory = "Chat"
    )
    public static boolean transparentChat;

    @Property(
        type = PropertyType.SWITCH,
        name = "Tab Height",
        description = "Move the tab overlay down 12 pixels when there's an active bossbar.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean tabHeight = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Armorstands",
        description = "Stop armorstands from rendering.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableArmorstands;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Item Frames",
        description = "Stop item frames from rendering.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableItemFrames;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Arrows",
        description = "Stop arrows shot by a player from rendering.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableArrows;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Skulls",
        description = "Stop skulls from rendering.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableSkulls;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable End Portals",
        description = "Stop end portals from rendering.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableEndPortals;

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Own Nametag",
        description = "See your own nametag in third person.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean showOwnNametag;

    @Property(
        type = PropertyType.SWITCH,
        name = "Mouse Bind Fix",
        description = "Fixes an issue where keybinds bound to mouse buttons do not work in inventories.",
        category = "Fixes",
        subcategory = "General"
    )
    public static boolean mouseBindFix = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "OptiFine Zoom Adjustment",
        description = "Scroll when using OptiFine's zoom to zoom further/farther.",
        category = "Quality of Life",
        subcategory = "OptiFine"
    )
    public static boolean scrollToZoom = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Optifine Zoom Sensitivity",
        description = "Remove the smoothing from OptiFine's zoom.",
        category = "Quality of Life",
        subcategory = "OptiFine"
    )
    public static boolean normalZoomSensitivity;

    public PatcherConfig() {
        super(new File("./config/patcher.toml"));
        initialize();
    }
}
