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
import club.sk1er.elementa.UIConstraints;
import club.sk1er.elementa.components.UIImage;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.components.image.DefaultLoadingImage;
import club.sk1er.elementa.constraints.ConstantColorConstraint;
import club.sk1er.elementa.constraints.PixelConstraint;
import club.sk1er.elementa.constraints.RelativeConstraint;
import club.sk1er.elementa.constraints.animation.AnimatingConstraints;
import club.sk1er.elementa.constraints.animation.Animations;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

/**
 * Used for rendering the current screenshot on the screen when "Screenshot Preview" is enabled.
 */
public class Viewer {

    /**
     * Create an instance of the class for outside usage.
     */
    private static final Viewer instance = new Viewer();

    /**
     * Create a {@link Window} instance.
     */
    private Window currentWindow;

    /**
     * Render the current window, which contains all UI Components.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderScreenshot(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (currentWindow == null) return;
        currentWindow.draw();
    }

    /**
     * Crate a new capture, then render the animation.
     *
     * @param image The screenshot just taken.
     */
    public void newCapture(BufferedImage image) {
        currentWindow = new Window();
        instantiateComponents(image);
    }

    /**
     * Animate the screenshot, imitating the iOS screenshot animation.
     *
     * @param image The screenshot just taken and created by {@link Viewer#newCapture(BufferedImage)}
     */
    private void instantiateComponents(BufferedImage image) {
        UIComponent imageComponent = new UIImage(CompletableFuture.completedFuture(image), DefaultLoadingImage.INSTANCE)
            .setX(new PixelConstraint(0))
            .setY(new PixelConstraint(0))
            .setWidth(new RelativeConstraint(1))
            .setHeight(new RelativeConstraint(1));

        currentWindow.addChild(imageComponent);

        imageComponent.onMouseEnterRunnable(() -> {
            UIConstraints constraints = imageComponent.getConstraints().copy();
            constraints.setColor(new ConstantColorConstraint(Color.WHITE));
            imageComponent.setConstraints(constraints);
            fadeOutAnimation(imageComponent);
        });

        AnimatingConstraints anim = imageComponent.makeAnimation()
            .setWidthAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(1 / 3f))
            .setHeightAnimation(Animations.OUT_QUINT, 1.5f, new RelativeConstraint(1 / 3f))
            .setXAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
            .setYAnimation(Animations.OUT_QUINT, 1.5f, new PixelConstraint(10, true))
            .onCompleteRunnable(() -> fadeOutAnimation(imageComponent));

        imageComponent.animateTo(anim);
    }

    /**
     * Create a fading out animation, removing the screenshot from the screen, and closing the current window.
     *
     * @param container The current UI Component being animated, which in this case is the screenshot.
     */
    private void fadeOutAnimation(UIComponent container) {
        AnimatingConstraints newAnim = container.makeAnimation()
            .setColorAnimation(Animations.OUT_CUBIC, 1f,
                new ConstantColorConstraint(new Color(255, 255, 255, 0)),
                3f
            ).onCompleteRunnable(() -> currentWindow = null);

        container.animateTo(newAnim);
    }

    /**
     * Create a getter for the {@link Viewer} instance for outside usage.
     *
     * @return The Viewer instance.
     */
    public static Viewer getInstance() {
        return instance;
    }
}
