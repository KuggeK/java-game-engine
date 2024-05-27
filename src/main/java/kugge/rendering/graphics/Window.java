package kugge.rendering.graphics;

public interface Window {
    
    public void create(Renderer renderer, WindowSettings settings);

    public void create(Renderer renderer);

    public void setWindowSettings(WindowSettings settings);
}
