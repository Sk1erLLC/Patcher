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

package club.sk1er.patcher.screen;

import club.sk1er.elementa.components.UIRoundedRectangle;
import club.sk1er.patcher.util.name.NameFetcher;
import club.sk1er.vigilance.gui.VigilancePalette;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class ScreenHistoryOld extends GuiScreen {
    private final NameFetcher nameFetcher = new NameFetcher();
    private final boolean focus;
    private GuiTextField nameField;
    private String name;
    private int offset;

    public ScreenHistoryOld() {
        this(null, true);
    }

    public ScreenHistoryOld(String name, boolean focus) {
        this.name = name;
        this.focus = focus;
        getNameHistory(name);
    }

    public void getNameHistory(String username) {
        offset = 0;
        nameFetcher.execute(username);
    }

    @Override
    public void initGui() {
        super.initGui();
        nameField = new GuiTextField(0, fontRendererObj, (width >> 1) - 57, (height / 5) + 10, 115, 20);
        nameField.setText(name);
        nameField.setFocused(focus);
        nameField.setMaxStringLength(16);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        final int left = (width / 3) - 1;
        final int top = (height / 5) - 3;
        final int right = width - (width / 3);
        final int bottom = (height / 5) + 37;

        UIRoundedRectangle.Companion.drawRoundedRectangle(
            left, top,
            right, bottom + (nameFetcher.getNames().size() * 10) + offset,
            3, VigilancePalette.INSTANCE.getBACKGROUND()
        );

        drawCenteredString(fontRendererObj, "Name History", width >> 1, height / 5, -1);
        nameField.drawTextBox();

        // Check if names have been scrolled outside of bounding box.
        // Highlight current and original names.
        for (int currentName = 0; currentName < nameFetcher.getNames().size(); currentName++) {
            final float xPos = width >> 1;
            final float yPos = bottom + (currentName * 10) + offset - 1;
            if (yPos < (height / 5f) + 35) {
                continue;
            }

            final String text = nameFetcher.getNames().get(currentName);
            if (currentName == 0) {
                drawCenteredString(
                    fontRendererObj, text + " » Original",
                    (int) xPos, (int) yPos,
                    new Color(0, 167, 81).getRGB()
                );
            } else {
                drawCenteredString(
                    fontRendererObj, text,
                    (int) xPos, (int) yPos,
                    currentName == nameFetcher.getNames().size() - 1 ? new Color(1, 162, 82).getRGB() : -1
                );
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN) {
            nameFetcher.getNames().clear();
            this.getNameHistory(this.nameField.getText());
        }

        this.nameField.textboxKeyTyped(typedChar, keyCode);
        this.name = this.nameField.getText();

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        nameFetcher.getNames().clear();
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int scrollBounds = Mouse.getEventDWheel();
        if (scrollBounds < 0) {
            // works out length of scrollable area
            final int size = nameFetcher.getNames().size();
            final int length = height / 5 - (size * 9);
            if (offset - length + 1 > -size && length <= size) {
                // regions it cant exceed
                offset -= 10;
            }
        } else if (scrollBounds > 0 && offset < 0) {
            offset += 10;
        }
    }
}
