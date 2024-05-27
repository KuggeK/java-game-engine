package kugge.rendering;

import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.WindowSettings;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine {
    
    Renderer renderer;
    Window window;

    public static void main(String[] args) {
        RenderingEngine engine = new RenderingEngine();
        engine.start();
    }

    public void start() {
        WindowSettings settings = new WindowSettings(800, 600, "Rendering Engine");
        window = new OpenGLWindow(settings);
        renderer = new OpenGLRenderer();
        window.create(renderer);
    }
}
