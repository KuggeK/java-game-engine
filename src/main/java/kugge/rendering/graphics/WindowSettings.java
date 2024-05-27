package kugge.rendering.graphics;

public class WindowSettings {

    private int width;
    private int height;
    private String title;

    public WindowSettings(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }
}
