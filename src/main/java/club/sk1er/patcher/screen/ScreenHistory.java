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
import club.sk1er.patcher.Patcher;
import club.sk1er.vigilance.gui.VigilancePalette;
import me.kbrewster.mojangapi.MojangAPI;
import me.kbrewster.mojangapi.profile.Name;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.modcore.api.utils.Multithreading;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScreenHistory extends GuiScreen {

    private final List<String> names = new ArrayList<>();
    private final boolean focus;
    private GuiTextField nameField;
    private String name;
    private int offset;
    private String exceptionName;

    public ScreenHistory() {
        this(null, true);
    }

    public ScreenHistory(String name, boolean focus) {
        this.name = name;
        this.focus = focus;
        getNameHistory(name);
    }

    private void getNameHistory(String username) {
        offset = 0;
        try {
            if (username.isEmpty()) {
                return;
            }

            Multithreading.runAsync(() -> {
                UUID uuid = null;
                try {
                    uuid = MojangAPI.getUUID(username);
                } catch (Exception e) {
                    exceptionName = e.getClass().getSimpleName();
                    Patcher.instance.getLogger().warn("Failed fetching UUID.", e);
                }

                if (uuid != null) {
                    for (Name history : MojangAPI.getNameHistory(uuid)) {
                        String name = history.getName();
                        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                        if (history.getChangedToAt() == 0) {
                            names.add(name);
                        } else {
                            names.add(String.format("%s » %s", name, format.format(history.getChangedToAt())));
                        }
                    }
                } else {
                    names.add("Failed to fetch " + username + "'s names: " + exceptionName);
                }
            });
        } catch (Exception e) {
            Patcher.instance.getLogger().warn("User catch failed, tried fetching {}.", username, e);
        }
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
            right, bottom + (names.size() * 10) + offset,
            3, VigilancePalette.INSTANCE.getBACKGROUND()
        );

        drawCenteredString(fontRendererObj, "Name History", width >> 1, height / 5, -1);
        nameField.drawTextBox();

        // Check if names have been scrolled outside of bounding box.
        // Highlight current and original names.
        for (int currentName = 0; currentName < names.size(); currentName++) {
            final float xPos = width >> 1;
            final float yPos = bottom + (currentName * 10) + offset - 1;
            if (yPos < (height / 5f) + 35) {
                continue;
            }

            final String text = names.get(currentName);
            if (currentName == 0) {
                drawCenteredString(
                    fontRendererObj, exceptionName != null ? text : text + " » Original",
                    (int) xPos, (int) yPos,
                    new Color(0, 167, 81).getRGB()
                );
            } else {
                drawCenteredString(
                    fontRendererObj, text,
                    (int) xPos, (int) yPos,
                    currentName == names.size() - 1 ? new Color(1, 162, 82).getRGB() : -1
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
            this.names.clear();
            this.exceptionName = null;
            this.getNameHistory(this.nameField.getText());
        }

        this.nameField.textboxKeyTyped(typedChar, keyCode);
        this.name = this.nameField.getText();

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        names.clear();
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        exceptionName = null;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int scrollBounds = Mouse.getEventDWheel();
        if (scrollBounds < 0) {
            // works out length of scrollable area
            final int size = names.size();
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
