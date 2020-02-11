package club.sk1er.patcher.hooks;

import club.sk1er.patcher.tweaker.asm.GuiPlayerTabOverlayTransformer;
import net.minecraft.client.gui.Gui;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

    public static int getColor(int i1, int j2, int k2) {
        int width = j2 + i1;
        if (k2 != 0) {
            Gui.drawRect(j2, k2 - 1, width, k2, 150 << 24);
        }

        if (k2 % 2 == 0) {
            return Integer.MAX_VALUE & ~(60 << 24);
        }

        return 45 << 24;
    }
}
