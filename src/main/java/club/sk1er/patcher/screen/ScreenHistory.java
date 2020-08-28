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

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.Patcher;
import me.kbrewster.mojangapi.MojangAPI;
import me.kbrewster.mojangapi.profile.Name;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScreenHistory extends GuiScreen {

    // add usernames to a list
    private final List<String> names = new ArrayList<>();

    // should the input field for name searching be focused on init?
    private final boolean focus;

    // input field
    private GuiTextField nameField;

    // inserted name
    private String name;

    // height offset
    private int offset;

    private String exceptionName;

    public ScreenHistory() {
        this("", true);
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
                            names.add(String.format("%s > %s", name, format.format(history.getChangedToAt())));
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
        nameField = new GuiTextField(0, fontRendererObj, width / 2 - (115 / 2), height / 5 + 10, 115,
            20);
        nameField.setText(name);
        nameField.setFocused(focus);
        nameField.setMaxStringLength(16);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        fontRendererObj.drawStringWithShadow("* this design is temporary.", 3, 3, new Color(125, 125, 125, 180).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);

        int left = width / 5 - 1;
        int top = height / 5 - 1;
        int right = width - width / 5;
        int bottom = height / 5 + 33;

        //BG
        drawRect(left, top, right, bottom + (names.size() * 10), new Color(0, 0, 0, 100).getRGB());

        //TITLE BG
        drawRect(left, top, right, bottom, new Color(0, 0, 0, 150).getRGB());

        //TITLE;
        drawCenteredString(fontRendererObj, "Name History", width / 2, height / 5, -1);

        //Text Box
        nameField.drawTextBox();
        int defaultColour = -1;

        // Check if names have been scrolled outside of bounding box.
        // Highlight current and original names.
        int bound = names.size();
        for (int i = 0; i < bound; i++) {
            float xPos = width >> 1;
            float yPos = bottom + (i * 10) + offset;
            if (yPos < (height / 5f) + 32) {
                continue;
            }

            if (i == 0) {
                drawCenteredString(fontRendererObj, names.get(i), (int) xPos, (int) yPos, Color.YELLOW.getRGB());
            } else {
                if (i == names.size() - 1) {
                    drawCenteredString(fontRendererObj, names.get(i), (int) xPos, (int) yPos, Color.GREEN.getRGB());
                } else {
                    drawCenteredString(fontRendererObj, names.get(i), (int) xPos, (int) yPos, defaultColour);
                }
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
            names.clear();
            getNameHistory(nameField.getText());
        }

        nameField.textboxKeyTyped(typedChar, keyCode);
        name = nameField.getText();

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
        int i = Mouse.getEventDWheel();
        if (i < 0) {
            // works out length of scrollable area
            int length = height / 5 - (names.size() * fontRendererObj.FONT_HEIGHT);

            if (offset - length + 1 > -names.size() && length <= names.size()) {
                // regions it cant exceed
                offset -= 10;
            }
        } else if (i > 0 && offset < 0) {
            offset += 10;
        }
    }
}
