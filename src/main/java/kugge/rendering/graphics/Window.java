package kugge.rendering.graphics;

/**
 * Represents a window that can be created and displays a renderer.
 */
public interface Window {
    
    /**
     * Creates and shows a window with the given settings to display the given renderer.
     * @param renderer
     * @param settings
     */
    public void create(Renderer renderer, WindowSettings settings);

    /**
     * Creates and shows a window to display the given renderer.
     * @param renderer
     */
    public void create(Renderer renderer);

    /**
     * Sets and updates the window settings to the given settings.
     * @param settings
     */
    public void setWindowSettings(WindowSettings settings);
}
