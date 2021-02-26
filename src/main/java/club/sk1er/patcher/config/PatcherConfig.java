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

import club.sk1er.patcher.Patcher;
import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.modcore.api.utils.GuiUtil;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

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
        description = "Fix the camera being too far back, seemingly making your eyes be in the back of your head.\n" +
            "§cCurrently makes the F3 crosshair disappear.",
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
        type = PropertyType.SWITCH, name = "Player Void Rendering",
        description = "Remove the black box around the player while in the void.",
        category = "Bug Fixes", subcategory = "Rendering"
    )
    public static boolean playerVoidRendering = true;

    @Property(
        type = PropertyType.SWITCH, name = "Fluid Stitching",
        description = "Fix missing edges in fluids.",
        category = "Bug Fixes", subcategory = "Rendering", triggerActionOnInitialization = false
    )
    public static boolean fluidStitching = true;

    // MISCELLANEOUS

    @Property(
        type = PropertyType.SWITCH, name = "Fullbright",
        description = "Remove lighting updates, increasing visibility.\n§eCan positively impact performance.",
        category = "Miscellaneous", subcategory = "Rendering", triggerActionOnInitialization = false
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
    public static boolean nauseaEffect;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Achievements",
        description = "Remove achievement notifications.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean disableAchievements;

    @Property(
        type = PropertyType.DECIMAL_SLIDER, name = "Fire Overlay Height",
        description = "Change the height of the fire overlay.",
        category = "Miscellaneous", subcategory = "Overlays",
        minF = -0.5F, maxF = 1.5F
    )
    public static float fireOverlayHeight;

    @Property(
        type = PropertyType.SWITCH, name = "FOV Modifier",
        description = "Allow for modifying FOV change states.",
        category = "Miscellaneous", subcategory = "Field of View"
    )
    public static boolean allowFovModifying;

    @Property(
        type = PropertyType.DECIMAL_SLIDER, name = "Sprinting FOV",
        description = "Modify your FOV when sprinting.",
        category = "Miscellaneous", subcategory = "Field of View",
        minF = -5, maxF = 5
    )
    public static float sprintingFovModifierFloat = 1;

    @Property(
        type = PropertyType.DECIMAL_SLIDER, name = "Bow FOV",
        description = "Modify your FOV when pulling back a bow.",
        category = "Miscellaneous", subcategory = "Field of View",
        minF = -5, maxF = 5
    )
    public static float bowFovModifierFloat = 1;

    @Property(
        type = PropertyType.DECIMAL_SLIDER, name = "Speed FOV",
        description = "Modify your FOV when having the speed effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        minF = -5, maxF = 5
    )
    public static float speedFovModifierFloat = 1;

    @Property(
        type = PropertyType.DECIMAL_SLIDER, name = "Slowness FOV",
        description = "Modify your FOV when having the slowness effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        minF = -5, maxF = 5
    )
    public static float slownessFovModifierFloat = 1;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Water FOV",
        description = "Remove the change of FOV when underwater.",
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
        type = PropertyType.SWITCH, name = "Disable Hotbar Scrolling",
        description = "Remove the ability to scroll through your hotbar.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean disableHotbarScrolling;

    @Property(
        type = PropertyType.SWITCH, name = "Crosshair Perspective",
        description = "Remove the crosshair when in third person.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean crosshairPerspective;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Unfocused Sounds",
        description = "Change the volume of sounds when you're not tabbed into the window.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static float unfocusedSounds = 1.0F;

    @Property(
        type = PropertyType.SWITCH, name = "Unfocused FPS",
        description = "Toggle changing your FPS to whatever Unfocused FPS is set to when not tabbed into the window.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean unfocusedFPS;

    @Property(
        type = PropertyType.SLIDER, name = "Unfocused FPS Amount",
        description = "Change the maximum FPS when you're not tabbed into the window, saving resources.\n§eRequires Unfocused FPS.",
        category = "Miscellaneous", subcategory = "General",
        min = 1, max = 240
    )
    public static int unfocusedFPSAmount = 60;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Ground Foliage",
        description = "Stop plants/flowers from rendering.",
        category = "Miscellaneous", subcategory = "Blocks", triggerActionOnInitialization = false
    )
    public static boolean removeGroundFoliage;

    @Property(
        type = PropertyType.SWITCH, name = "Show Own Nametag",
        description = "See your own nametag in third person.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean showOwnNametag;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Ridden Horse Opacity",
        description = "Change the opacity of the horse you're currently riding for visibility.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static float riddenHorseOpacity = 1.0F;

    @Property(
        type = PropertyType.SWITCH, name = "Zoom Adjustment",
        description = "Scroll when using OptiFine's zoom to adjust the zoom level.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean scrollToZoom = true;

    @Property(
        type = PropertyType.SWITCH, name = "Zoom Smooth Camera",
        description = "Remove the smooth camera effect when using zoom.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean normalZoomSensitivity;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Zoom Sensitivity",
        description = "Use a custom mouse sensitivity when zoomed.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static float customZoomSensitivity = 1.0F;

    @Property(
        type = PropertyType.SWITCH, name = "Smooth Zoom Animation",
        description = "Add a smooth animation when you zoom in and out.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean smoothZoomAnimation;

    @Property(
        type = PropertyType.SWITCH, name = "Smooth Scroll-to-Zoom Animation",
        description = "Add a smooth animation when you scroll in and out while zoomed.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean smoothZoomAnimationWhenScrolling;

    @Property(
        type = PropertyType.SELECTOR, name = "Smooth Zoom Function",
        description = "Change the smoothing function used in the smooth zooming animation.",
        category = "Miscellaneous", subcategory = "OptiFine",
        options = {"In Out Quad", "In Out Circular", "Out Quint"}
    )
    public static int smoothZoomAlgorithm = 0;

    @Property(
        type = PropertyType.SWITCH, name = "Toggle to Zoom",
        description = "Make OptiFine's zoom key a toggle instead of requiring you to hold it.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean toggleToZoom;

    @Property(
        type = PropertyType.SWITCH, name = "Simplify FPS Counter",
        description = "Remove the additions OptiFine L5 and above makes to the debug screen FPS counter.",
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
        description = "Instant switching between full screen and non fullscreen modes.\n§eRequires Windowed Fullscreen.",
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
        description = "Replaces the selection box for crops with the 1.12 variant.\n§eOnly works on Hypixel & Singleplayer.",
        category = "Miscellaneous", subcategory = "Blocks"
    )
    public static boolean futureHitBoxes = true;

    @Property(
        type = PropertyType.SWITCH, name = "Alternate Text Shadow",
        description = "Change the text shadow to only move down rather than moving to the side.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean alternateTextShadow;

    @Property(
        type = PropertyType.SWITCH, name = "Add Text Shadow to Nametags",
        description = "Render nametags with shadowed text.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean shadowedNametagText;

    @Property(
        type = PropertyType.SWITCH, name = "Add Text Shadow to Actionbar",
        description = "Render actionbar messages with shadowed text.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean shadowedActionbarText;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Text Shadow",
        description = "Remove shadows from text.\n§eCan positively impact performance.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean disableShadowedText;

    @Property(
        type = PropertyType.SWITCH, name = "Better Camera",
        description = "Stop tall grass, plants, reeds, etc. from affecting your FOV as done in 1.14+.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean betterCamera = true;

    @Property(
        type = PropertyType.SWITCH, name = "Better F1",
        description = "Hide nametags when in F1 mode.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean betterHideGui;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Screen Bobbing",
        description = "While using View Bobbing, only remove the view aspect but have the hand still bounce around.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean removeViewBobbing;

    @Property(
        type = PropertyType.SWITCH, name = "Remove Map Bobbing",
        description = "While using View Bobbing, remove the hand bobbing when holding a map.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean mapBobbing;

    @Property(
        type = PropertyType.SWITCH, name = "Static Items",
        description = "Stop items from bobbing up and down when dropped on the ground.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean staticItems;

    @Property(
        type = PropertyType.BUTTON, name = "Modify Every Sound",
        description = "Open a separate GUI allowing you to mute or amplify individual sounds.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static void modifyEverySound() {
        GuiUtil.open(Objects.requireNonNull(Patcher.instance.getPatcherSoundConfig().gui()));
    }

    // PERFORMANCE

    @Property(
        type = PropertyType.SWITCH, name = "Item Searching",
        description = "Stop items from searching for extra items to combine with when the stack is already full.",
        category = "Performance", subcategory = "Items"
    )
    public static boolean itemSearching = true;

    @Property(
        type = PropertyType.SWITCH, name = "Instant World Swapping",
        description = "Remove the dirt screen and waiting time when switching a world.",
        category = "Performance", subcategory = "World"
    )
    public static boolean instantWorldSwapping = true;

    @Property(
        type = PropertyType.SWITCH, name = "Limit Chunk Updates",
        description = "Limit the amount of chunk updates that happen a second.",
        category = "Performance", subcategory = "World"
    )
    public static boolean limitChunks;

    @Property(
        type = PropertyType.SLIDER, name = "Chunk Update Limit",
        description = "Specify the amount of updates that can happen a second.",
        category = "Performance", subcategory = "World",
        min = 5, max = 250
    )
    public static int chunkUpdateLimit = 50;

    @Property(
        type = PropertyType.SWITCH, name = "Downscale Pack Images",
        description = "Change all pack icons to 64x64 to reduce memory usage.",
        category = "Performance", subcategory = "Resources"
    )
    public static boolean downscalePackImages = true;

    @Property(
        type = PropertyType.SWITCH, name = "Static Fog Color",
        description = "Reduce the amount of instructions to produce the fog color to improve performance.",
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
        description = "Render models in a single draw call.",
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
        description = "Stop armorstands from rendering.\nArmorstands are commonly used for NPC nametags. Enabling this will stop those from rendering as well.",
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
        type = PropertyType.SWITCH, name = "Disable Mapped Item Frames",
        description = "Stop item frames only with maps as their item from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableMappedItemFrames;

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
        type = PropertyType.SWITCH, name = "Disable Nametag Boxes",
        description = "Remove the transparent box around the nametag.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableNametagBoxes;

    /*@Property(
        type = PropertyType.SWITCH, name = "HUD Caching",
        description = "Limit the FPS of your HUD to 20, giving the game more time to render other things.\n" +
            "§c§lVery experimental, will not work out for everyone.",
        category = "Performance", subcategory = "Experimental"
    )
    public static boolean hudCaching;

    @Property(
        type = PropertyType.SWITCH, name = "HUD Caching - Compatibility Mode",
        description = "If issues with mods and their HUD items are found, try enabling this.",
        category = "Performance", subcategory = "Experimental"
    )
    public static boolean hudCachingCompatibilityMode;

    @Property(
        type = PropertyType.SLIDER, name = "HUD Cache Timer",
        description = "Change the amount of time between HUD updates.\n" +
            "Measured in ms, higher value will give better performance but is not recommended.\n" +
            "§c§lVery experimental, will not work out for everyone.",
        category = "Performance", subcategory = "Experimental",
        min = 100, max = 2500
    )
    public static int hudCacheTimer = 1000;*/

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
        description = "Stop the entity culling effect when using OptiFine shaders.\n§cDue to the way OptiFine shaders work, we are unable to make Entity Culling compatible.",
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
        type = PropertyType.SWITCH, name = "Don't Cull Armorstand Nametags",
        description = "Render nametags even when the armorstand is occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullArmorStandNametags;

    @Property(
        type = PropertyType.SWITCH, name = "Check Armorstand Rules",
        description = "Don't cull armorstands that have a marker set in their entity rules." +
            "\nThis will result in a lot of unculled armorstands in places like Hypixel Skyblock, " +
            "but will provide better entity visibility, while losing out on some performance improvements.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean checkArmorstandRules;

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
        type = PropertyType.SWITCH, name = "Entity Back-face Culling",
        description = "Stop rendering sides of entities that you cannot see.\nBeing inside an entity will cause that body part to be invisible.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean entityBackFaceCulling;

    @Property(
        type = PropertyType.SWITCH, name = "Player Back-face Culling",
        description = "Stop rendering sides of players that you cannot see.\nBeing inside a player will cause that body part to be invisible.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean playerBackFaceCulling;

    @Property(
        type = PropertyType.SWITCH, name = "Disable Mob Spawning",
        description = "Reduce memory usage by disabling the check for mob spawning despite the set game rule.\n§eThis will disable mob spawning in a singleplayer world.",
        category = "Performance", subcategory = "World"
    )
    public static boolean mobSpawningOptimization;

    @Property(
        type = PropertyType.SWITCH, name = "Entity Render Distance Toggle",
        description = "Toggle allowing a custom entity render distance.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean entityRenderDistanceToggle;

    @Property(
        type = PropertyType.SLIDER, name = "Global Entity Render Distance",
        description = "Stop rendering all entities outside of the specified radius.\n" +
            "This will ignore the distance of other entity render distances if smaller.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int entityRenderDistance = 64;

    @Property(
        type = PropertyType.SLIDER, name = "Player Render Distance",
        description = "Stop rendering players outside of the specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int playerRenderDistance = 64;

    @Property(
        type = PropertyType.SLIDER, name = "Passive Entity Render Distance",
        description = "Stop rendering passive entities outside of the specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int passiveEntityRenderDistance = 64;

    @Property(
        type = PropertyType.SLIDER, name = "Hostile Entity Render Distance",
        description = "Stop rendering hostile entities outside of the specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int hostileEntityRenderDistance = 64;

    @Property(
        type = PropertyType.SWITCH, name = "Optimized Resource Pack Discovery",
        description = "Optimize the time it takes to open the resource packs GUI.\n§cDoes not work with Labymod's RP24 addon.",
        category = "Performance", subcategory = "General"
    )
    public static boolean optimizedResourcePackDiscovery = true;

    // SCREENS

    @Property(
        type = PropertyType.SELECTOR, name = "Name History Style",
        description = "Change how the Name History will appear.",
        category = "Screens", subcategory = "General",
        options = {"Open in a GUI", "Send in chat", "Popup in the top-right"}
    )
    public static int nameHistoryStyle = 2;

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
        type = PropertyType.SWITCH, name = "Custom Tab Opacity",
        description = "Allow for customizing tab opacity.",
        category = "Screens", subcategory = "Tab"
    )
    public static boolean customTabOpacity = false;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Tab Opacity",
        description = "Change the tab list opacity.",
        category = "Screens", subcategory = "Tab"
    )
    public static float tabOpacity = 1.0F;

    @Property(
        type = PropertyType.SWITCH, name = "GUI Crosshair",
        description = "Stop rendering the crosshair when in a GUI.",
        category = "Screens", subcategory = "General"
    )
    public static boolean guiCrosshair;

    @Property(
        type = PropertyType.SWITCH, name = "1.11 Chat Length",
        description = "Extend the amount of characters you can type from 100 to 256 on supported servers." +
            "\n§eSupported servers are servers that support 1.11 or above." +
            "\n§cSome servers may kick you for this despite supporting 1.11 or above.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean extendedChatLength = true;

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
        description = "Clean up chat by stacking duplicate messages.\n§cDoes not work with Labymod.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean compactChat = true;

    @Property(
        type = PropertyType.SWITCH, name = "Consecutive Compact Chat",
        description = "Only compact messages if they're consecutive.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean consecutiveCompactChat;

    @Property(
        type = PropertyType.SLIDER, name = "Compact Chat Time",
        description = "Change how long before old messages are no longer compacted.\n§eMeasured in seconds.",
        category = "Screens", subcategory = "Chat",
        min = 1, max = 120
    )
    public static int compactChatTime = 60;

    @Property(
        type = PropertyType.SWITCH, name = "Anti Clear Chat",
        description = "Remove blank messages from chat.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean antiClearChat;

    @Property(
        type = PropertyType.SWITCH, name = "Shift Chat",
        description = "Holding shift while pressing enter will keep chat open.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean shiftChat;

    @Property(
        type = PropertyType.SLIDER, name = "Chat Delay",
        description = "Delay chat messages if they're sent within the selected timeframe after the previous message.\n§eMeasured in seconds.",
        category = "Screens", subcategory = "Chat",
        max = 6
    )
    public static int chatDelay = 0;

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
        description = "Add timestamps before a message.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean timestamps;

    @Property(
        type = PropertyType.SELECTOR, name = "Chat Timestamps Format",
        description = "Change the time format of Chat Timestamps.",
        category = "Screens", subcategory = "Chat",
        options = {"12 Hour", "24 Hour"}
    )
    public static int timestampsFormat = 0;

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
        description = "Keep your currently-typed message in chat when toggling fullscreen.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean chatKeeper = true;

    @Property(
        type = PropertyType.SWITCH, name = "Skin Refresher",
        description = "Add a button to the escape menu to refresh your current skin without needing to leave the server.\n§eAlso accessible with the command \"/patcher refresh\".",
        category = "Screens", subcategory = "General"
    )
    public static boolean skinRefresher;

    @Property(
        type = PropertyType.SWITCH, name = "Replace Open to Lan",
        description = "Remove the Open to Lan button when in a multiplayer server with a button to quickly open your server list.\n" +
            "Opening Direct Connect will make you leave the server.",
        category = "Screens", subcategory = "General"
    )
    public static boolean replaceOpenToLan;

    @Property(
        type = PropertyType.SWITCH, name = "Image Preview",
        description = "Preview image links when hovering over a supported URL.\nPress Shift to use fullscreen and Control to render in native image resolution.\n" +
            "§eCurrently supported: Imgur, Discord, Badlion Screenshots.",
        category = "Screens", subcategory = "Image Preview"
    )
    public static boolean imagePreview = true;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Image Preview Width",
        description = "The % of screen width to be used for image preview.",
        category = "Screens", subcategory = "Image Preview"
    )
    public static float imagePreviewWidth = 0.50F;

    @Property(
        type = PropertyType.SWITCH, name = "Safe Chat Clicks",
        description = "Show the command/link that runs/opens on click.",
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
        type = PropertyType.SWITCH, name = "Smart Disconnect",
        description = "Choose between disconnecting or relogging when clicking the disconnect button.\n§eOnly works on Multiplayer servers.",
        category = "Screens", subcategory = "General"
    )
    public static boolean smartDisconnect;

    // SCREENSHOTS

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
        type = PropertyType.SWITCH, name = "Auto Copy Screenshot",
        description = "Automatically copy screenshots to the clipboard when taken.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean autoCopyScreenshot;

    @Property(
        type = PropertyType.SWITCH, name = "Screenshot Preview",
        description = "Preview the look of your screenshot when taken in the bottom right corner.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean screenshotPreview;

    @Property(
        type = PropertyType.SLIDER, name = "Preview Time",
        description = "Adjust how long the preview should stay on screen before sliding out.\nTime is measured in seconds.",
        category = "Screenshots", subcategory = "General",
        min = 1, max = 5
    )
    public static int previewTime = 3;

    @Property(
        type = PropertyType.SELECTOR, name = "Preview Animation",
        description = "Select an animation style for the screenshot preview.",
        category = "Screenshots", subcategory = "General",
        options = {"iOS Style", "Slide Out", "None"}
    )
    public static int previewAnimationStyle = 0;

    @Property(
        type = PropertyType.PERCENT_SLIDER, name = "Preview Scale",
        description = "Change the scale of the preview.",
        category = "Screenshots", subcategory = "General"
    )
    public static float previewScale = 1.0F;

    @Property(
        type = PropertyType.SWITCH, name = "Compact Response",
        description = "Compact the message given when screenshotting.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean compactScreenshotResponse;

    // HIDDEN

    @Property(
        type = PropertyType.SLIDER, name = "Desired Scale Override",
        category = "hidden", hidden = true)
    public static int desiredScaleOverride = -1;

    public PatcherConfig() {
        super(new File("./config/patcher.toml"));
        initialize();

        final Consumer<Object> reloadWorld = renderer -> Minecraft.getMinecraft().renderGlobal.loadRenderers();
        registerListener("fullbright", reloadWorld);
        registerListener("fluidStitching", reloadWorld);
        registerListener("removeGroundFoliage", reloadWorld);
    }
}
