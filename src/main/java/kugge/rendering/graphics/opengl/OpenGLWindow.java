package kugge.rendering.graphics.opengl;

import javax.swing.JFrame;

import com.jogamp.opengl.awt.GLCanvas;

import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.WindowSettings;

public class OpenGLWindow extends JFrame implements Window {

    private WindowSettings settings;
    private static final WindowSettings DEFAULT_SETTINGS = new WindowSettings(800, 600, "Window");
    private GLCanvas canvas;

    public OpenGLWindow() {
        this(DEFAULT_SETTINGS);
    }

    public OpenGLWindow(WindowSettings settings) {
        super(settings.getTitle());
        this.settings = settings;
        updateSettings();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new GLCanvas();
        add(canvas);
        setVisible(true);
    }

    @Override
    public void setWindowSettings(WindowSettings settings) {
        this.settings = settings;
        updateSettings();
    }

    public GLCanvas getCanvas() {
        return canvas;
    }

    private void updateSettings() {
        this.setSize(settings.getWidth(), settings.getHeight());
        this.setTitle(settings.getTitle());
    }
}
