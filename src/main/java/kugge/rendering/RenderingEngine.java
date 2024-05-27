package kugge.rendering;

import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine {
    Renderer renderer;
    Window window;

    public static void main(String[] args) {
        RenderingEngine engine = new RenderingEngine();
        String type = args.length > 0 ? args[0] : "OPENGL";
        engine.start(RendererType.valueOf(type));
    }

    enum RendererType {
        OPENGL
    }

    public void start(RendererType type) {
        switch (type) {
            case OPENGL:
                renderer = new OpenGLRenderer();
                window = new OpenGLWindow();
                break;
        }
        window.create(renderer);
    }
}
