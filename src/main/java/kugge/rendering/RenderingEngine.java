package kugge.rendering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.Cube;
import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.Mesh;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.core.objects.Sphere;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.materials.Materials;
import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine {
    Renderer renderer;
    Window window;
    RenderScene scene;
    KeyInput keyInput = new KeyInput();
    Thread inputThread;
    Camera camera;

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
            Sphere.withId(1),
            new Cube(2)
        );
        List<Instance> instances = new ArrayList<>();

        try {
            meshes.get(0).setTextureParameters(Map.of(
                GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT,
                GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT,
                GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR,
                GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR
            ));
            meshes.get(0).addTexture(Texture.loadTexture("/textures/brick.jpg"));

            meshes.get(1).setTextureParameters(Map.of(
                GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE,
                GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE,
                GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST,
                GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR
            ));
            meshes.get(1).addTexture(Texture.loadTexture("/textures/grass.jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = -5; i < 5; i++) {
            Instance instance1 = new Instance(1, new float[] {i, 0, -5}, new float[] {0, 0, 0}, new float[] {0.3f, 0.3f, 0.3f}, Materials.RED);
            instance1.setTextureIndex(0);

            // Set texture index to -1 to disable texture
            if (i == 1) {
                instance1.setTextureIndex(-1);
                instance1.setMaterial(Materials.EMERALD);
            }

            instances.add(instance1);
            Instance instance2 = new Instance(2, new float[] {0, i, -5}, new float[] {0, 0, 0}, new float[] {0.3f, .3f, 0.3f}, Materials.BRONZE);
            instance2.setTextureIndex(0);
            instances.add(instance2);
        }

        camera = new Camera();
        scene = new RenderScene(camera, meshes, instances);

        scene.setDirectionalLight(new DirectionalLight(
            new float[] {0.1f, 0.1f, 0.1f, 1.0f}, // Ambient
            new float[] {0.5f, 0.5f, 0.5f, 1.0f}, // Diffuse
            new float[] {1.0f, 1.0f, 1.0f, 1.0f}, // Specular
            new float[] {0.5f, -1.0f, -1.0f, 0.0f} // Direction
            ));

        switch (type) {
            case OPENGL:
                renderer = new OpenGLRenderer(scene);
                window = new OpenGLWindow();
                break;        
        }
        keyInput = new KeyInput();
        window.registerEventListener(new DebugKeyInput(camera, renderer));
        
        renderer.linkToWindow(window);
        renderer.render();
    }
}
