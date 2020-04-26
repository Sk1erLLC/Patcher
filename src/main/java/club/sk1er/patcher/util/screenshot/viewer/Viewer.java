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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class Viewer {
    private Window currentWindow;

    @SubscribeEvent
    public void renderScreenshot(RenderGameOverlayEvent.Post event) {
        if (!event.type.equals(RenderGameOverlayEvent.ElementType.TEXT)) return;
        if (currentWindow == null) return;
        currentWindow.draw();
    }

    private static final Viewer instance = new Viewer();

    public static Viewer getInstance() {
        return instance;
    }

    public void newCapture(BufferedImage image) {
        currentWindow = new Window();
        instantiateComponents(image);
    }

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

    private void fadeOutAnimation(UIComponent container) {
        AnimatingConstraints newAnim = container.makeAnimation()
            .setColorAnimation(Animations.OUT_CUBIC, 1f,
                new ConstantColorConstraint(new Color(255, 255, 255, 0)),
                3f
            ).onCompleteRunnable(() -> currentWindow = null);

        container.animateTo(newAnim);
    }
}
