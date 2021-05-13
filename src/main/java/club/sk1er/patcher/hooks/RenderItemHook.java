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

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.hash.impl.ItemHash;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RenderItemHook {

    private final EnhancedItemRenderer itemRenderer = EnhancementManager.getInstance().getEnhancement(EnhancedItemRenderer.class);
    private ItemHash itemHash;
    private int glList;

    public boolean renderModelStart(IBakedModel model, int color, ItemStack stack) {
        itemHash = null;
        glList = 0;

        if (PatcherConfig.optimizedItemRenderer) {
            List<Object> itemInformation = new ArrayList<>();
            itemInformation.add(model.isGui3d());
            itemInformation.add(model.isBuiltInRenderer());
            itemInformation.add(model.isAmbientOcclusion());
            itemInformation.add(model.getParticleTexture());
            itemInformation.addAll(model.getGeneralQuads());
            itemInformation.add(color);
            itemInformation.add(stack == null ? "" : stack.getUnlocalizedName());
            itemInformation.add(stack == null ? "" : stack.getItemDamage());
            itemInformation.add(stack == null ? "" : stack.getMetadata());
            itemInformation.add(stack == null ? "" : stack.getTagCompound());

            itemHash = new ItemHash(itemInformation.toArray());
            Integer integer = itemRenderer.getItemCache().getIfPresent(itemHash);

            if (integer != null) {
                GlStateManager.callList(integer);
                GlStateManager.resetColor();
                return true;
            }

            glList = itemRenderer.getGlList();
            GL11.glNewList(glList, GL11.GL_COMPILE_AND_EXECUTE);
        }

        return false;
    }

    public void renderModelEnd() {
        if (PatcherConfig.optimizedItemRenderer) {
            GL11.glEndList();
            itemRenderer.getItemCache().put(itemHash, glList);
        }
    }
}
