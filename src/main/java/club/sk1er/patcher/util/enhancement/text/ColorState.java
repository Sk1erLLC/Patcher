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

public class ColorState {
    private float lastAlpha, lastGreen, lastBlue, lastRed;

    public float getLastAlpha() {
        return lastAlpha;
    }

    public void setLastAlpha(float lastAlpha) {
        this.lastAlpha = lastAlpha;
    }

    public float getLastGreen() {
        return lastGreen;
    }

    public void setLastGreen(float lastGreen) {
        this.lastGreen = lastGreen;
    }

    public float getLastBlue() {
        return lastBlue;
    }

    public void setLastBlue(float lastBlue) {
        this.lastBlue = lastBlue;
    }

    public float getLastRed() {
        return lastRed;
    }

    public void setLastRed(float lastRed) {
        this.lastRed = lastRed;
    }
}
