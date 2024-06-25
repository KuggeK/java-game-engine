package kugge.rendering.graphics;

/**
 * Contains the basic settings for a window.
 */
public class WindowSettings {

    private int width;
    private int height;
    private String title;
    private boolean fullscreen;
    private boolean resizable;
    private int targetFPS;

    public WindowSettings(int width, int height, String title, boolean fullscreen, boolean resizable, int targetFPS) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.fullscreen = fullscreen;
        this.resizable = resizable;
        this.targetFPS = targetFPS;
    }

    public WindowSettings(int width, int height, String title) {
        this(width, height, title, false, true, 60);
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

    public boolean isFullscreen() {
        return fullscreen;
    }

    public boolean isResizable() {
        return resizable;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public void setTargetFPS(int targetFPS) {
        this.targetFPS = targetFPS;
    }
}
