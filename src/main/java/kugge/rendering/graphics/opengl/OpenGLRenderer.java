package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GLAutoDrawable;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
public class OpenGLRenderer implements Renderer {

    private RenderScene scene;
    private GLAutoDrawable drawable;
    OpenGLBindings bindings;

    private Thread renderThread;
    private final Runnable renderRun = () -> {
        drawable.display();
    };

    public OpenGLRenderer(RenderScene scene) {
        this.scene = scene;
        bindings = new OpenGLBindings(scene.getMeshes(), scene.getMaterials());
        bindings.setTextures(scene.getTextures());
        renderThread = new Thread(renderRun); 
    }

    public void linkToWindow(Window window) {
        drawable = ((OpenGLWindow) window).getCanvas();
        drawable.addGLEventListener(bindings);
    }

    @Override
    public void render() {
        render(false);
    }

    @Override
    public void render(boolean force) {
        if (renderThread != null && renderThread.isAlive() && !force) {
            return;
        }

        // Set camera information
        bindings.setFov(scene.getCamera().getFov());
        bindings.setNear(scene.getCamera().getNear());
        bindings.setFar(scene.getCamera().getFar());
        bindings.setViewMatrix(scene.getCamera().getViewMatrix());

        // Set instance information
        bindings.setInstances(scene.getInstances());

        // Set light information
        bindings.setGlobalAmbient(scene.getGlobalAmbient());
        bindings.setDirectionalLight(scene.getDirectionalLight());
        bindings.setPositionalLights(scene.getPositionalLights());

        if (renderThread != null && renderThread.isAlive()) {
            renderThread.interrupt();
        }
        renderThread = new Thread(renderRun);
        renderThread.start();
    }

    @Override
    public void setRenderScene(RenderScene scene) {
        this.scene = scene;
    }
}
