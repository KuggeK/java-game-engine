package kugge.rendering.graphics.opengl;

import javax.swing.JFrame;

import com.jogamp.opengl.awt.GLCanvas;

import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.WindowSettings;

public class OpenGLWindow extends JFrame implements Window {

    private OpenGLRenderer renderer;
    private WindowSettings settings;
    private static final WindowSettings DEFAULT_SETTINGS = new WindowSettings(800, 600, "Window");

    public OpenGLWindow() {
        this(DEFAULT_SETTINGS);
    }

    public OpenGLWindow(WindowSettings settings) {
        super(settings.getTitle());
        this.settings = settings;
        updateSettings();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void create(Renderer renderer, WindowSettings settings) {
        this.settings = settings;
        updateSettings();
        create(renderer);
    }

    @Override
    public void create(Renderer renderer) {
        if (renderer instanceof OpenGLRenderer) {
            this.renderer = (OpenGLRenderer) renderer;
            GLCanvas canvas = new GLCanvas();
            this.renderer.setDrawable(canvas);
            add(canvas);
            setVisible(true);
        } else {
            throw new IllegalArgumentException("Renderer must be of type OpenGLRenderer");
        }
    }

    @Override
    public void setWindowSettings(WindowSettings settings) {
        this.settings = settings;
        updateSettings();
    }

    private void updateSettings() {
        this.setSize(settings.getWidth(), settings.getHeight());
        this.setTitle(settings.getTitle());
    }
}
