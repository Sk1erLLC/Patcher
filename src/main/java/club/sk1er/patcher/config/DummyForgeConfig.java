package club.sk1er.patcher.config;

import club.sk1er.patcher.Patcher;
import gg.essential.api.utils.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

@SuppressWarnings("unused")
public class DummyForgeConfig implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraft) {
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    //#if MC==10809
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return DummyForgeConfigGUI.class;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement runtimeOptionCategoryElement) {
        return null;
    }
    //#else
    //$$ @Override
    //$$ public boolean hasConfigGui() {
    //$$     return true;
    //$$ }

    //$$ @Override
    //$$ public GuiScreen createConfigGui(GuiScreen guiScreen) {
    //$$     return Patcher.instance.getPatcherConfig().gui();
    //$$ }
    //#endif

    public static class DummyForgeConfigGUI extends GuiScreen {
        public DummyForgeConfigGUI(GuiScreen parent) {
            GuiUtil.open(Patcher.instance.getPatcherConfig().gui());
        }
    }
}
