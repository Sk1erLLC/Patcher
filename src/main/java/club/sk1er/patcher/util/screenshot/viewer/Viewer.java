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
import club.sk1er.elementa.constraints.ConstantColorConstraint;
import club.sk1er.elementa.constraints.PixelConstraint;
import club.sk1er.elementa.constraints.RelativeConstraint;
import club.sk1er.elementa.constraints.animation.AnimatingConstraints;
import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class Viewer {

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

        final int style = PatcherConfig.previewAnimationStyle;
        final float previewScale = (float) 1 / PatcherConfig.previewScale;
        AnimatingConstraints animation = null;
        if (style == 0) {
            // ios style
            animation = imageComponent.makeAnimation()
                .setWidthAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(previewScale))
                .setHeightAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(previewScale))
                .setXAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
                .setYAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
                .onCompleteRunnable(() -> slideOutAnimation(imageComponent));
        } else if (style == 1) {
            // slide out
            imageComponent
                .setWidth(new RelativeConstraint(previewScale))
                .setHeight(new RelativeConstraint(previewScale))
                .setX(new RelativeConstraint(1))
                .setY(new PixelConstraint(10, true));
            animation = imageComponent.makeAnimation()
                .setXAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
                .onCompleteRunnable(() -> slideOutAnimation(imageComponent));
        } else if (style == 2) {
            // none
            imageComponent
                .setWidth(new RelativeConstraint(previewScale))
                .setHeight(new RelativeConstraint(previewScale))
                .setX(new PixelConstraint(10, true))
                .setY(new PixelConstraint(10, true));

            animation = imageComponent.makeAnimation()
                .onCompleteRunnable(() -> fakeAnimation(imageComponent));
        }

        if (animation != null) {
            imageComponent.animateTo(animation);
        }
    }

    private void slideOutAnimation(UIComponent container) {
        final AnimatingConstraints slideAnimation = container.makeAnimation()
            .setXAnimation(Animations.IN_OUT_CIRCULAR, 1f,
                new RelativeConstraint(2),
                PatcherConfig.previewTime
            ).onCompleteRunnable(() -> {
                this.currentWindow = null;
                /*this.screenshotFile = null;*/
            });

        container.animateTo(slideAnimation);
    }

    private void fakeAnimation(UIComponent container) {
        final AnimatingConstraints slideAnimation = container.makeAnimation()
            .setColorAnimation(Animations.IN_OUT_CIRCULAR, 1f,
                new ConstantColorConstraint(new Color(255, 255, 255, 255)),
                PatcherConfig.previewTime
            ).onCompleteRunnable(() -> {
                this.currentWindow = null;
                /*this.screenshotFile = null;*/
            });

        container.animateTo(slideAnimation);
    }
}
