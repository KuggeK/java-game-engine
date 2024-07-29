package kugge.engine.rendering;

import kugge.engine.rendering.objects.RenderInstance;
import kugge.engine.rendering.objects.SkyBox;
import kugge.engine.rendering.objects.Texture;
import kugge.engine.rendering.opengl.OpenGLBindings;
import kugge.engine.rendering.opengl.OpenGLWindow;

public class RenderingEngine implements Renderer {
    private OpenGLWindow window;
    private RenderSceneImpl scene;
    private OpenGLBindings bindings;
    
    private Thread renderThread;
    private final Runnable renderRunnable = () -> {
        window.getCanvas().display();
    };

    public RenderingEngine() {
        scene = new RenderSceneImpl();

        SkyBox skyBox = null;
        try {
            Texture skyboxTex = Texture.loadTexture("skybox2.png");
            skyBox = SkyBox.unwrapSkyboxTexture(skyboxTex);
        } catch (Exception e) {
            // TODO: handle exception
        }
        scene.setSkyBox(skyBox);
        
        linkToWindow(window);
    }

    public void addInstance(RenderInstance instance) {
        scene.addRenderInstance(instance);
    }
    
    public void removeInstance(RenderInstance instance) {
        scene.removeRenderInstance(instance);
    }

    public Window getWindow() {
        return window;
    }


    @Override
    public void render() {
        render(false);
    }


    @Override
    public void render(boolean force) {
        if (renderThread != null && renderThread.isAlive()) {
            if (force) {
                renderThread.interrupt();
            } else {
                return;
            }
        }

        renderThread = new Thread(renderRunnable);
        renderThread.start();
    }


    @Override
    public void setRenderScene(RenderScene scene) {
        this.scene = (RenderSceneImpl) scene;
        bindings = new OpenGLBindings(this.scene);
    }


    @Override
    public void linkToWindow(Window window) {
        this.window = (OpenGLWindow) window;
        window.registerEventListener(bindings);
    }

}
