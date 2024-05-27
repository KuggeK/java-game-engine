package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import kugge.rendering.graphics.Renderer;

public class OpenGLRenderer implements Renderer, GLEventListener {

    private GLAutoDrawable drawable;
    private Thread renderThread;
    private final Runnable renderRun = () -> {
        drawable.display();
    };

    public OpenGLRenderer() {
        renderThread = new Thread(renderRun);
    }

    /**
     * Sets the drawable to render to.
     * @param drawable The drawable to render to.
     */
    public void setDrawable(GLAutoDrawable drawable) {
        this.drawable = drawable;
        drawable.addGLEventListener(this);
    }

    /**
     * Renders the scene. If the previous render is still running, do nothing.
     */
    @Override
    public void render() {
        render(false);
    }

    /**
     * Renders the scene. If force is true and the previous render is still running, 
     * it will interrupt the previous render and start a new one. If force is false,
     * it will only render if the previous render is done.
     * @param force Whether to force the render or not.
     */
    public void render(boolean force) {
        if (renderThread.isAlive()) {
            if (force) {
                renderThread.interrupt();
            } else {
                return;
            }
        }
        renderThread = new Thread(renderRun);
        renderThread.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void display(GLAutoDrawable drawable) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        drawable.removeGLEventListener(this);
        if (renderThread.isAlive()) {
            renderThread.interrupt();
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }    
}
