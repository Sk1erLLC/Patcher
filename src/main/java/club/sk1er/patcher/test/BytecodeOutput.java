package club.sk1er.patcher.test;

import net.minecraft.client.Minecraft;

public class BytecodeOutput {

    protected Minecraft mc = Minecraft.getMinecraft();
    public static boolean transparent;

    public void drawBackground() {
        if (mc.theWorld != null && transparent) return;

        System.out.println("Ahh");
    }
}
