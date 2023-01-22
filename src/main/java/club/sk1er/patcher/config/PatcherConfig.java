package club.sk1er.patcher.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator;
import cc.polyfrost.oneconfig.config.migration.VigilanceName;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.tweaker.ClassTransformer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeVersion;
import org.apache.commons.lang3.SystemUtils;

import java.util.Arrays;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class PatcherConfig extends Config {

    // BUG FIXES

    @Switch(
        name = "Keep Shaders on Perspective Change",
        description = "Resolve Vanilla shaders being cleared when changing perspective.",
        category = "Bug Fixes", subcategory = "General",
        size = 2
    )
    public static boolean keepShadersOnPerspectiveChange = true;

    @Info(
        text = "Parallax Fix currently makes the F3 crosshair disappear.",
        category = "Bug Fixes", subcategory = "General",
        type = InfoType.ERROR
    )
    private static String parallaxFixInfo = "";

    @Switch(
        name = "Parallax Fix",
        description = "Resolve the camera being too far back, seemingly making your eyes be in the back of your head.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean parallaxFix;

    @Info(
        text = "Culling Fix can negatively impact performance.",
        category = "Bug Fixes", subcategory = "General",
        type = InfoType.ERROR
    )
    private static String cullingFixInfo = "";

    @Switch(
        name = "Culling Fix",
        description = "Resolve false negatives in frustum culling, creating invisible chunks in some cases.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean cullingFix;

    @Switch(
        name = "Resource Exploit Fix",
        description = "Resolve an exploit in 1.8 allowing servers to look through directories.",
        category = "Bug Fixes", subcategory = "Security"
    )
    public static boolean resourceExploitFix = true;

    @Switch(
        name = "Layers In Tab",
        description = "Resolve players sometimes not having a hat layer in Tab.",
        category = "Bug Fixes", subcategory = "General"
    )
    public static boolean layersInTab = true;

    @Switch(
        name = "Player Void Rendering",
        description = "Resolve the black box around the player while in the void.",
        category = "Bug Fixes", subcategory = "Rendering",
        size = 2
    )
    public static boolean playerVoidRendering = true;

    @Info(
        text = "Alex Arm Position requires a restart once toggled.",
        category = "Bug Fixes", subcategory = "Rendering",
        type = InfoType.WARNING
    )
    private static String fixedAlexArmsInfo = "";

    @Switch(
        name = "Alex Arm Position",
        description = "Resolve Alex-model arms being shifted down further than Steve-model arms.",
        category = "Bug Fixes", subcategory = "Rendering"
    )
    public static boolean fixedAlexArms = true;

    @Switch(
        name = "Fix Actionbar Overlap",
        description = "Prevents the actionbar text from rendering above the armor/health bar.",
        category = "Bug Fixes", subcategory = "Rendering"
    )
    public static boolean fixActionbarOverlap;

    @Dropdown(
        name = "Keyboard Layout",
        description = "The layout of your keyboard, used to fix input bugs accordingly.",
        category = "Bug Fixes", subcategory = "Linux",
        options = {"QWERTY", "BE AZERTY", "FR AZERTY"}
    )
    public static int keyboardLayout = 0;

    @Switch(
        name = "Vanilla Glass Panes",
        description = "Reverts a Forge change causing Glass Panes and Iron Bars to connect where they shouldn't.",
        category = "Bug Fixes", subcategory = "Forge"
    )
    public static boolean vanillaGlassPanes;

    // MISCELLANEOUS

    @Info(
        text = "Better Keybind Handling does not work on macOS due to LWJGL issues.",
        category = "Miscellaneous", subcategory = "General",
        type = InfoType.ERROR,
        size = 2
    )
    private static String betterKeybindHandlingInfo = "";

    @Switch(
        name = "Better Keybind Handling",
        description = "Make keys re-register when closing a GUI, like in 1.12+.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean newKeybindHandling = true;

    @Switch(
        name = "Separate Sound & Texture Reloading",
        description = "Separate reloading resources into reloading sounds (F3+S) and reloading textures (F3+T).",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean separateResourceLoading;

    @Info(
        text = "Fullbright can positively impact performance. It may conflict with minimaps mods.",
        category = "Miscellaneous", subcategory = "Rendering",
        type = InfoType.WARNING,
        size = 2
    )
    private static String fullbrightInfo = "";

    @Switch(
        name = "Fullbright",
        description = "Remove lighting updates, increasing visibility.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean fullbright = true;

    @Switch(
        name = "Smart Fullbright",
        description = "Automatically disable the Fullbright effect when using OptiFine shaders.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean smartFullbright = true;

    @Switch(
        name = "Nausea Effect",
        description = "Remove the nether portal effect appearing when clearing nausea.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean nauseaEffect;

    @Switch(
        name = "Disable Achievements",
        description = "Remove achievement notifications.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean disableAchievements;

    @Slider(
        name = "Fire Overlay Height",
        description = "Change the height of the fire overlay.",
        category = "Miscellaneous", subcategory = "Overlays",
        min = -0.5F, max = 1.5F
    )
    public static float fireOverlayHeight;

    @Slider( // todo add percentage
        name = "Fire Overlay Opacity",
        description = "Change the opacity of the fire overlay.",
        category = "Miscellaneous", subcategory = "Overlays",
        min = 0F, max = 1.0F
    )
    public static float fireOverlayOpacity = 1.0F;

    @Switch(
        name = "Hide Fire Overlay with Fire Resistance",
        description = "Hide the fire overlay when you have fire resistance active.\n" +
            "The overlay will blink 5 seconds before your fire resistance is about to run out.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean hideFireOverlayWithFireResistance;

    @Switch(
        name = "Disable Titles",
        description = "Stop titles from appearing.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    @VigilanceName(name = "Disable Titles", category = "Miscellaneous", subcategory = "Titles")
    public static boolean disableTitles;

    @Slider(
        name = "Title Scale",
        description = "Set the scale for titles.",
        category = "Miscellaneous", subcategory = "Titles",
        min = 0.0F, max = 1.0F
    )
    public static float titleScale = 1.0F;

    @Switch(
        name = "Automatically Scale Title",
        description = "Automatically scale titles if the title goes over the screen.",
        category = "Miscellaneous", subcategory = "Titles"
    )
    public static boolean autoTitleScale;

    @Slider(
        name = "Title Opacity",
        description = "Change the opacity of titles.",
        category = "Miscellaneous", subcategory = "Titles",
        min = 0F, max = 1.0F
    )
    public static float titleOpacity = 1.0F;

    @Switch(
        name = "FOV Modifier",
        description = "Allow for modifying FOV change states.",
        category = "Miscellaneous", subcategory = "Field of View"
    )
    public static boolean allowFovModifying;

    @Slider(
        name = "Sprinting FOV",
        description = "Modify your FOV when sprinting.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5F, max = 5F
    )
    public static float sprintingFovModifierFloat = 1;

    @Slider(
        name = "Bow FOV",
        description = "Modify your FOV when pulling back a bow.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static float bowFovModifierFloat = 1;

    @Slider(
        name = "Speed FOV",
        description = "Modify your FOV when having the speed effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static float speedFovModifierFloat = 1;

    @Slider(
        name = "Slowness FOV",
        description = "Modify your FOV when having the slowness effect.",
        category = "Miscellaneous", subcategory = "Field of View",
        min = -5, max = 5
    )
    public static float slownessFovModifierFloat = 1;

    @Switch(
        name = "Toggle Tab",
        description = "Hold tab open without needing to hold down the tab key.",
        category = "Miscellaneous", subcategory = "Tab"
    )
    public static boolean toggleTab;

    @Switch(
        name = "Disable Hotbar Scrolling",
        description = "Remove the ability to scroll through your hotbar.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean disableHotbarScrolling;

    @Switch(
        name = "Crosshair Perspective",
        description = "Remove the crosshair when in third person.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean crosshairPerspective;

    @Slider(
        name = "Unfocused Sounds",
        description = "Change the volume of sounds when you're not tabbed into the window.",
        category = "Miscellaneous", subcategory = "General",
        min = 0F, max = 1.0F
    )
    public static float unfocusedSounds = 1.0F;

    @Switch(
        name = "Unfocused FPS",
        description = "Toggle changing your FPS to whatever Unfocused FPS is set to when not tabbed into the window.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean unfocusedFPS;

    @Slider(
        name = "Unfocused FPS Amount",
        description = "Change the maximum FPS when you're not tabbed into the window, saving resources.",
        category = "Miscellaneous", subcategory = "General",
        min = 15, max = 240
    )
    public static int unfocusedFPSAmount = 60;

    @Number(
        name = "Custom FPS Limit",
        description = "Change the maximum FPS to a value that Minecraft doesn't normally allow for. Setting this to 0 will go back to the value set in Minecraft.",
        category = "Miscellaneous", subcategory = "General",
        min = 0,
        max = Integer.MAX_VALUE
    )
    public static int customFpsLimit = 0;

    @Switch(
        name = "Remove Ground Foliage",
        description = "Stop plants/flowers from rendering.",
        category = "Miscellaneous", subcategory = "Blocks"
    )
    public static boolean removeGroundFoliage;

    @Switch(
        name = "Show Own Nametag",
        description = "See your nametag in third person.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean showOwnNametag;

    @Switch(
        name = "Clean Projectiles",
        description = "Show projectiles 2 ticks after they're shot to stop them from obstructing your view.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean cleanProjectiles;

    @Slider(
        name = "Ridden Horse Opacity",
        description = "Change the opacity of the horse you're currently riding for visibility.",
        category = "Miscellaneous", subcategory = "Rendering",
        min = 0F, max = 1.0F
    )
    public static float riddenHorseOpacity = 1.0F;

    @Switch(
        name = "Hide Aura on Invisible Withers",
        description = "Don't render the aura around a wither when it is invisible.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean hideAuraOnInvisibleWither;

    @Switch(
        name = "Zoom Adjustment",
        description = "Scroll when using OptiFine's zoom to adjust the zoom level.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean scrollToZoom = true;

    @Switch(
        name = "Remove Smooth Camera While Zoomed",
        description = "Remove the smooth camera effect when using zoom.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean normalZoomSensitivity;

    @Switch(
        name = "Render Hand While Zoomed",
        description = "Keep your hand on screen when you zoom in.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean renderHandWhenZoomed;

    @Slider(
        name = "Zoom Sensitivity",
        description = "Use a custom mouse sensitivity value when zoomed in.",
        category = "Miscellaneous", subcategory = "OptiFine",
        min = 0F, max = 1F
    )
    public static float customZoomSensitivity = 1.0F;

    @Switch(
        name = "Dynamic Zoom Sensitivity",
        description = "Reduce your mouse sensitivity the more you zoom in.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean dynamicZoomSensitivity;

    @Switch(
        name = "Smooth Zoom Animation",
        description = "Add a smooth animation when you zoom in and out.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean smoothZoomAnimation;

    @Switch(
        name = "Smooth Scroll-to-Zoom Animation",
        description = "Add a smooth animation when you scroll in and out while zoomed.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean smoothZoomAnimationWhenScrolling;

    @Dropdown(
        name = "Smooth Zoom Function",
        description = "Change the smoothing function used in the smooth zooming animation.",
        category = "Miscellaneous", subcategory = "OptiFine",
        options = {"In Out Quad", "In Out Circular", "Out Quint"}
    )
    public static int smoothZoomAlgorithm = 0;

    @Switch(
        name = "Toggle to Zoom",
        description = "Make OptiFine's zoom key a toggle instead of requiring you to hold it.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean toggleToZoom;

    @Switch(
        name = "Simplify FPS Counter",
        description = "Remove the extra FPS counter added by OptiFine.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean normalFpsCounter = true;

    @Switch(
        name = "Use Vanilla Metrics Renderer",
        description = "Replace OptiFine's ALT+F3 metrics renderer with the Vanilla renderer.",
        category = "Miscellaneous", subcategory = "OptiFine"
    )
    public static boolean useVanillaMetricsRenderer = true;

    @Switch(
        name = "Number Ping",
        description = "Show a readable ping number in tab instead of bars.",
        category = "Miscellaneous", subcategory = "Tab"
    )
    public static boolean numberPing = true;

    @Switch(
        name = "Numerical Enchantments",
        description = "Use readable numbers instead of Roman numerals on enchants.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean numericalEnchants;

    @Switch(
        name = "Translate Unknown Roman Numerals",
        description = "Generate Roman numeral from enchantment and potion levels instead of using language file.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean betterRomanNumerals = true;

    @Switch(
        name = "Clean View",
        description = "Stop rendering your potion effect particles.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean cleanView;

    @Switch(
        name = "Windowed Fullscreen",
        description = "Implement Windowed Fullscreen in Minecraft, allowing you to drag your mouse outside the window.",
        category = "Miscellaneous", subcategory = "Window"
    )
    public static boolean windowedFullscreen;

    @Switch(
        name = "Instant Fullscreen",
        description = "Instant switching between fullscreen and non-fullscreen modes.",
        category = "Miscellaneous", subcategory = "Window"
    )
    public static boolean instantFullscreen;

    @Switch(
        name = "Remove Water Overlay",
        description = "Remove the water texture overlay when underwater.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean removeWaterOverlay;

    @Switch(
        name = "Disable Breaking Particles",
        description = "Remove block-breaking particles for visibility.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean disableBlockBreakParticles;

    @Switch(
        name = "Disable Lightning Bolts",
        description = "Stop lightning bolts from appearing.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean disableLightningBolts;

    @Info(
        text = "Files deleted by Log Optimizer are not recoverable.",
        category = "Miscellaneous", subcategory = "General",
        type = InfoType.WARNING
    )
    private static boolean logOptimizerInfo = true;

    @Switch(
        name = "Log Optimizer",
        description = "Delete all files in the logs folder, as these can usually take up a lot of space.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean logOptimizer;

    @Slider(
        name = "Log Optimizer Amount",
        description = "Choose how many days old a file must be before being deleted.",
        category = "Miscellaneous", subcategory = "General",
        min = 1, max = 90
    )
    public static int logOptimizerLength = 30;

    @Switch(
        name = "Remove Inverted Colors from Crosshair",
        description = "Remove the inverted color effect on the crosshair.",
        category = "Miscellaneous", subcategory = "Overlays"
    )
    public static boolean removeInvertFromCrosshair;

    @Info(
        text = "1.12 Farm Selection Boxes only works on Hypixel & Singleplayer.",
        category = "Miscellaneous", subcategory = "Blocks",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean farmSelectionBoxesInfo = true;

    @Switch(
        name = "1.12 Farm Selection Boxes",
        description = "Replaces the selection box for crops with the 1.12 variant.",
        category = "Miscellaneous", subcategory = "Blocks"
    )
    public static boolean futureHitBoxes = true;

    @Switch(
        name = "Alternate Text Shadow",
        description = "Change the text-shadow to only move down rather than move to the side.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean alternateTextShadow;

    @Switch(
        name = "Add Text Shadow to Nametags",
        description = "Render nametags with shadowed text.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean shadowedNametagText;

    @Switch(
        name = "Add Text Shadow to Actionbar",
        description = "Render actionbar messages with shadowed text.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean shadowedActionbarText;

    @Switch(
        name = "Add Background to Actionbar",
        description = "Render a background behind the actionbar.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean actionbarBackground;

    @Info(
        text = "Disable Text Shadow can positively impact performance.",
        category = "Miscellaneous", subcategory = "Rendering",
        type = InfoType.INFO
    )
    private static boolean disableTextShadowInfo = true;

    @Switch(
        name = "Disable Text Shadow",
        description = "Remove shadows from text.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean disableShadowedText;

    @Switch(
        name = "Left Hand in First Person",
        description = "Render the first-person hand on the left of the screen.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean leftHandInFirstPerson;

    @Switch(
        name = "Better Camera",
        description = "Stop tall grass, plants, reeds, etc. from affecting your FOV as done in 1.14+.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean betterCamera = true;

    @Switch(
        name = "Better F1",
        description = "Hide nametags when in F1 mode.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean betterHideGui;

    @Switch(
        name = "Remove Screen Bobbing",
        description = "While using View Bobbing, only remove the view aspect but have the hand still bounce around.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean removeViewBobbing;

    @Switch(
        name = "Remove Map Bobbing",
        description = "While using View Bobbing, remove the hand bobbing when holding a map.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean mapBobbing;

    @Switch(
        name = "Static Items",
        description = "Stop items from bobbing up and down when dropped on the ground.",
        category = "Miscellaneous", subcategory = "General"
    )
    public static boolean staticItems;

    @Button(
        name = "Modify Every Sound",
        text = "Modify",
        description = "Open a separate GUI allowing you to mute or amplify individual sounds.",
        category = "Miscellaneous", subcategory = "General"
    )
    private void modifyEverySound() {
        Patcher.instance.getPatcherSoundConfig().openGui();
    }

    @Switch(
        name = "Natural Capes",
        description = "Changes some physics in capes to fix rotation bugs and look more natural.",
        category = "Miscellaneous", subcategory = "Rendering"
    )
    public static boolean naturalCapes;

    // PERFORMANCE

    @Switch(
        name = "Optimized World Swapping",
        description = "Remove unnecessary garbage collection & screen displaying to make world swapping feel nearly instant.",
        category = "Performance", subcategory = "World"
    )
    public static boolean optimizedWorldSwapping = true;

    @Switch(
        name = "Limit Chunk Updates",
        description = "Limit the number of chunk updates that happen a second.",
        category = "Performance", subcategory = "World"
    )
    public static boolean limitChunks;

    @Slider(
        name = "Chunk Update Limit",
        description = "Specify the number of updates that can happen a second.",
        category = "Performance", subcategory = "World",
        min = 5, max = 250
    )
    public static int chunkUpdateLimit = 50;

    @Switch(
        name = "Downscale Pack Images",
        description = "Change all pack icons to 64x64 to reduce memory usage.",
        category = "Performance", subcategory = "Resources"
    )
    public static boolean downscalePackImages = true;

    @Switch(
        name = "Low Animation Tick",
        description = "Lowers the number of animations that happen a second from 1000 to 500.",
        category = "Performance", subcategory = "World"
    )
    public static boolean lowAnimationTick = true;

    @Switch(
        name = "Batch Model Rendering",
        description = "Render models in a single draw call.",
        category = "Performance", subcategory = "World"
    )
    public static boolean batchModelRendering = true;

    @Switch(
        name = "Static Particle Color",
        description = "Disable particle lighting checks each frame.",
        category = "Performance", subcategory = "Particles"
    )
    public static boolean staticParticleColor = true;

    @Slider(
        name = "Max Particle Limit",
        description = "Stop additional particles from appearing when there are too many at once.",
        category = "Performance", subcategory = "Particles",
        min = 1, max = 10000
    )
    public static int maxParticleLimit = 4000;

    @Switch(
        name = "Optimized Font Renderer",
        description = "Use modern rendering techniques to improve the font renderer performance.",
        category = "Performance", subcategory = "Text Rendering"
    )
    public static boolean optimizedFontRenderer = true;

    @Switch(
        name = "Cache Font Data",
        description = "Cache font data, allowing for it to be reused multiple times before needing recalculation.",
        category = "Performance", subcategory = "Text Rendering"
    )
    public static boolean cacheFontData = true;

    @Info(
        text = "Armor stands are commonly used for NPC nametags. Enabling Disable Armorstands will stop those from rendering as well.",
        category = "Performance", subcategory = "Entity Rendering",
        type = InfoType.ERROR,
        size = 2
    )
    private static boolean armorStandInfo = true;

    @Switch(
        name = "Disable Armorstands",
        description = "Stop armor stands from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableArmorstands;

    @Switch(
        name = "Disable Semitransparent Players",
        description = "Stop semitransparent players from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableSemitransparentEntities;

    @Switch(
        name = "Disable Enchantment Books",
        description = "Stop enchantment table books from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableEnchantmentBooks;

    @Switch(
        name = "Disable Item Frames",
        description = "Stop item frames from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableItemFrames;

    @Switch(
        name = "Disable Mapped Item Frames",
        description = "Stop item frames only with maps as their item from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableMappedItemFrames;

    @Switch(
        name = "Disable Grounded Arrows",
        description = "Stop arrows that are in the ground from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableGroundedArrows;

    @Switch(
        name = "Disable Attached Arrows",
        description = "Stop arrows that are attached to a player from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableAttachedArrows;

    @Switch(
        name = "Disable Moving Arrows",
        description = "Stop arrows that are airborne from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableMovingArrows;

    @Switch(
        name = "Disable Skulls",
        description = "Stop skulls from rendering.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableSkulls;

    @Switch(
        name = "Disable End Portals",
        description = "Stop end portals from rendering.",
        category = "Performance", subcategory = "General"
    )
    public static boolean disableEndPortals;

    @Switch(
        name = "Disable Nametag Boxes",
        description = "Remove the transparent box around the nametag.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean disableNametagBoxes;

    @Switch(
        name = "Unstacked Items",
        description = "Render stacks of items on the ground as just one instead of having up to 5 copies in one stack.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean unstackedItems;

    @Info(
        text = "Due to the way OptiFine shaders work, we are unable to make Entity Culling compatible.",
        category = "Performance", subcategory = "Culling",
        type = InfoType.ERROR,
        size = 2
    )
    private static boolean entityCullingInfo = true;

    @Switch(
        name = "Entity Culling",
        description = "Check to see if an entity is visible to the player before attempting to render them.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean entityCulling = true;

    @Dropdown(
        name = "Entity Culling Interval",
        description = "The amount of time in ms between performing visibility checks for entities.\nShorter periods are more costly toward performance but provide the most accurate information.\nLower values are recommended in competitive environments.",
        category = "Performance", subcategory = "Culling",
        options = {"50", "25", "10"}
    )
    public static int cullingInterval = 0;

    @Switch(
        name = "Smart Entity Culling",
        description = "Disable Entity Culling effect when using OptiFine shaders.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean smartEntityCulling = true;

    @Switch(
        name = "Don't Cull Player Nametags",
        description = "Continue to render Player Nametags when the entity is being occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullNametags;

    @Switch(
        name = "Don't Cull Entity Nametags",
        description = "Continue to render Entity Nametags when the entity is being occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullEntityNametags;

    @Switch(
        name = "Don't Cull Armorstand Nametags",
        description = "Continue to render Armorstand Nametags when the entity is being occluded.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean dontCullArmorStandNametags;

    @Switch(
        name = "Check Armorstand Rules",
        description = "Don't cull armor stands that have a specific rule assigned to them." +
            "\nThis will result in a lot of non-occluded armor stands in places like Hypixel Skyblock, " +
            "but will resolve special entities being occluded when they typically shouldn't be.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean checkArmorstandRules;

    @Switch(
        name = "Disable Enchantment Glint",
        description = "Disable the enchantment glint.",
        category = "Performance", subcategory = "General"
    )
    public static boolean disableEnchantmentGlint;

    @Info(
        text = "When back-face culling is enabled, being inside an entity will cause that body part to be invisible.",
        category = "Performance", subcategory = "Culling",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean backFaceCullingInfo = true;

    @Info(
        text = "Some models may have a transparent face and will cause the back face to not show, such as Wither Skeletons.",
        category = "Performance", subcategory = "Culling",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean backFaceCullingInfo2 = true;

    @Switch(
        name = "Entity Back-face Culling",
        description = "Stop rendering sides of entities that you cannot see.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean entityBackFaceCulling;

    @Switch(
        name = "Player Back-face Culling",
        description = "Stop rendering sides of players that you cannot see.\n" +
            "Being inside a player will cause that body part to be invisible.",
        category = "Performance", subcategory = "Culling"
    )
    public static boolean playerBackFaceCulling;

    @Switch(
        name = "Entity Render Distance Toggle",
        description = "Toggle allowing a custom entity render distance.",
        category = "Performance", subcategory = "Entity Rendering"
    )
    public static boolean entityRenderDistanceToggle;

    @Slider(
        name = "Global Entity Render Distance",
        description = "Stop rendering all entities outside of a specified radius.\n" +
            "This will ignore the distance of other entity render distances if smaller.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int entityRenderDistance = 64;

    @Slider(
        name = "Player Render Distance",
        description = "Stop rendering players outside of a specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int playerRenderDistance = 64;

    @Slider(
        name = "Tile Entity Render Distance",
        description = "Stop rendering tile entities outside of a specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int tileEntityRenderDistance = 64;

    @Slider(
        name = "Passive Entity Render Distance",
        description = "Stop rendering passive entities outside of a specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int passiveEntityRenderDistance = 64;

    @Slider(
        name = "Hostile Entity Render Distance",
        description = "Stop rendering hostile entities outside of a specified radius.",
        category = "Performance", subcategory = "Entity Rendering",
        min = 1, max = 64
    )
    public static int hostileEntityRenderDistance = 64;

    // SCREENS

    @Switch(
        name = "Inventory Position",
        description = "Stop potion effects from shifting your inventory to the right.",
        category = "Screens", subcategory = "Inventory"
    )
    public static boolean inventoryPosition = true;

    @Switch(
        name = "Click Out of Containers",
        description = "Click outside a container to close the menu.",
        category = "Screens", subcategory = "Inventory"
    )
    public static boolean clickOutOfContainers;

    @Dropdown(
        name = "Inventory Scale",
        description = "Change the scale of your inventory independent of your GUI scale.",
        category = "Screens", subcategory = "Inventory",
        options = {"Off", "1 (Small)", "2 (Normal)", "3 (Large)", "4", "5 (Auto)"}
    )
    public static int inventoryScale = 0;

    public static int getInventoryScale() {
        return inventoryScale == 0 ? -1 : inventoryScale;
    }

    @Switch(
        name = "Remove Container Background",
        description = "Remove the dark background inside a container.",
        category = "Screens", subcategory = "General"
    )
    public static boolean removeContainerBackground = false;

    @Slider(
        name = "Container Opacity",
        description = "Change the opacity of supported containers.\nIncludes Chests & Survival Inventory.",
        category = "Screens", subcategory = "General",
        min = 0, max = 1.0F
    )
    public static float containerOpacity = 1.0f;

    @Slider(
        name = "Tab Opacity",
        description = "Change the tab list opacity.",
        category = "Screens", subcategory = "Tab",
        min = 0.0f, max = 1.0f
    )
    public static float tabOpacity = 1.0F;

    @Slider(
        name = "Tab Player Count",
        description = "Change how many players can display on tab.",
        category = "Screens", subcategory = "Tab",
        min = 10, max = 120
    )
    public static int tabPlayerCount = 80;

    @Switch(
        name = "GUI Crosshair",
        description = "Stop rendering the crosshair when in a GUI.",
        category = "Screens", subcategory = "General"
    )
    public static boolean guiCrosshair;

    @Info(
        text = "Supported servers for 1.11 chat length are servers that support 1.11 or above.",
        category = "Screens", subcategory = "Chat",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean chatLengthInfo = true;

    @Info(
        text = "Some servers may kick you for this despite supporting 1.11 or above.",
        category = "Screens", subcategory = "Chat",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean chatLengthInfo2 = true;

    @Switch(
        name = "1.11 Chat Length",
        description = "Extend the number of characters you can type from 100 to 256 on supported servers.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean extendedChatLength = true;

    @Info(
        text = "Transparent Chat can positively impact performance.",
        category = "Screens", subcategory = "Chat",
        type = InfoType.INFO
    )
    private static boolean transparentChatInfo = true;

    @Switch(
        name = "Transparent Chat",
        description = "Remove the background from chat.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean transparentChat;

    @Switch(
        name = "Chat Background When Open",
        description = "Add back the background when chat is open.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean transparentChatOnlyWhenClosed;

    @Info(
        text = "Transparent Chat Input Field can positively impact performance.",
        category = "Screens", subcategory = "Chat",
        type = InfoType.INFO,
        size = 2
    )
    private static boolean transparentChatInputFieldInfo = true;

    @Switch(
        name = "Transparent Chat Input Field",
        description = "Remove the background from chat's input field.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean transparentChatInputField;

    @Switch(
        name = "Extend Chat Background",
        description = "Extend the chat background all the way to the left of the screen.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean extendChatBackground = true;

    @Switch(
        name = "Tab Height",
        description = "Move the tab overlay down the selected amount of pixels when there's an active bossbar.",
        category = "Screens", subcategory = "Tab"
    )
    public static boolean tabHeightAllow = true;

    @Slider(
        name = "Set Tab Height",
        description = "Choose how many pixels tab will move down when there's an active bossbar.",
        category = "Screens", subcategory = "Tab",
        min = 10, max = 24
    )
    public static int tabHeight = 10;

    @Switch(
        name = "Compact Chat",
        description = "Clean up the chat by stacking duplicate messages.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean compactChat = true;

    @Switch(
        name = "Consecutive Compact Chat",
        description = "Only compact messages if they're consecutive.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean consecutiveCompactChat;

    @Slider(
        name = "Compact Chat Time",
        description = "Change the amount of time old messages take to stop being compacted. Measured in seconds.",
        category = "Screens", subcategory = "Chat",
        min = 1, max = 120
    )
    public static int compactChatTime = 60;

    @Switch(
        name = "Remove Blank Messages",
        description = "Stop messages with no content from showing up in chat.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean removeBlankMessages;

    @Switch(
        name = "Shift Chat",
        description = "Keep chat open while sending a message if Shift is held while pressing Enter.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean shiftChat;

    @Slider(
        name = "Chat Delay",
        description = "Delay chat messages if they're sent within the selected timeframe after the previous message. Measured in seconds.",
        category = "Screens", subcategory = "Chat",
        min = 0, max = 6
    )
    public static int chatDelay = 0;

    @Switch(
        name = "Startup Notification",
        description = "Notify how long the game took to start.",
        category = "Screens", subcategory = "General"
    )
    public static boolean startupNotification = true;

    @Switch(
        name = "Damage Glance",
        description = "View the damage value of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean damageGlance;

    @Switch(
        name = "Item Count Glance",
        description = "View the total amount of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean itemCountGlance;

    @Switch(
        name = "Enchantment Glance",
        description = "View the enchantments of the currently held item above your hotbar.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean enchantmentsGlance;

    @Switch(
        name = "Protection Percentage",
        description = "View how much total armor protection you have inside your inventory.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean protectionPercentage;

    @Switch(
        name = "Projectile Protection Percentage",
        description = "View how much total projectile protection you have inside your inventory.",
        category = "Screens", subcategory = "Combat Utilities"
    )
    public static boolean projectileProtectionPercentage;

    @Switch(
        name = "Chat Position",
        description = "Move the chat up 12 pixels to stop it from overlapping the health bar, as done in 1.12+.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean chatPosition = true;

    @Switch(
        name = "Chat Timestamps",
        description = "Add timestamps before a message.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean timestamps;

    @Switch(
        name = "Show Seconds on Timestamps",
        description = "Show the seconds on a timestamped message.",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean secondsOnTimestamps;

    @Dropdown(
        name = "Chat Timestamps Format",
        description = "Change the time format of Chat Timestamps.",
        category = "Screens", subcategory = "Chat",
        options = {"12 Hour", "24 Hour"}
    )
    public static int timestampsFormat = 0;

    @Dropdown(
        name = "Chat Timestamps Style",
        description = "Choose how Chat Timestamps should appear.",
        category = "Screens", subcategory = "Chat",
        options = {"Always Present", "Message Hover"}
    )
    public static int timestampsStyle = 0;

    @Switch(
        name = "Clean Main Menu",
        description = "Remove the Realms button on the main menu as it's useless on older versions.",
        category = "Screens", subcategory = "General"
    )
    public static boolean cleanMainMenu = true;

    @Dropdown(
        name = "Open to LAN Replacement",
        description = "Modify the Open to LAN button to either redirect to the server list or be removed.",
        category = "Screens", subcategory = "General",
        options = {"Default", "Server List", "Remove"}
    )
    public static int openToLanReplacement = 0;

    @Switch(
        name = "Image Preview",
        description = "Preview image links when hovering over a supported URL." +
            "\nPress Shift to use fullscreen and Control to render in native image resolution.",
        category = "Screens", subcategory = "Image Preview"
    )
    public static boolean imagePreview = true;

    @Slider(
        name = "Image Preview Width",
        description = "The %% of screen width to be used for image preview.",
        category = "Screens", subcategory = "Image Preview",
        min = 0F, max = 1F
    )
    public static float imagePreviewWidth = 0.50F;

    @Switch(
        name = "Safe Chat Clicks",
        description = "Show the command or link that is run/opened on click. ",
        category = "Screens", subcategory = "Chat"
    )
    public static boolean safeChatClicks;

    @Switch(
        name = "Smart Disconnect",
        description = "Choose between disconnecting or relogging when clicking the disconnect button.",
        category = "Screens", subcategory = "General"
    )
    public static boolean smartDisconnect;

    @Switch(
        name = "Confirm Quit",
        description = "Prevent closing the game through the Quit Game button without confirmation.",
        category = "Screens", subcategory = "General"
    )
    public static boolean confirmQuit;

    // SCREENSHOTS

    @Info(
        text = "Essential's Screenshot Manager must be disabled for this to work.",
        category = "Screenshots", subcategory = "General",
        type = InfoType.WARNING,
        size = 2
    )
    private static boolean screenshotManagerWarning = true;

    @Switch(
        name = "Screenshot Manager",
        description = "Change the way screenshotting works as a whole, creating a whole new process to screenshotting such as uploading to Imgur, copying to clipboard, etc.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean screenshotManager = false;

    @Switch(
        name = "No Feedback",
        description = "Remove the messages from screenshots entirely.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean screenshotNoFeedback;

    @Switch(
        name = "Auto Copy Screenshot",
        description = "Automatically copy screenshots to the clipboard when taken.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean autoCopyScreenshot;

    @Switch(
        name = "Screenshot Preview",
        description = "Preview your screenshot when taken in the bottom right corner.",
        category = "Screenshots", subcategory = "General"
    )
    public static boolean screenshotPreview;

    @Slider(
        name = "Preview Time",
        description = "Adjust how long the preview should stay on the screen before sliding out.\nTime is measured in seconds.",
        category = "Screenshots", subcategory = "General",
        min = 1, max = 5
    )
    public static int previewTime = 3;

    @Dropdown(
        name = "Preview Animation",
        description = "Select an animation style for the screenshot preview.",
        category = "Screenshots", subcategory = "General",
        options = {"iOS Style", "Slide Out", "None"}
    )
    public static int previewAnimationStyle = 0;

    @Slider(
        name = "Preview Scale",
        description = "Change the scale of the preview.",
        category = "Screenshots", subcategory = "General",
        min = 0F, max = 1F
    )
    public static float previewScale = 1.0F;

    @Switch(
        name = "Compact Response",
        description = "Compact the message given when screenshotting.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean compactScreenshotResponse;

    @Switch(
        name = "Favorite Screenshot",
        description = "Show a text component that allows you to favorite a screenshot.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean favoriteScreenshot = true;

    @Switch(
        name = "Delete Screenshot",
        description = "Show a text component that allows you to delete a screenshot.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean deleteScreenshot = true;

    @Switch(
        name = "Upload Screenshot",
        description = "Show a text component that allows you to upload a screenshot to Imgur.\nSupport for custom services is currently planned.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean uploadScreenshot = true;

    @Switch(
        name = "Copy Screenshot",
        description = "Show a text component that allows you to copy a screenshot.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean copyScreenshot = true;

    @Switch(
        name = "Open Screenshots Folder",
        description = "Show a text component that allows you to open the screenshots folder.",
        category = "Screenshots", subcategory = "Feedback"
    )
    public static boolean openScreenshotsFolder = true;

    // EXPERIMENTAL

    /*Switchty(
        name = "Cache Entrypoints",
        description = "Cache Forge mod entry points, improving startup time as Forge no longer needs to walk through " +
            "every class to find the @Mod annotation.",
        category = "Experimental", subcategory = "Mod Discovery"
    )
    public static boolean cacheEntrypoints = true;*/

    @Info(
        text = "This may cause stuff with animations to feel \"choppy\".",
        category = "Experimental", subcategory = "HUD Caching",
        type = InfoType.WARNING
    )
    private static boolean hudCachingWarning = true;

    @Switch(
        name = "HUD Caching",
        description = "Reuse frames from the HUD instead of constantly recreating them every frame, as most HUD elements will stay the same for a long amount of time.",
        category = "Experimental", subcategory = "HUD Caching"
    )
    public static boolean hudCaching;

    // HIDDEN

    public static boolean labyModMoment = true;
    public static String selectedAudioDevice = "";

    public static PatcherConfig INSTANCE = new PatcherConfig(); // Needs to be at the bottom or the default values take priority

    public PatcherConfig() {
        super(new Mod("Patcher", ModType.UTIL_QOL, new VigilanceMigrator("./config/patcher.toml")), "patcher.json");
        initialize();

        Runnable reloadWorld = () -> Minecraft.getMinecraft().renderGlobal.loadRenderers();
        addListener("fullbright", reloadWorld);
        addListener("removeGroundFoliage", reloadWorld);
        addListener("vanillaGlassPanes", reloadWorld);

        try {
            addDependency("smartFullbright", "fullbright");
            addDependency("unfocusedFPSAmount", "unfocusedFPS");
            addDependency("instantFullscreen", "windowedFullscreen");
            addDependency("tabHeight", "tabHeightAllow");
            addDependency("consecutiveCompactChat", "compactChat");
            addDependency("compactChatTime", "compactChat");
            addDependency("timestampsFormat", "timestamps");
            addDependency("timestampsStyle", "timestamps");
            addDependency("secondsOnTimestamps", "timestamps");
            addDependency("imagePreviewWidth", "imagePreview");
            addDependency("transparentChatOnlyWhenClosed", "transparentChat");

            Arrays.asList(
                "slownessFovModifierFloat", "speedFovModifierFloat",
                "bowFovModifierFloat", "sprintingFovModifierFloat"
            ).forEach(property -> addDependency(property, "allowFovModifying"));

            addDependency("logOptimizerLength", "logOptimizer");
            addDependency("dynamicZoomSensitivity", "scrollToZoom");
            addDependency("smoothZoomAnimation", "scrollToZoom");
            addDependency("smoothZoomAlgorithm", "smoothZoomAnimation");

            Arrays.asList(
                "cullingInterval", "smartEntityCulling", "dontCullNametags",
                "dontCullEntityNametags", "dontCullArmorStandNametags", "checkArmorstandRules"
            ).forEach(property -> addDependency(property, "entityCulling"));

            Arrays.asList(
                "entityRenderDistance", "playerRenderDistance",
                "passiveEntityRenderDistance", "hostileEntityRenderDistance"
            ).forEach(property -> addDependency(property, "entityRenderDistanceToggle"));

            addDependency("cacheFontData", "optimizedFontRenderer");
            addDependency("chunkUpdateLimit", "limitChunks");

            Arrays.asList(
                "screenshotNoFeedback", "compactScreenshotResponse", "autoCopyScreenshot", "screenshotPreview",
                "previewTime", "previewAnimationStyle", "previewScale", "favoriteScreenshot",
                "deleteScreenshot", "uploadScreenshot", "copyScreenshot", "openScreenshotsFolder"
            ).forEach(property -> addDependency(property, "screenshotManager"));

            hideIf("instantFullscreen", () -> !SystemUtils.IS_OS_WINDOWS);

            Supplier<Boolean> noOptiFine = () -> ClassTransformer.optifineVersion.equals("NONE");
            Arrays.asList(
                "scrollToZoom", "normalZoomSensitivity", "customZoomSensitivity", "smoothZoomAnimation",
                "smoothZoomAnimationWhenScrolling", "smoothZoomAlgorithm", "toggleToZoom", "normalFpsCounter",
                "useVanillaMetricsRenderer", "renderHandWhenZoomed", "smartFullbright", "smartEntityCulling",
                "dynamicZoomSensitivity"
            ).forEach(property -> hideIf(property, noOptiFine));

            Supplier<Boolean> smoothFontDetected = () -> ClassTransformer.smoothFontDetected;
            hideIf("optimizedFontRenderer", smoothFontDetected);
            hideIf("cacheFontData", smoothFontDetected);

            // these are all already built into the client by 1.12, so no
            // need to show them in the config menu

            //noinspection ConstantConditions
            Supplier<Boolean> minecraft112 = () -> ForgeVersion.mcVersion.equals("1.12.2");
            Arrays.asList(
                "resourceExploitFix", "newKeybindHandling", "separateResourceLoading", "futureHitBoxes", "farmSelectionBoxesInfo",
                "leftHandInFirstPerson", "extendedChatLength", "chatPosition",
                "parallaxFix", "crosshairPerspective", "extendChatBackground", "vanillaGlassPanes"
            ).forEach(property -> hideIf(property, minecraft112));

            hideIf("keyboardLayout", () -> !SystemUtils.IS_OS_LINUX);
        } catch (Exception e) {
            Patcher.instance.getLogger().error("Failed to access property.", e);
        }
    }
}
