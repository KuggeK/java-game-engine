package io.github.kuggek.engine.rendering.objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
    public static final int NO_ID = -1;
    
    private int ID;
    private String fileName;
    private String name;

    private int width;
    private int height;
    private int[] pixels;

    public Texture(int ID, String fileName, int width, int height, int[] pixels) {
        this.ID = ID;
        this.fileName = fileName;
        this.name = "Texture" + ID;
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public Texture(int width, int height, int[] pixels) {
        this(-1, null, width, height, pixels);
    }

    public int getID() {
        return ID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Loads a texture from the given file name.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Texture loadTexture(String fileName) throws IOException {
        return loadTexture(new File(fileName));
    }

    /**
     * Loads a texture from the given file.
     * @param file
     * @return
     * @throws IOException
     */
    public static Texture loadTexture(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        Texture texture = new Texture(width, height, pixels);
        texture.setFileName(file.getName());
        return texture;
    }
}
