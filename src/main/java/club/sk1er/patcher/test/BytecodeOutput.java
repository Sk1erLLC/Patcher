package club.sk1er.patcher.test;

import java.io.IOException;

public class BytecodeOutput {

  public static boolean fuck;

  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if (fuck) {
      checkHotbarKeys(mouseButton - 100);
    }

    System.out.println("h");
  }

  protected boolean checkHotbarKeys(int keyCode) {
    return false;
  }
}
