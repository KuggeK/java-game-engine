package kugge.rendering;

import java.util.ArrayList;
import java.util.List;

import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.Cube;
import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.Mesh;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine {
    Renderer renderer;
    Window window;
    RenderScene scene;

    public static void main(String[] args) {
        RenderingEngine engine = new RenderingEngine();
        String type = args.length > 0 ? args[0] : "OPENGL";
        engine.start(RendererType.valueOf(type));
    }

    enum RendererType {
        OPENGL
    }

    public void start(RendererType type) {
        List<Mesh> meshes = List.of(
            Cube.withSize(1, 0.1f),
            Cube.withSize(2, 0.3f)
        );
        List<Instance> instances = new ArrayList<>();

        for (int i = -5; i < 5; i++) {
            instances.add(new Instance(1, new float[] {i, 0, 0}, new float[] {0, 0, 0}, new float[] {1, 1, 1}, null));
            instances.add(new Instance(2, new float[] {0, i, 0}, new float[] {0, 0, 0}, new float[] {1, 1, 1}, null));
        }

        scene = new RenderScene(new Camera(), meshes, instances);

        switch (type) {
            case OPENGL:
                renderer = new OpenGLRenderer(scene);
                window = new OpenGLWindow();
                break;        
        }

        renderer.linkToWindow(window);
        renderer.render();
    }
}
