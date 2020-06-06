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

package club.sk1er.patcher.util.cache;

public class CachedString {

    private final String text;
    private final int listId;

    private int lastTexture;
    private float width, height;

    private float lastRed, lastBlue, lastGreen, lastAlpha;

    public CachedString(String text, int listId, float width, float height) {
        this.text = text;
        this.listId = listId;
        this.width = width;
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public int getListId() {
        return listId;
    }

    public int getLastTexture() {
        return lastTexture;
    }

    public void setLastTexture(int lastTexture) {
        this.lastTexture = lastTexture;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLastRed() {
        return lastRed;
    }

    public void setLastRed(float lastRed) {
        this.lastRed = lastRed;
    }

    public float getLastBlue() {
        return lastBlue;
    }

    public void setLastBlue(float lastBlue) {
        this.lastBlue = lastBlue;
    }

    public float getLastGreen() {
        return lastGreen;
    }

    public void setLastGreen(float lastGreen) {
        this.lastGreen = lastGreen;
    }

    public float getLastAlpha() {
        return lastAlpha;
    }

    public void setLastAlpha(float lastAlpha) {
        this.lastAlpha = lastAlpha;
    }
}
