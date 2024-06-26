package kugge.rendering.core.objects;

import java.util.List;

public class SkyBox {
    private Texture right;
    private Texture left;
    private Texture top;
    private Texture bottom;
    private Texture front;
    private Texture back;

    private int textureSize;

    public SkyBox(Texture right, Texture left, Texture top, Texture bottom, Texture front, Texture back) {
        List<Texture> textures = List.of(right, left, top, bottom, front, back);
        // Textures must all be square
        if (!textures.stream().allMatch(texture -> texture.getWidth() == texture.getHeight())) {
            throw new IllegalArgumentException("Skybox textures must be square.");
        }
        // Textures must all be the same size
        this.textureSize = right.getWidth();
        if (!textures.stream().allMatch(texture -> texture.getWidth() == textureSize && texture.getHeight() == textureSize)) {
            throw new IllegalArgumentException("Skybox textures must all be the same size.");
        }

        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.front = front;
        this.back = back;
    }

    public SkyBox(Texture texture) {
        if (texture.getWidth() != texture.getHeight()) {
            throw new IllegalArgumentException("Skybox textures must be square.");
        }

        this.right = texture;
        this.left = texture;
        this.top = texture;
        this.bottom = texture;
        this.front = texture;
        this.back = texture;
    }

    public SkyBox(Texture[] textures) {
        this(textures[0], textures[1], textures[2], textures[3], textures[4], textures[5]);
    }
    
    public Texture getRight() {
        return right;
    }

    public void setRight(Texture right) {
        this.right = right;
    }

    public Texture getLeft() {
        return left;
    }

    public void setLeft(Texture left) {
        this.left = left;
    }

    public Texture getTop() {
        return top;
    }

    public void setTop(Texture top) {
        this.top = top;
    }

    public Texture getBottom() {
        return bottom;
    }

    public void setBottom(Texture bottom) {
        this.bottom = bottom;
    }

    public Texture getFront() {
        return front;
    }

    public void setFront(Texture front) {
        this.front = front;
    }

    public Texture getBack() {
        return back;
    }

    public void setBack(Texture back) {
        this.back = back;
    }

    public boolean isComplete() {
        return right != null && left != null && top != null && bottom != null && front != null && back != null;
    }

    public int getTextureSize() {
        return textureSize;
    }

    public Texture getTexture(int index) {
        switch (index) {
            case 0:
                return right;
            case 1:
                return left;
            case 2:
                return top;
            case 3:
                return bottom;
            case 4:
                return front;
            case 5:
                return back;
            default:
                throw new IllegalArgumentException("Invalid skybox texture index.");
        }
    }
}
