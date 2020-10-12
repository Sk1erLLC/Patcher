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

import java.io.File;

@SuppressWarnings("unused")
public class PatcherConfig extends Vigilant {

    // BUG FIXES

    @Property(
        type = PropertyType.SWITCH, name = "Fullscreen Fix",
        description = "Resolve an issue where you could not maximize the game once toggling fullscreen.",
        category = "Bug Fixes", subcategory = "Window"
    )
    public static boolean fullscreenFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Reset Death Timers",
        description = "Resolve an issue where changing the fullscreen state while on the Game Over screen would lock the buttons.",
        category = "Bug Fixes", subcategory = "Screen"
    )
    public static boolean resetDeathTimers = true;

    @Property(
        type = PropertyType.SWITCH, name = "Command Handling",
        description = "Fix Forge's command handler not checking for a '/' at the start of a command.",
        category = "Bug Fixes", subcategory = "Chat"
    )
    public static boolean forgeCommandHandling = true;

    @Property(
        type = PropertyType.SWITCH, name = "Case Insensitive Commands",
        description = "Stop Vanilla commands from forcing case sensitivity.",
        category = "Bug Fixes", subcategory = "Chat"
    )
    public static boolean caseInsensitiveCommands = true;

    @Property(
        type = PropertyType.SWITCH, name = "Mouse Delay Fix",
        description = "Resolve an issue where your crosshair is a tick behind your head position.",
        category = "Bug Fixes", subcategory = "Movement"
    )
    public static boolean mouseDelayFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Arm Rotation",
        description = "Resolve an issue where your arm rotation would be angled upwards when mounting an entity.",
        category = "Bug Fixes", subcategory = "Movement"
    )
    public static boolean armPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Head Rotations",
        description = "Resolve an issue where your head would not properly rotate while riding an entity.",
        category = "Bug Fixes", subcategory = "Movement"
    )
    public static boolean headRotation = true;

    @Property(
        type = PropertyType.SWITCH, name = "Sky Height",
        description = "Remove the flickering effect from the void when passing between Y level 63.",
        category = "Bug Fixes", subcategory = "World"
    )
    public static boolean skyHeight = true;

    @Property(
        type = PropertyType.SWITCH, name = "Mouse Bind Fix",
        description = "Fixes an issue where keybinds bound to mouse buttons do not work in inventories.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean mouseBindFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Keep Shaders on Perspective Change",
        description = "Keep the Vanilla shaders you're currently using while also being able to toggle perspective.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean keepShadersOnPerspectiveChange = true;

    @Property(
        type = PropertyType.SWITCH, name = "Better Keybind Handling",
        description = "Make keys re-register when closing a GUI, like in 1.12+.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean newKeybindHandling = true;

    @Property(
        type = PropertyType.SWITCH, name = "Arrow Lighting",
        description = "Stop arrows attached to an entity from messing up entity lighting.",
        category = "Bug Fixes", subcategory = "Entities"
    )
    public static boolean fixArrowLighting;

    @Property(
        type = PropertyType.SWITCH, name = "Parallax Fix",
        description = "Fix the camera being too far back, seemingly making your eyes be in the back of your head.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean parallaxFix;

    @Property(
        type = PropertyType.SWITCH, name = "Culling Fix",
        description = "Fix false negatives in frustum culling, creating sometimes invisible chunks.\n§cCan negatively impact performance.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean cullingFix;

    @Property(
        type = PropertyType.SWITCH, name = "Resource Exploit Fix",
        description = "Fix an exploit in 1.8 allowing servers to look through directories.",
        category = "Bug Fixes", subcategory = "Security"
    )
    public static boolean resourceExploitFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Layers In Tab",
        description = "Fixes players sometimes not having a hat layer in Tab.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean layersInTab = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chunk Lighting",
        description = "Fixes chunks sometimes not displaying any light when empty.\n§cCan negatively impact performance.",
        category = "Bug Fixes", subcategory = "Lighting"
    )
    public static boolean chunkLighting = true;

    @Property(
        type = PropertyType.SWITCH, name = "Player Void Rendering",
        description = "Remove the black box around the player while in the void.",
        category = "Bug Fixes", subcategory = "Rendering"
    )
    public static boolean playerVoidRendering = true;

    // MISCELLANEOUS

    @Property(
        type = PropertyType.SWITCH, name = "Fullbright",
        description = "Remove lighting updates, increasing visibility.\n§eCan positively impact performance.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean fullbright = true;

    @Property(
        type = PropertyType.SWITCH, name = "Smart Fullbright",
        description = "Automatically disable the Fullbright effect when using OptiFine shaders.\n§eRequires Fullbright.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean smartFullbright = true;

    @Property(
        type = PropertyType.SWITCH, name = "Nausea Effect",
        description = "Remove the nether portal appearing when clearing nausea.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean nauseaEffect = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Achievements",
        description = "Remove achievement notifications.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean disableAchievements = true;

    @Property(
        type = PropertyType.SLIDER, name = "Fire Overlay Height",
        description = "Change the height of the fire overlay.",
        category = "Miscellaneous", subcategory = "Overlays",
        min = -200, max = 200
    )
    public static int fireHeight;

    @Property(
        type = PropertyType.SWITCH, name = "FOV Modifier",
        description = "Allow for modifying FOV change states.",
        category = "Miscellaneous", subcategory = "Field of View"
    )
    public static boolean allowFovModifying;

    @Property(
        type = PropertyType.SLIDER, name = "Sprinting FOV",
        description = "Modify your FOV when sprinting.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int sprintingFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Bow FOV",
        description = "Modify your FOV when pulling back a bow.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int bowFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Speed FOV",
        description = "Modify your FOV when having the speed effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int speedFovModifier = 1;

    @Property(
        type = PropertyType.SLIDER, name = "Slowness FOV",
        description = "Modify your FOV when having the slowness effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static int slownessFovModifier = 1;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Water FOV",
        description = "Remove the change of FOV when underwater.\n§eDoes not require FOV Modifier to be enabled.",
        category = "Miscellaneous", subcategory = "Field of View"
    )
    public static boolean removeWaterFov;

    @Property(
        type = PropertyType.SWITCH, name = "Toggle Tab",
        description = "Hold tab open without needing to hold down the tab key.",
        category = "Miscellaneous", subcategory = "Tab"
    )
    public static boolean toggleTab;

    @Property(
        type = PropertyType.SWITCH, name = "Crosshair Perspective",
        description = "Remove the crosshair when in third person.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean crosshairPerspective;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Ground Foliage",
        description = "Stop plants/flowers from rendering.",
        category = "Miscellaneous", subcategory = "Blocks"
    )
    public static boolean removeGroundFoliage;

    @Property(
        type = PropertyType.SWITCH, name = "Show Own Nametag",
        description = "See your own nametag in third person.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean showOwnNametag;

    @Property(
        type = PropertyType.SWITCH, name = "OptiFine Zoom Adjustment",
        description = "Scroll when using OptiFine's zoom to adjust the zoom level.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean scrollToZoom = true;

    @Property(
        type = PropertyType.SWITCH, name = "OptiFine Zoom Sensitivity",
        description = "Remove the smooth camera effect when using OptiFine zoom.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean normalZoomSensitivity;

    @Property(
        type = PropertyType.SWITCH, name = "Simplify OptiFine FPS Counter",
        description = "Remove the additions OptiFine L5 and above makes to the debug screen fps counter.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean normalFpsCounter = true;

    @Property(
        type = PropertyType.SWITCH, name = "Number Ping",
        description = "Show a readable ping number in tab instead of bars.",
        category = "Miscellaneous", subcategory = "Tab"
    )
    public static boolean numberPing = true;

    @Property(
        type = PropertyType.SWITCH, name = "Numerical Enchantments",
        description = "Use readable numbers instead of roman numerals on enchants.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean romanNumerals = true;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Water Overlay",
        description = "Remove the water texture overlay when underwater.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean removeWaterOverlay;

    @Property(
        type = PropertyType.SWITCH, name = "Clean View",
        description = "Stop rendering your own potion effect particles.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean cleanView;

    @Property(
        type = PropertyType.SWITCH, name = "Windowed Fullscreen",
        description = "Implement Windowed Fullscreen in Minecraft allowing you to drag your mouse outside the window",
        category = "Miscellaneous", subcategory = "Window"
    )
    public static boolean windowedFullscreen;

    @Property(
        type = PropertyType.SWITCH, name = "Instant Fullscreen (Windows Only)",
        description = "Instant switching between full screen and non fullscreen modes.\nWindowed Fullscreen must also be enabled for this to work.",
        category = "Miscellaneous", subcategory = "Window"
    )
    public static boolean instantFullscreen;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Breaking Particles",
        description = "Remove block breaking particles for visibility.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean disableBlockBreakParticles;

    @Property(
        type = PropertyType.SWITCH, name = "Log Optimizer",
        description = "Delete any files in the logs folder, as this usually can take up a lot of space.\n§cThese files are not recoverable once deleted.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean logOptimizer;

    @Property(
        type = PropertyType.SLIDER, name = "Log Optimizer Amount",
        description = "Choose how many days old a file must be before deleted.",
        category = "Miscellaneous", subcategory = "General",
        min = 1, max = 90
    )
    public static int logOptimizerLength = 30;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Inverted Colors from Crosshair",
        description = "Remove the inverted color effect on the crosshair.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean removeInvertFromCrosshair;

    @Property(
        type = PropertyType.SWITCH, name = "1.12 Farm Selection Boxes",
        description = "Replaces the selection box for crops with the 1.12 variant.\n§eOnly works on Hypixel & Singleplayer.\nWill be moved to Hytilities in the future.",
        category = "Miscellaneous", subcategory = "Blocks"
    )
    public static boolean futureHitBoxes = true;

    @Property(
        type = PropertyType.SWITCH, name = "Clean Text Shadow",
        description = "Change the text shadow to only move down rather than moving to the side.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean cleanTextShadow;

    // PERFORMANCE

    @Property(
        type = PropertyType.SWITCH, name = "Item Searching",
        description = "Stop items from searching for extra items to combine with when the stack is already full.",
        category = "Performance", subcategory = "Items"
    )
    public static boolean searchingOptimizationFix = true;

    @Property(
        type = PropertyType.SWITCH, name = "Instant World Swapping",
        description = "Remove the dirt screen and waiting time when switching a world.",
        category = "Performance", subcategory = "World"
    )
    public static boolean instantWorldSwapping = true;

    @Property(
        type = PropertyType.SWITCH, name = "Downscale Pack Images",
        description = "Change all pack icons to 64x64 to reduce memory usage.",
        category = "Performance", subcategory = "Resources"
    )
    public static boolean downscalePackImages = true;

    @Property(
        type = PropertyType.SWITCH, name = "Static Fog Color",
        description = "Simplify fog color creation with a static fog color.",
        category = "Performance", subcategory = "World"
    )
    public static boolean disableConstantFogColorChecking = true;

    @Property(
        type = PropertyType.SWITCH, name = "Low Animation Tick",
        description = "Lowers the amount of animations that happen a second from 1000 to 500.",
        category = "Performance", subcategory = "World"
    )
    public static boolean lowAnimationTick = true;

    @Property(
        type = PropertyType.SWITCH, name = "Batch Model Rendering",
        description = "Render models in a single draw call, reducing the amount of OpenGL instructions performed a second.",
        category = "Performance", subcategory = "World"
    )
    public static boolean batchModelRendering = true;

    @Property(
        type = PropertyType.SWITCH, name = "Static Particle Color",
        description = "Disable particle lighting checks each frame.",
        category = "Performance", subcategory = "Particles"
    )
    public static boolean staticParticleColor = true;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Font Renderer",
        description = "Use modern rendering techniques to improve the font renderer performance.",
        category = "Performance", subcategory = "Text Rendering"
    )
    public static boolean optimizedFontRenderer = true;

    @Property(
        type = PropertyType.SWITCH, name = "Cache Font Data",
        description = "Cache font data allowing for it to be reused multiple times before needing recalculation.",
        category = "Performance", subcategory = "Text Rendering"
    )
    public static boolean cacheFontData = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Armorstands",
        description = "Stop armorstands from rendering.\nArmorstands are commonly used for NPC nametag rendering. Enabling this will stop those from rendering as well.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableArmorstands;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Semitransparent Players",
        description = "Stop semitransparent players from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableSemitransparentEntities;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Enchantment Books",
        description = "Stop enchantment table books from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableEnchantmentBooks;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Item Frames",
        description = "Stop item frames from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableItemFrames;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Grounded Arrows",
        description = "Stop arrows that are in the ground from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableGroundedArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Attached Arrows",
        description = "Stop arrows that are attached to a player from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableAttachedArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Moving Arrows",
        description = "Stop arrows that are airborne from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableMovingArrows;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Skulls",
        description = "Stop skulls from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableSkulls;

    @Property(
        type = PropertyType.SWITCH, name = "Disable End Portals",
        description = "Stop end portals from rendering.",
        category = "Performance", subcategory = "General"
    )
    public static boolean disableEndPortals;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Shadowed Text",
        description = "Remove shadows from text.",
        category = "Performance", subcategory = "Text Rendering"
    )
    public static boolean disableShadowedText;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Nametag Boxes",
        description = "Remove the transparent box around the nametag.\n§eCan positively impact performance.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableNametagBoxes;

    @Property(
        type = PropertyType.SWITCH, name = "Entity Culling",
        description = "Stop entities that aren't visible to the player from rendering.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean entityCulling = true;

    @Property(
        type = PropertyType.SELECTOR, name = "Entity Culling Interval",
        description = "The amount of time in ms between occlusion checks for entities.\nShorter periods are more costly toward performance but provide the most accurate information.\nLower values recommended in competitive environments.",
        category = "Performance", subcategory = "Culling",
        options = {"50", "25", "10"}
    )
    public static int cullingInterval = 0;

    @Property(
        type = PropertyType.SWITCH, name = "Smart Entity Culling",
        description = "Stop the entity culling effect when using OptiFine shaders.\n§cDue to the way OptiFine shaders work, we are unable to make Entity Culling compatible at this time.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean smartEntityCulling = true;

    @Property(
        type = PropertyType.SWITCH, name = "Don't Cull Player Nametags",
        description = "Render nametags even when the player and nametag are occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullNametags;

    @Property(
        type = PropertyType.SWITCH, name = "Don't Cull Entity Nametags",
        description = "Render nametags even when the entity and nametag are occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullEntityNametags;

    @Property(
        type = PropertyType.SWITCH, name = "Don't Cull Armor Stand Nametags",
        description = "Render nametags even when the armour stand is occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullArmourStandNametags;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Enchantment Glint",
        description = "Disable the enchantment glint on enchanted items/potions.",
        category = "Performance", subcategory = "General"
    )
    public static boolean disableEnchantmentGlint;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Cloud Renderer",
        description = "Use modern rendering techniques to improve cloud rendering performance.",
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
        type = PropertyType.SWITCH, name = "Disable GL Error Checking",
        description = "Disable unnecessary constant checking for errors in OpenGL.\n§cRequires restart once toggled.",
        category = "Performance", subcategory = "General"
    )
    public static boolean glErrorChecking = true;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Item Renderer",
        description = "Cache information about items, avoiding recalculating everything about it every frame.",
        category = "Performance", subcategory = "Items"
    )
    public static boolean optimizedItemRenderer = true;

    @Property(
        type = PropertyType.SWITCH, name = "Particle Culling",
        description = "Stop particles that aren't visible to the player from rendering.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean cullParticles = true;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Mob Spawning",
        description = "Reduce memory usage by disabling the check for mob spawning despite the set game rule.\n§eThis will disable mob spawning in a singleplayer world.",
        category = "Performance", subcategory = "World"
    )
    public static boolean mobSpawningOptimization;

    @Property(
        type = PropertyType.SLIDER, name = "Entity Render Distance",
        description = "Stop rendering entities outside of the specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int entityRenderDistance = 64;

    @Property(
        type = PropertyType.SWITCH, name = "Entity Render Distance Toggle",
        description = "Toggle allowing a custom entity render distance.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean entityRenderDistanceToggle;

    // SCREENS

    @Property(
        type = PropertyType.SWITCH, name = "Inventory Position",
        description = "Stop potion effects from shifting your inventory to the right.",
        category = "Screens", subcategory = "Inventory"
    )
    public static boolean inventoryPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Container Backgrounds",
        description = "Remove the dark background inside of a container.",
        category = "Screens", subcategory = "General"
    )
    public static boolean disableTransparentBackgrounds = false;

    @Property(
        type = PropertyType.SLIDER, name = "Chat History Length",
        description = "Change how many messages you can scroll back past.",
        category = "Screens", subcategory = "Chat",
        min = 100, max = 10000
    )
    public static int chatHistoryLength = 100;

    @Property(
        type = PropertyType.SWITCH, name = "Custom Tab Opacity",
        description = "Allow for customizing tab opacity.",
        category = "Screens", subcategory = "Tab"
    )
    public static boolean customTabOpacity = false;

    @Property(
        type = PropertyType.SLIDER, name = "Tab Opacity",
        description = "Change the tab list opacity.",
        category = "Screens", subcategory = "Tab",
        max = 100
    )
    public static int tabOpacity = 100;

    @Property(
        type = PropertyType.SWITCH, name = "GUI Crosshair",
        description = "Stop rendering the crosshair when in a GUI.",
        category = "Screens", subcategory = "General"
    )
    public static boolean guiCrosshair;

    @Property(
        type = PropertyType.SWITCH, name = "Transparent Chat",
        description = "Remove the background from chat.\n§eCan positively impact performance.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean transparentChat;

    @Property(
        type = PropertyType.SWITCH, name = "Transparent Chat Input Field",
        description = "Remove the background from chat's input field.\n§eCan positively impact performance.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean transparentChatInputField;

    @Property(
        type = PropertyType.SWITCH, name = "Tab Height",
        description = "Move the tab overlay down n amount of pixels when there's an active bossbar.",
        category = "Screens", subcategory = "Tab"
    )
    public static boolean tabHeightAllow = true;

    @Property(
        type = PropertyType.SLIDER, name = "Set Tab Height",
        description = "Choose how many pixels down the tab will go when there's an active bossbar.",
        category = "Screens", subcategory = "Tab",
        max = 24
    )
    public static int tabHeight = 10;

    @Property(
        type = PropertyType.SWITCH, name = "Compact Chat",
        description = "Clean up chat by stacking duplicate messages.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean compactChat = true;

    @Property(
        type = PropertyType.SLIDER, name = "Super Compact Chat",
        description = "Clean up chat by stacking duplicate messages found within the select range.",
        category = "Screens", subcategory = "Chat",
        min = 1, max = 25
    )
    public static int superCompactChatAmount = 5;

    @Property(
        type = PropertyType.SWITCH, name = "Anti Clear Chat",
        description = "Remove blank messages from chat.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean antiClearChat = true;

    @Property(
        type = PropertyType.SWITCH, name = "Startup Notification",
        description = "Notify how long the game took to startup with a notification.",
        category = "Screens", subcategory = "General"
    )
    public static boolean startupNotification = true;

    @Property(
        type = PropertyType.SWITCH, name = "Damage Glance",
        description = "View the damage value of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean damageGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Item Count Glance",
        description = "View the amount of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean itemCountGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Enchantment Glance",
        description = "View the enchantments of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean enchantmentsGlance = true;

    @Property(
        type = PropertyType.SWITCH, name = "Protection Percentage",
        description = "View how much total armor protection you have inside of your inventory.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean protectionPercentage = true;

    @Property(
        type = PropertyType.SWITCH, name = "Projectile Protection Percentage",
        description = "View how much total projectile protection you have inside of your inventory.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean projectileProtectionPercentage = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Position",
        description = "Move the chat up 12 pixels to stop it from overlapping the health bar, as done in 1.12+.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean chatPosition = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Timestamps",
        description = "Add timestamps before a message.\nExample: §7[10:23 AM] Steve: Hey!",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean timestamps;

    @Property(
        type = PropertyType.SWITCH, name = "Cross Chat",
        description = "Stop clearing chat when switching servers.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean crossChat = true;

    @Property(
        type = PropertyType.SWITCH, name = "Clean Main Menu",
        description = "Remove the Realms button on the main menu as you need to be on the latest Minecraft version to use Realms.",
        category = "Screens", subcategory = "General"
    )
    public static boolean cleanMainMenu = true;

    @Property(
        type = PropertyType.SWITCH, name = "Chat Keeper",
        description = "Keep chat when toggling fullscreen.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean chatKeeper = true;

    @Property(
        type = PropertyType.SWITCH, name = "Skin Refresher",
        description = "Add a button to the escape menu to refresh your current skin without needing to leave the server.\n§eAlso accessible with the command \"/refreshskin\".",
        category = "Screens", subcategory = "General"
    )
    public static boolean skinRefresher = true;

    @Property(
        type = PropertyType.SWITCH, name = "Replace Open to Lan",
        description = "Remove the Open to Lan button when in a multiplayer server with a button to quickly open your server list.\nWill be reworked in the future to not kick you from the server.",
        category = "Screens", subcategory = "General"
    )
    public static boolean replaceOpenToLan;

    @Property(
        type = PropertyType.SWITCH, name = "Image Preview",
        description = "Preview image links when hovering over a supported URL.\nPress Shift to use fullscreen and Control to render in native image resolution.\n" +
            "Currently supported: Imgur, Discord, Badlion Screenshots.",
        category = "Screens", subcategory = "Image Preview"
    )
    public static boolean imagePreview = true;

    @Property(
        type = PropertyType.SLIDER, name = "Image Preview Width",
        description = "The % of screen width to be used for image preview.",
        category = "Screens", subcategory = "Image Preview",
        min = 10, max = 100
    )
    public static int imagePreviewWidth = 50;

    @Property(
        type = PropertyType.SWITCH, name = "Safe Chat Clicks",
        description = "Show the command or link that is ran/opened on click.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean safeChatClicks;

    @Property(
        type = PropertyType.SWITCH, name = "Replaced Mods Warning",
        description = "Display on startup what mods you may have that are replaced by Patcher.",
        category = "Screens", subcategory = "General"
    )
    public static boolean replacedModsWarning = true;

    @Property(
        type = PropertyType.SWITCH, name = "Screenshot Manager",
        description = "Change the way screenshotting works as a whole, creating a whole new process to screenshotting such as uploading to Imgur, copying to clipboard, etc.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean screenshotManager = true;

    @Property(
        type = PropertyType.SWITCH, name = "No Feedback",
        description = "Remove the messages from screenshots entirely.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean screenshotNoFeedback;

    @Property(
        type = PropertyType.SWITCH, name = "Screenshot Preview",
        description = "Preview the look of your screenshot when taken in the bottom right corner.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean screenshotPreview;

    @Property(
        type = PropertyType.SWITCH, name = "Compact Response",
        description = "Compact the message given when screenshotting.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean compactScreenshotResponse;

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
