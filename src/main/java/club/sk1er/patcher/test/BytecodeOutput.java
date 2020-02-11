package club.sk1er.patcher.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public class BytecodeOutput extends GuiSlot {
    public BytecodeOutput(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
    }

    @Override
    protected int getSize() {
        return 0;
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     *
     * @param slotIndex
     * @param isDoubleClick
     * @param mouseX
     * @param mouseY
     */
    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        mc.refreshResources();
    }

    /**
     * Returns true if the element passed in is currently selected
     *
     * @param slotIndex
     */
    @Override
    protected boolean isSelected(int slotIndex) {
        return false;
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {

    }
}
