package kugge.rendering.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.GameComponent;
import kugge.rendering.core.objects.SkyBox;
import kugge.rendering.core.objects.Subsystem;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.rendering.RenderInstance;
import kugge.rendering.core.objects.rendering.RenderSceneImpl;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine implements Subsystem {

    private final Map<Class<? extends GameComponent>, Consumer<GameComponent>> componentDestroyListeners = new HashMap<>();
    private final Map<Class<? extends GameComponent>, Consumer<GameComponent>> componentInitListeners = new HashMap<>();

    private OpenGLWindow window;
    private OpenGLRenderer renderer;
    private RenderSceneImpl scene;

    public RenderingEngine() {
        // Add component destroy and init listeners
        componentInitListeners.put(RenderInstance.class, this::addInstance);
        componentDestroyListeners.put(RenderInstance.class, this::removeInstance);

        // TODO Don't just change the camera automatically, allow for multiple 
        // cameras and a way to switch between them.
        componentInitListeners.put(Camera.class, (component) -> {
            Camera camera = (Camera) component;
            scene.setCamera(camera);
        });

        // TODO Add listeners for lights and other rendering components

        scene = new RenderSceneImpl();

        SkyBox skyBox = null;
        try {
            Texture skyboxTex = Texture.loadTexture("skybox2.png");
            skyBox = SkyBox.unwrapSkyboxTexture(skyboxTex);
        } catch (Exception e) {
            // TODO: handle exception
        }
        scene.setSkyBox(skyBox);
        
        window = new OpenGLWindow();
        renderer = new OpenGLRenderer(scene);
        renderer.linkToWindow(window);
    }


    @Override
    public Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentDestroyListeners() {
        return componentDestroyListeners;
    }

    @Override
    public Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentInitListeners() {
        return componentInitListeners;
    }

    @Override
    public void update(float dt) {
        renderer.render();
    }

    public void addInstance(GameComponent instance) {
        RenderInstance renderInstance = (RenderInstance) instance;    
        scene.addRenderInstance(renderInstance);
    }
    
    public void removeInstance(GameComponent instance) {
        RenderInstance renderInstance = (RenderInstance) instance;
        scene.removeRenderInstance(renderInstance);
    }

    public Window getWindow() {
        return window;
    }

}
