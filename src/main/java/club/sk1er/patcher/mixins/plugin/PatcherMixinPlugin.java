package club.sk1er.patcher.mixins.plugin;

import club.sk1er.patcher.tweaker.ClassTransformer;
import com.google.common.collect.ArrayListMultimap;
import kotlin.text.StringsKt;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PatcherMixinPlugin implements IMixinConfigPlugin {
    private static final String LABYMOD_CLASS = "net/labymod/main/LabyMod.class";
    private static final String SMOOTHFONT_CLASS = "bre/smoothfont/mod_SmoothFont.class";

    private static final ArrayListMultimap<String, String> CONFLICTING_CLASSES = ArrayListMultimap.create();

    static {
        CONFLICTING_CLASSES.put("GuiContainerMixin_MouseBindFixThatLabyBreaks", LABYMOD_CLASS);
        CONFLICTING_CLASSES.put("FontRendererMixin_Optimization", SMOOTHFONT_CLASS);
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixinPackage = StringsKt.substringBeforeLast(mixinClassName, '.', mixinClassName);
        if (mixinPackage.endsWith("optifine") && "NONE".equals(ClassTransformer.optifineVersion)) {
            // OptiFine isn't present, let's not apply this
            return false;
        }
        for (String conflictingClass : CONFLICTING_CLASSES.get(StringsKt.substringAfterLast(mixinClassName, '.', mixinClassName))) {
            if (this.getClass().getClassLoader().getResource(conflictingClass) != null) {
                // Conflicting class is present, let's not apply this
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
