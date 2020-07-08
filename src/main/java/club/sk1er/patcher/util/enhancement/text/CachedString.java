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

public final class CachedString {

    private final String text;
    private final int listId;

    private final float height;
    private float width;

    private float lastRed;
    private float lastBlue;
    private float lastGreen;
    private float lastAlpha;

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

    public float getLastAlpha() {
        return lastAlpha;
    }

    public void setLastAlpha(float lastAlpha) {
        this.lastAlpha = lastAlpha;
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float v) {
        this.width = v;
    }

    public float getHeight() {
        return height;
    }


}