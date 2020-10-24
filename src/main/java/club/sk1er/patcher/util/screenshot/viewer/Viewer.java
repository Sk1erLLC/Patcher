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

package club.sk1er.patcher.util.screenshot.viewer;

import club.sk1er.elementa.UIComponent;
import club.sk1er.elementa.components.UIImage;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.components.image.DefaultLoadingImage;
import club.sk1er.elementa.constraints.PixelConstraint;
import club.sk1er.elementa.constraints.RelativeConstraint;
import club.sk1er.elementa.constraints.animation.AnimatingConstraints;
import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

/**
 * Used for rendering the current screenshot on the screen when "Screenshot Preview" is enabled.
 */
public class Viewer {

    /**
     * Create a {@link Window} instance.
     */
    private Window currentWindow;

    //private File screenshotFile;

    /**
     * Render the current window, which contains all UI Components.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderScreenshot(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (this.currentWindow == null) return;
        this.currentWindow.draw();
    }

    /**
     * Crate a new capture, then render the animation.
     *
     * @param image The screenshot just taken.
     */
    public void newCapture(BufferedImage image/*, File screenshot*/) {
        this.currentWindow = new Window();
        //this.screenshotFile = screenshot;
        instantiateComponents(image);
    }

    /**
     * Animate the screenshot, imitating the iOS screenshot animation.
     *
     * @param image The screenshot just taken and created by {@link Viewer#newCapture(BufferedImage)}
     */
    private void instantiateComponents(BufferedImage image) {
        final UIComponent imageComponent = new UIImage(CompletableFuture.completedFuture(image), DefaultLoadingImage.INSTANCE)
            .setX(new PixelConstraint(0))
            .setY(new PixelConstraint(0))
            .setWidth(new RelativeConstraint(1))
            .setHeight(new RelativeConstraint(1));

        currentWindow.addChild(imageComponent);

        // todo: doesn't do anything, probably because this isn't a "gui", so there's no way to process the click event?
        // need to ask false or matt on how we should do this
        /*imageComponent.onMouseClickConsumer((mouseX, mouseY, mouseButton) -> {
            try {
                ModCoreDesktop.INSTANCE.open(this.screenshotFile);
            } catch (Exception e) {
                Patcher.instance.getLogger().error("Failed to open screenshot through preview.", e);
            }
        });*/

        final AnimatingConstraints appearAnimation = imageComponent.makeAnimation()
            .setWidthAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(1 / 3f))
            .setHeightAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(1 / 3f))
            .setXAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
            .setYAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
            .onCompleteRunnable(() -> slideOutAnimation(imageComponent));

        imageComponent.animateTo(appearAnimation);
    }

    /**
     * Create a fading out animation, removing the screenshot from the screen, and closing the current window.
     *
     * @param container The current UI Component being animated, which in this case is the screenshot.
     */
    private void slideOutAnimation(UIComponent container) {
        AnimatingConstraints slideAnimation = container.makeAnimation()
            .setXAnimation(Animations.IN_OUT_CIRCULAR, 1f,
                new RelativeConstraint(2),
                PatcherConfig.previewTime
            ).onCompleteRunnable(() -> {
                this.currentWindow = null;
                /*this.screenshotFile = null;*/
            });

        container.animateTo(slideAnimation);
    }
}
