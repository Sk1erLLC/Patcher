/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.config;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;

@SuppressWarnings("unused")
public class PatcherConfig extends Vigilant {
    @Property(
        type = PropertyType.SWITCH, name = "Fullscreen Fix",
        description = "Allow the screen to be resized after toggling fullscreen.",
        category = "Fixes", subcategory = "Screen"
    )
    public static boolean fullscreenFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Item Searching",
        description = "Minecraft doesn't stop searching for other items to combine despite being a full stack.",
        category = "Performance", subcategory = "Items"
    )
    public static boolean searchingOptimizationFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Reset Death Timers",
        description = "Allow for respawning when toggling fullscreen on the \"You Died!\" menu.",
        category = "Fixes", subcategory = "Screen"
    )
    public static boolean resetDeathTimers = true;

    @Property(
        type = PropertyType.SWITCH, name = "Command Handling",
        description = "Fix Forge's command handler not checking for a '/' at the start of a command.",
        category = "Fixes", subcategory = "Chat"
    )
    public static boolean forgeCommandHandling = true;

    @Property(
        type = PropertyType.SWITCH, name = "Case Insensitive Commands",
        description = "Allow for case insensitivity.",
        category = "Fixes", subcategory = "Chat"
    )
    public static boolean caseInsensitiveCommands = true;

    @Property(
        type = PropertyType.SWITCH, name = "Inventory Position",
        description = "Stop potion effects from shifting your inventory to the right.",
        category = "Quality of Life", subcategory = "Inventory"
    )
    public static boolean inventoryPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Mouse Delay Fix",
        description = "Stop your actual position being a tick ahead of your crosshair.",
        category = "Fixes", subcategory = "Movement"
    )
    public static boolean mouseDelayFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Arm Position",
        description = "Reset the player state properly once mounting an entity.",
        category = "Fixes", subcategory = "Movement"
    )
    public static boolean armPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Head Rotations",
        description = "Properly rotate the users head while mounting an entity.",
        category = "Fixes", subcategory = "Movement"
    )
    public static boolean headRotation = true;

    @Property(
        type = PropertyType.SWITCH, name = "Fullbright",
        description = "Remove lighting updates, increasing visibility.\n§eMay improve performance.",
        category = "Quality of Life", subcategory = "World"
    )
    public static boolean fullbright = true;

    @Property(
        type = PropertyType.SWITCH, name = "Sky Height",
        description = "Set the sky height to 0, removing void flickering.",
        category = "Quality of Life", subcategory = "World"
    )
    public static boolean skyHeight = true;

    @Property(
        type = PropertyType.SWITCH, name = "Instant World Swapping",
        description = "Remove waiting times between swapping worlds.",
        category = "Quality of Life", subcategory = "World"
    )
    public static boolean instantWorldSwapping = true;

    @Property(
        type = PropertyType.SWITCH, name = "Nausea Effect",
        description = "Remove the nether portal appearing when clearing nausea.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean nauseaEffect = true;

    @Property(
        type = PropertyType.SWITCH, name = "Scoreboard Patch",
        description = "Fix scoreboard spamming logs with errors.",
        category = "Fixes", subcategory = "General"
    )
    public static boolean patchInternalErrors = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Achievements",
        description = "Remove achievement notifications.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean disableAchievements = true;

    @Property(
        type = PropertyType.SWITCH, name = "Container Backgrounds",
        description = "Remove the dark background inside a container.",
        category = "Quality of Life", subcategory = "Inventory"
    )
    public static boolean disableTransparentBackgrounds = false;

    @Property(
        type = PropertyType.SLIDER, name = "Fire Overlay Height",
        description = "Change the height of the ingame fire overlay.",
        category = "Quality of Life", subcategory = "Overlay",
        min = -200, max = 200
    )
    public static int fireHeight;

    @Property(
        type = PropertyType.SLIDER, name = "Chat History Length",
        description = "Change how many messages you can scroll back past.",
        category = "Quality of Life", subcategory = "Chat",
        min = 100, max = 10000
    )
    public static int chatHistoryLength = 100;

    @Property(
        type = PropertyType.SWITCH, name = "FOV Modifier",
        description = "Allow for modifying FOV change states.",
        category = "Quality of Life", subcategory = "Field of View"
    )
    public static boolean allowFovModifying;

    @Property(
        type = PropertyType.SLIDER, name = "Sprinting FOV",
        description = "Change your FOV when sprinting.",
        category = "Quality of Life", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int sprintingFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Bow FOV",
        description = "Change your FOV when pulling back a bow.",
        category = "Quality of Life", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int bowFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Speed FOV",
        description = "Change your FOV when having the speed effect.",
        category = "Quality of Life", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int speedFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Slowness FOV",
        description = "Change your FOV when having the slowness effect.",
        category = "Quality of Life", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int slownessFovModifier = 1;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Water FOV",
        description = "Remove the change of FOV when underwater.\n§eDoes not require FOV Modifier to be enabled.",
        category = "Quality of Life", subcategory = "Field of View"
    )
    public static boolean removeWaterFov;

    @Property(
        type = PropertyType.SWITCH, name = "Custom Tab Opacity",
        description = "Allow for customizing tab opacity.",
        category = "Quality of Life", subcategory = "Tab"
    )
    public static boolean customTabOpacity = false;

    @Property(
        type = PropertyType.SLIDER, name = "Tab Opacity",
        description = "Change the tab list opacity.",
        category = "Quality of Life", subcategory = "Tab",
        max = 100
    )
    public static int tabOpacity = 100;

    @Property(
        type = PropertyType.SWITCH, name = "Downscale Pack Images",
        description = "Change all pack icons to 64x64 to improve memory usage.",
        category = "Performance", subcategory = "Resources"
    )
    public static boolean downscalePackImages = true;

    @Property(
        type = PropertyType.SWITCH, name = "Toggle Tab",
        description = "Hold tab open with a single keypress.",
        category = "Quality of Life", subcategory = "Tab"
    )
    public static boolean toggleTab;

    @Property(
        type = PropertyType.SWITCH, name = "Crosshair Perspective",
        description = "Remove the crosshair when in third person.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean crosshairPerspective;

    @Property(
        type = PropertyType.SWITCH, name = "GUI Crosshair",
        description = "Stop rendering the crosshair when in a GUI.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean guiCrosshair;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Tall Grass",
        description = "Stop tall grass/double tall plants from rendering.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean removeTallGrass;

    @Property(
        type = PropertyType.SWITCH, name = "Transparent Chat",
        description = "Remove the background from chat.\n§eMay improve performance.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean transparentChat;

    @Property(
        type = PropertyType.SWITCH, name = "Transparent Chat Field",
        description = "Remove the background from chat's input field.\n§eMay improve performance.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean transparentChatInputField;

    @Property(
        type = PropertyType.SWITCH, name = "Tab Height",
        description = "Move the tab overlay down n pixels when there's an active bossbar.",
        category = "Quality of Life", subcategory = "Tab"
    )
    public static boolean tabHeightAllow = true;

    @Property(
        type = PropertyType.SLIDER, name = "Set Tab Height",
        description = "Choose how many pixels down the tab will go when there's an active bossbar.",
        category = "Quality of Life", subcategory = "Tab",
        max = 24
    )
    public static int tabHeight = 10;

    @Property(
        type = PropertyType.SWITCH, name = "Constant Fog Color Checking",
        description = "Disable constant fog color checking.",
        category = "Performance", subcategory = "World"
    )
    public static boolean disableConstantFogColorChecking = true;

    @Property(
        type = PropertyType.SWITCH, name = "Low Animation Tick",
        description = "Lowers world particle count.",
        category = "Performance", subcategory = "World"
    )
    public static boolean lowAnimationTick = true;

    @Property(
        type = PropertyType.SWITCH, name = "Single Model Render Call",
        description = "Render entire model in a single draw call.",
        category = "Performance", subcategory = "World"
    )
    public static boolean singleModelCall = true;

    @Property(
        type = PropertyType.SWITCH, name = "Static Particle Color",
        description = "Disable particle lighting checks each frame.",
        category = "Performance", subcategory = "Particles"
    )
    public static boolean staticParticleColor = true;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Font Renderer",
        description = "Use a more efficient font renderer.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean optimizedFontRenderer = true;

    @Property(
        type = PropertyType.SWITCH, name = "Cache Font Data",
        description = "Cache font data allowing for it to be reused multiple times before needing recalculation.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean cacheFontData = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Armorstands",
        description = "Stop armorstands from rendering.\nArmorstands are commonly used for NPC nametag rendering. Enabling this will stop those from rendering as well.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableArmorstands;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Semitransparent Players",
        description = "Stop semi-transparent players from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableSemitransparentEntities;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Enchantment Books",
        description = "Stop enchantment table books from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableEnchantmentBooks;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Item Frames",
        description = "Stop item frames from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableItemFrames;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Grounded Arrows",
        description = "Stop arrows that are in the ground from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableGroundedArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Attached Arrows",
        description = "Stop arrows that are attached to a player from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableAttachedArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Moving Arrows",
        description = "Stop arrows that are airborne from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableMovingArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Skulls",
        description = "Stop skulls from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableSkulls;

    @Property(
        type = PropertyType.SWITCH, name = "Disable End Portals",
        description = "Stop end portals from rendering.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableEndPortals;

    @Property(
        type = PropertyType.SWITCH, name = "Show Own Nametag",
        description = "See your own nametag in third person.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean showOwnNametag;

    @Property(
        type = PropertyType.SWITCH, name = "Mouse Bind Fix",
        description = "Fixes an issue where keybinds bound to mouse buttons do not work in inventories.",
        category = "Fixes", subcategory = "General"
    )
    public static boolean mouseBindFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "OptiFine Zoom Adjustment",
        description = "Scroll when using OptiFine's zoom to adjust the zoom level.",
        category = "Quality of Life", subcategory = "OptiFine"
    )
    public static boolean scrollToZoom = true;

    @Property(
        type = PropertyType.SWITCH, name = "OptiFine Zoom Sensitivity",
        description = "Remove the smoothing from OptiFine's zoom.",
        category = "Quality of Life", subcategory = "OptiFine"
    )
    public static boolean normalZoomSensitivity;

    @Property(
            type = PropertyType.SWITCH, name = "Simplify OptiFine FPS Counter",
            description = "Remove the additions OptiFine L5 makes to the debug screen fps counter.",
            category = "Quality of Life", subcategory = "OptiFine"
    )
    public static boolean normalFpsCounter = true;

    @Property(
        type = PropertyType.SWITCH, name = "Compact Chat",
        description = "Clean up chat by stacking duplicate messages.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean compactChat = true;

    @Property(
        type = PropertyType.SLIDER, name = "Super Compact Chat",
        description = "Clean up chat by stacking duplicate messages found within the select range.",
        category = "Quality of Life", subcategory = "Chat",
        min = 1, max = 25
    )
    public static int superCompactChatAmount = 5;

    @Property(
        type = PropertyType.SWITCH, name = "AntiClearChat",
        description = "Remove blank messages from chat.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean antiClearChat = true;

    @Property(
        type = PropertyType.SWITCH, name = "Number Ping",
        description = "Show a readable ping number in tab instead of bars.\n§cMay turn out to be 1 on Hypixel in most cases.",
        category = "Quality of Life", subcategory = "Tab"
    )
    public static boolean numberPing = true;

    @Property(
        type = PropertyType.SWITCH, name = "Numerical Enchantments",
        description = "Use normal numbers instead of roman numerals on enchants.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean romanNumerals = true;

    @Property(
        type = PropertyType.SWITCH, name = "Startup Notification",
        description = "Notify how long the game took to startup with a notification.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean startupNotification = true;

    @Property(
        type = PropertyType.SWITCH, name = "Keep Shaders on Perspective Change",
        description = "Keep the shaders you're currently using while also being able to toggle perspective.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean keepShadersOnPerspectiveChange = true;

    @Property(
        type = PropertyType.SWITCH, name = "Better Keybind Handling",
        description = "Make keys re-register when closing a GUI, like in 1.12+.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean newKeybindHandling = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Shadowed Text",
        description = "Remove shadows from text.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableShadowedText;

    @Property(
        type = PropertyType.SWITCH, name = "Damage Glance",
        description = "View the damage value of the currently held item above your hotbar.",
        category = "Quality of Life", subcategory = "Combat Utilities"
    )
    public static boolean damageGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Item Count Glance",
        description = "View the amount of the currently held item above your hotbar.",
        category = "Quality of Life", subcategory = "Combat Utilities"
    )
    public static boolean itemCountGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Enchantment Glance",
        description = "View the enchantments of the currently held item above your hotbar.",
        category = "Quality of Life", subcategory = "Combat Utilities"
    )
    public static boolean enchantmentsGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Protection Percentage",
        description = "View how much total protection you have inside the inventory menu.",
        category = "Quality of Life", subcategory = "Combat Utilities"
    )
    public static boolean protectionPercentage = true;

    @Property(
        type = PropertyType.SWITCH, name = "Projectile Protection Percentage",
        description = "View how much total projectile protection you have inside the inventory menu.",
        category = "Quality of Life", subcategory = "Combat Utilities"
    )
    public static boolean projectileProtectionPercentage = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Position",
        description = "Move the chat up 12 pixels to stop it from overlapping the health bar, as done in 1.12+.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean chatPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Timestamps",
        description = "Add timestamps before a message.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean timestamps;

    @Property(
        type = PropertyType.SWITCH, name = "Transparent Nametags",
        description = "Remove boxes around nametags.\n§eMay improve performance.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean transparentNameTags;

    @Property(
        type = PropertyType.SWITCH, name = "Arrow Lighting",
        description = "Stop attached arrows from lighting up other entities.",
        category = "Fixes", subcategory = "Entities"
    )
    public static boolean fixArrowLighting;

    @Property(
        type = PropertyType.SWITCH, name = "Entity Culling",
        description = "Stop entities that aren't visible to the player from rendering.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean entityCulling = true;



    @Property(
        type = PropertyType.SWITCH, name = "Don't Cull Nametags",
        description = "Render nametags even with entity culling enabled.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullNametags;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Water Overlay",
        description = "Remove the water texture overlay when underwater.",
        category = "Quality of Life", subcategory = "Overlay"
    )
    public static boolean removeWaterOverlay;

    @Property(
        type = PropertyType.SWITCH, name = "CleanView",
        description = "Stop self-potion effects from rendering.",
        category = "Quality of Life", subcategory = "Rendering"
    )
    public static boolean cleanView;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Enchantment Glint",
        description = "Disable the enchantment glint on enchanted items/potions.",
        category = "Performance", subcategory = "Rendering"
    )
    public static boolean disableEnchantmentGlint;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Cloud Renderer",
        description = "Upload geometry to the GPU, allowing for much faster rendering.\n§eFor best results, have a dedicated GPU.",
        category = "Performance", subcategory = "World"
    )
    public static boolean gpuCloudRenderer = true;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Cloud Transparency",
        description = "Remove transparency from clouds.",
        category = "Performance", subcategory = "World"
    )
    public static boolean removeCloudTransparency;

    @Property(
        type = PropertyType.SWITCH, name = "CrossChat",
        description = "Stop clearing chat when switching servers.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean crossChat = true;

    @Property(
        type = PropertyType.SWITCH, name = "Clean Main Menu",
        description = "Remove the Realms button as you need to be on the latest Minecraft version to use Realms.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean cleanMainMenu = true;

    @Property(
        type = PropertyType.SWITCH, name = "Windowed Fullscreen",
        description = "Implement Windowed Fullscreen in Minecraft allowing you to drag your mouse outside the window",
        category = "Quality of Life", subcategory = "Fullscreen"
    )
    public static boolean windowedFullscreen;

    @Property(
        type = PropertyType.SWITCH, name = "Instant Fullscreen (Windows Only)",
        description = "Instant switching between full screen and non fullscreen modes.\nWindowed Fullscreen must also be enabled for this to work.",
        category = "Quality of Life", subcategory = "Fullscreen"
    )
    public static boolean instantFullscreen;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Breaking Particles",
        description = "Remove block breaking particles for visibility.",
        category = "Performance", subcategory = "Particles"
    )
    public static boolean disableBlockBreakParticles;

    @Property(
        type = PropertyType.SWITCH, name = "Screenshot Manager",
        description = "Overwrite the screenshotting experience as a whole.",
        category = "Quality of Life", subcategory = "Screenshot Utilities"
    )
    public static boolean screenshotManager = true;

    @Property(
        type = PropertyType.SWITCH, name = "No Feedback",
        description = "Remove messages from screenshots entirely.",
        category = "Quality of Life", subcategory = "Screenshot Utilities"
    )
    public static boolean screenshotNoFeedback;

    @Property(
        type = PropertyType.SWITCH, name = "Screenshot Preview",
        description = "Preview the look of your screenshot when taken.",
        category = "Quality of Life", subcategory = "Screenshot Utilities"
    )
    public static boolean screenshotPreview;

    @Property(
        type = PropertyType.SWITCH, name = "Compact Response",
        description = "Compact the feedback given when screenshotting.",
        category = "Quality of Life", subcategory = "Screenshot Utilities"
    )
    public static boolean compactScreenshotResponse;

    @Property(
        type = PropertyType.SWITCH, name = "Disable GL Error Checking",
        description = "Disable unnecessary constant checking for errors in OpenGL.\n§cRequires restart once toggled.",
        category = "Performance", subcategory = "General"
    )
    public static boolean glErrorChecking = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Keeper",
        description = "Keep chat when toggling fullscreen.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean chatKeeper = true;

    @Property(
        type = PropertyType.SWITCH, name = "Log Optimizer",
        description = "Toggle log optimizing.\n§cThese files are not recoverable once deleted.",
        category = "Quality of Life", subcategory = "Cleaner"
    )
    public static boolean logOptimizer;

    @Property(
        type = PropertyType.SLIDER, name = "Log Optimizer Amount",
        description = "Choose how many days old a file should be before deleted.",
        category = "Quality of Life", subcategory = "Cleaner",
        min = 1, max = 90
    )
    public static int logOptimizerLength = 30;

    @Property(
        type = PropertyType.SWITCH, name = "Skin Refresher",
        description = "Refresh your current skin without needing to leave the server.\n§eAlso accessible with the command \"/refreshskin\".",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean skinRefresher = true;

    @Property(
        type = PropertyType.SWITCH, name = "Parallax Fix",
        description = "Fix the camera being too far back, seemingly making your eyes be in the back of your head.",
        category = "Fixes", subcategory = "General"
    )
    public static boolean parallaxFix;

    @Property(
        type = PropertyType.SWITCH, name = "Culling Fix",
        description = "Fix false negatives in frustum culling check, fixing sometimes invisible chunks.\n§cMay affect performance.",
        category = "Fixes", subcategory = "General"
    )
    public static boolean cullingFix;

    @Property(
        type = PropertyType.SWITCH, name = "Replace Open to Lan",
        description = "Remove the 'Open to Lan' button when in a multiplayer server with a server list button.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean replaceOpenToLan;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Inverted Colors from Crosshair",
        description = "Remove the inverted color effect on the crosshair.",
        category = "Quality of Life", subcategory = "General"
    )
    public static boolean removeInvertFromCrosshair;

    @Property(
        type = PropertyType.SWITCH, name = "Image Preview",
        description = "Preview image links sent in chat.\nPress Shift to use fullscreen and Control to render in native image resolution.",
        category = "Quality of Life", subcategory = "Image Preview"
    )
    public static boolean imagePreview = true;

    @Property(
        type = PropertyType.SLIDER, name = "Image Preview Width",
        description = "The % of screen width to be used for image preview.",
        category = "Quality of Life", subcategory = "Image Preview",
        min = 10, max = 100
    )
    public static int imagePreviewWidth = 50;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Item Renderer",
        description = "Cache information about items, avoiding recalculating everything about it every frame.",
        category = "Performance", subcategory = "Items"
    )
    public static boolean optimizedItemRenderer = true;

    @Property(
        type = PropertyType.SWITCH, name = "1.12 Farm Selection Boxes",
        description = "Replaces the selection box for crops with the 1.12 variant.\n§eOnly works on Hypixel & Singleplayer.",
        category = "Quality of Life", subcategory = "Blocks"
    )
    public static boolean futureHitBoxes = true;

    @Property(
        type = PropertyType.SWITCH, name = "Particle Culling",
        description = "Stop particles that aren't visible to the player from rendering.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean cullParticles = true;

    @Property(
        type = PropertyType.SWITCH, name = "Resource Exploit Fix",
        description = "Fix an exploit in 1.8 allowing servers to look through directories.",
        category = "Fixes", subcategory = "Security"
    )
    public static boolean resourceExploitFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Mob Spawning",
        description = "Reduce memory usage by disabling the check for mob spawning despite the set game rule.\n§eThis will disable mob spawning in a singleplayer world.",
        category = "Performance", subcategory = "World"
    )
    public static boolean mobSpawningOptimization;

    @Property(
            type = PropertyType.SLIDER, name = "Entity Render Distance",
            description = "Stop rendering entities outside of the specified radius.",
            category = "Performance", subcategory = "Rendering",
            min = 1, max = 64
    )
    public static int entityRenderDistance = 64;

    @Property(
            type = PropertyType.SWITCH, name = "Entity Render Distance Toggle",
            description = "Toggle allowing a custom entity render distance.",
            category = "Performance", subcategory = "Rendering"
    )
    public static boolean entityRenderDistanceToggle;

    @Property(
        type = PropertyType.SWITCH, name = "Safe Chat Clicks",
        description = "Show the command or link that is ran/opened on click.",
        category = "Quality of Life", subcategory = "Chat"
    )
    public static boolean safeChatClicks;

    /*@Property(
        type = PropertyType.SWITCH, name = "Optimized Model Generation",
        description = "Reduce the amount of quads generated on item models, reducing memory usage.\n§cToggling this requires a restart.",
        category = "Performance", subcategory = "Models"
    )
    public static boolean optimizedModelGeneration;*/

    public PatcherConfig() {
        super(new File("./config/patcher.toml"));
        initialize();
    }
}
