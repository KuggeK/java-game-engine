package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GLAutoDrawable;

import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;

public class OpenGLRenderer implements Renderer {
    private Thread renderThread;
    private GLAutoDrawable drawable;
    private final Runnable renderRunnable = () -> {
        drawable.display();
    };

    private RenderScene scene;
    private OpenGLBindings bindings;

    public OpenGLRenderer(RenderScene scene) {
        this.scene = scene;
        bindings = new OpenGLBindings(scene);
    }

    @Override
    public void render() {
        render(false);
    }

    @Override
    public void render(boolean force) {
        if (force) {
            if (renderThread != null && renderThread.isAlive()) {
                renderThread.interrupt();
            }
        } else {
            if (renderThread != null && renderThread.isAlive()) {
                return;
            }
        }

        //scene.sortRenderInstances();

        renderThread = new Thread(renderRunnable);
        renderThread.start();
    }

    @Override
    public void setRenderScene(RenderScene scene) {
        this.scene = scene;
        bindings = new OpenGLBindings(scene);
    }

    @Override
    public void linkToWindow(Window window) {
        window.registerEventListener(bindings);
        drawable = ((OpenGLWindow) window).getCanvas();
    }
    

}
