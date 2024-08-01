package io.github.kuggek.engine.rendering;

import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.Texture;
import io.github.kuggek.engine.rendering.opengl.OpenGLBindings;
import io.github.kuggek.engine.rendering.opengl.OpenGLWindow;

public class RenderingEngine implements Renderer {
    private OpenGLWindow window;
    private RenderSceneImpl scene;
    public RenderSceneImpl getScene() {
        return scene;
    }


    private OpenGLBindings bindings;
    
    private Thread renderThread;
    private final Runnable renderRunnable = () -> {
        window.getCanvas().display();
    };

    public RenderingEngine() {
        scene = new RenderSceneImpl();
        bindings = new OpenGLBindings(scene);

        SkyBox skyBox = null;
        try {
            Texture skyboxTex = Texture.loadTexture("skybox2.png");
            skyBox = SkyBox.unwrapSkyboxTexture(skyboxTex);
        } catch (Exception e) {
            // TODO: handle exception
        }
        scene.setSkyBox(skyBox);
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
