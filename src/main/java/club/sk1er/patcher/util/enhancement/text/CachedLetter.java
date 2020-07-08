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

package club.sk1er.patcher.util.enhancement.text;

public class CachedLetter {

    private final float width;
    private final float textureX;
    private final float textureY;

    public CachedLetter(float textureX, float textureY, float width) {
        this.width = width;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public float getTextureX() {
        return textureX;
    }

    public float getTextureY() {
        return textureY;
    }

    public float getWidth() {
        return width;
    }
}
