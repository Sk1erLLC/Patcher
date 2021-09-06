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

package club.sk1er.patcher.hooks;

import net.minecraft.client.gui.FontRenderer;

public class OptiFineHook {

    public float getCharWidth(FontRenderer renderer, char c) {//Remapped by OptiFineHookTransformer to Optifine if needed
        return renderer.getCharWidth(c);
    }

    public float getOptifineBoldOffset(FontRenderer renderer) { //Remapped by FontRendererHookTransformer to Optifine if needed
        return 1;
    }

}
