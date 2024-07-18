package kugge.rendering.core.objects;

import java.util.List;

public class SkyBox {
    private Texture right;
    private Texture left;
    private Texture top;
    private Texture bottom;
    private Texture front;
    private Texture back;

    private Texture baseTexture;

    private int textureSize;

    private SkyBoxType type;

    // Skybox types
    public enum SkyBoxType {
        WRAPPED, // The whole skybox is in a single texture, in a cross shape
        SINGLE, // The skybox is a single texture for all sides
        MULTIPLE // The skybox is made of multiple separate textures
    }

    public static final float[] VERTICES = new float[] {
        // positions          
        -1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        -1.0f,  1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f
    };

    public SkyBox(Texture right, Texture left, Texture top, Texture bottom, Texture front, Texture back, SkyBoxType type) {
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
        this.type = type;
    }

    public SkyBox(Texture right, Texture left, Texture top, Texture bottom, Texture front, Texture back, SkyBoxType type, Texture baseTexture) {
        this(right, left, top, bottom, front, back, type);
        this.baseTexture = baseTexture;
    }

    public SkyBox(Texture texture) {
        if (texture.getWidth() != texture.getHeight()) {
            throw new IllegalArgumentException("Skybox textures must be square.");
        }

        this.baseTexture = texture;
        this.textureSize = texture.getWidth();
        this.type = SkyBoxType.SINGLE;
    }

    public SkyBox(Texture[] textures, SkyBoxType type) {
        this(textures[0], textures[1], textures[2], textures[3], textures[4], textures[5], type);
    }

    public SkyBox(List<Texture> textures, SkyBoxType type) {
        this(textures.get(0), textures.get(1), textures.get(2), textures.get(3), textures.get(4), textures.get(5), type);
    }
    
    public Texture getRight() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return right;
    }

    public void setRight(Texture right) {
        this.right = right;
    }

    public Texture getLeft() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return left;
    }

    public void setLeft(Texture left) {
        this.left = left;
    }

    public Texture getTop() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return top;
    }

    public void setTop(Texture top) {
        this.top = top;
    }

    public Texture getBottom() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return bottom;
    }

    public void setBottom(Texture bottom) {
        this.bottom = bottom;
    }

    public Texture getFront() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return front;
    }

    public void setFront(Texture front) {
        this.front = front;
    }

    public Texture getBack() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
        return back;
    }

    public void setBack(Texture back) {
        this.back = back;
    }

    public boolean isComplete() {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture != null;
        }
        return right != null && left != null && top != null && bottom != null && front != null && back != null;
    }

    public int getTextureSize() {
        return textureSize;
    }

    public Texture getTexture(int index) {
        if (type == SkyBoxType.SINGLE) {
            return baseTexture;
        }
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

    public SkyBoxType getType() {
        return type;
    }

    public Texture getBaseTexture() {
        if (type == SkyBoxType.MULTIPLE) {
            throw new IllegalStateException("Skybox is not a single texture.");
        }
        return baseTexture;
    }
    
    public static SkyBox unwrapSkyboxTexture(Texture texture) {
        int texWidth = texture.getWidth();
        int texHeight = texture.getHeight();
        int width = texWidth / 4;
        int height = texHeight / 3;

        // Downscale the bigger side to match the smaller side
        if (width != height) {
            if (width > height) {
                width = height;
            } else {
                height = width;
            }
        }

        int[] pixels = texture.getPixels();
        
        int[] right= new int[width * height];
        int[] left = new int[width * height];
        int[] top = new int[width * height];
        int[] bottom = new int[width * height];
        int[] front = new int[width * height];
        int[] back = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                right[x + y * width] = pixels[(height * texWidth) + y * texWidth + (2 * width) + x];
                left[x + y * width] = pixels[(height * texWidth) + y * texWidth + x];
                top[x + y * width] = pixels[y * texWidth + width + x];
                bottom[x + y * width] = pixels[(2 * height * texWidth) + y * texWidth + width + x];
                front[x + y * width] = pixels[(height * texWidth) + y * texWidth + width + x];
                back[x + y * width] = pixels[(height * texWidth) + y * texWidth + (3 * width) + x];
            }
        }
        return new SkyBox(
            new Texture(width, height, right),
            new Texture(width, height, left),
            new Texture(width, height, top),
            new Texture(width, height, bottom),
            new Texture(width, height, front),
            new Texture(width, height, back),
            SkyBoxType.WRAPPED,
            texture
        );
    }
}
