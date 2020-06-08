package club.sk1er.patcher.util.enhancement.text;

public class CachedLetter {


    private float width;
    private float textureX;
    private float textureY;

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
