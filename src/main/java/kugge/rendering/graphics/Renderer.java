package kugge.rendering.graphics;

/**
 * Represents a real-time renderer that can render a scene with a given camera and objects.
 */
public interface Renderer {

    /**
     * Renders the current scene. If the previous render is still running, do nothing.
     */
    public void render();

    /**
     * Renders the current scene. If force is true and the previous render is still running, 
     * it will interrupt the previous render and start a new one. If force is false and the
     * previous render is still running, do nothing.
     * @param force Whether to force the render or not.
     */
    public void render(boolean force);
}