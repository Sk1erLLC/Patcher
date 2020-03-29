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
            description = "Stop your actual position being a tick ahead of your crosshair.",
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

    @Property(
        type = PropertyType.SLIDER,
        name = "Sprinting FOV",
        description = "Change your FOV when sprinting.",
        category = "Quality of Life",
        subcategory = "Field of View",
        min = -5,
        max = 5
    )
    public static int sprintingFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER,
        name = "Bow FOV",
        description = "Change your FOV when pulling back a bow.",
        category = "Quality of Life",
        subcategory = "Field of View",
        min = -5,
        max = 5
    )
    public static int bowFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER,
        name = "Speed FOV",
        description = "Change your FOV when having the speed effect.",
        category = "Quality of Life",
        subcategory = "Field of View",
        min = -5,
        max = 5
    )
    public static int speedFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER,
        name = "Slowness FOV",
        description = "Change your FOV when having the slowness effect.",
        category = "Quality of Life",
        subcategory = "Field of View",
        min = -5,
        max = 5
    )
    public static int slownessFovModifier = 1;

    @Property(
        type = PropertyType.SWITCH,
        name = "Custom Tab Opacity",
        description = "Allow for customizing tab opacity.",
        category = "Quality of Life",
        subcategory = "Tab"
    )
    public static boolean customTabOpacity = false;

    @Property(
        type = PropertyType.SLIDER,
        name = "Tab Opacity",
        description = "Change the tab list opacity.",
        category = "Quality of Life",
        subcategory = "Tab",
        max = 100
    )
    public static int tabOpacity = 100;

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
            subcategory = "Tab"
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
        subcategory = "Tab"
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

    @Property(
        type = PropertyType.SWITCH,
        name = "Compact Chat",
        description = "Clean up chat by stacking duplicate messages.",
        category = "Quality of Life",
        subcategory = "Chat"
    )
    public static boolean compactChat = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Number Ping",
        description = "Show a readable ping number in tab instead of bars.\n§cMay turn out to be 1 on Hypixel in most cases.",
        category = "Quality of Life",
        subcategory = "Tab"
    )
    public static boolean numberPing = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Numerical Enchantments",
        description = "Use normal numbers instead of roman numerals on enchants.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean romanNumerals = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Startup Notification",
        description = "Notify how long the game took to startup with a notification.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean startupNotification = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Keep Shaders on Perspective Change",
        description = "Keep the shaders you're currently using while also being able to toggle perspective.",
        category = "Quality of Life",
        subcategory = "General"
    )
    public static boolean keepShadersOnPerspectiveChange = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Better Keybind Handling",
        description = "Make keys re-register when closing a GUI, like in 1.12+.",
        category = "Quality of Life",
        subcategory = "General"
    )
    public static boolean newKeybindHandling = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Shadowed Text",
        description = "Remove the shadow from text, resulting in fewer draw calls.",
        category = "Performance",
        subcategory = "Rendering"
    )
    public static boolean disableShadowedText;

    @Property(
        type = PropertyType.SWITCH,
        name = "Damage Glance",
        description = "View the damage value of a currently held item above your hotbar.",
        category = "Quality of Life",
        subcategory = "Hotbar Utilities"
    )
    public static boolean damageGlance = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Item Count Glance",
        description = "View the amount of a currently held item you have above your hotbar.",
        category = "Quality of Life",
        subcategory = "Hotbar Utilities"
    )
    public static boolean itemCountGlance = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Enchantment Glance",
        description = "View the enchantments of the currently held item above your hotbar.",
        category = "Quality of Life",
        subcategory = "Hotbar Utilities"
    )
    public static boolean enchantmentsGlance = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Position",
        description = "Move the chat up 12 pixels to stop it from overlapping the health bar, as done in 1.12+.",
        category = "Quality of Life",
        subcategory = "Chat"
    )
    public static boolean chatPosition = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Timestamps",
        description = "Add timestamps before a message.",
        category = "Quality of Life",
        subcategory = "Chat"
    )
    public static boolean timestamps;

    @Property(
        type = PropertyType.SWITCH,
        name = "Transparent Nametags",
        description = "Remove the box around a nametag.",
        category = "Quality of Life",
        subcategory = "Rendering"
    )
    public static boolean transparentNameTags;

    public PatcherConfig() {
        super(new File("./config/patcher.toml"));
        initialize();
    }
}
