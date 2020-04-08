package club.sk1er.patcher.screen;

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.Patcher;
import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.mojangapi.MojangAPI;
import me.kbrewster.mojangapi.profile.Name;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class ScreenHistory extends GuiScreen {

  private final List<String> names = new ArrayList<>();
  private GuiTextField nameField;
  private String name;
  private int offset;

  public ScreenHistory() {
    this("");
  }

  public ScreenHistory(String name) {
    this.name = name;
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
        } catch (IOException | APIException e) {
          e.printStackTrace();
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
        }
      });
    } catch (Exception e) {
      Patcher.instance.getLogger().warn("User catch failed, tried fetching {}", username, e);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    nameField = new GuiTextField(0, mc.fontRendererObj, width / 2 - (115 / 2), height / 5 + 10, 115,
        20);
    nameField.setText(name);
    nameField.setFocused(true);
    nameField.setMaxStringLength(16);
    Keyboard.enableRepeatEvents(true);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
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
    drawCenteredString(mc.fontRendererObj, "Name History", width / 2, height / 5, -1);

    //Text Box
    nameField.drawTextBox();
    int defaultColour = -1;

    // Check if names have been scrolled outside of bounding box.
    // Highlight current and original names.
    int bound = names.size();
    for (int i = 0; i < bound; i++) {
      float xPos = (width >> 1) - (115 >> 1);
      float yPos = bottom + (i * 10) + offset;
      if (yPos < (height / 5f) + 32) {
        continue;
      }

      if (i == 0) {
        mc.fontRendererObj.drawString(names.get(i), (int) xPos, (int) yPos, Color.YELLOW.getRGB());
      } else {
        if (i == names.size() - 1) {
          mc.fontRendererObj.drawString(names.get(i), (int) xPos, (int) yPos, Color.GREEN.getRGB());
        } else {
          mc.fontRendererObj.drawString(names.get(i), (int) xPos, (int) yPos, defaultColour);
        }
      }
    }
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
  }
}
