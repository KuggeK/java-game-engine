package io.github.kuggek.engine.rendering;

import org.joml.Vector4f;

import io.github.kuggek.engine.rendering.objects.Camera;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.rendering.opengl.OpenGLBindings;
import io.github.kuggek.engine.rendering.opengl.shaders.SkyBoxShaderProgram;

public class RenderingEngine implements Renderer, RenderingSettings {
    private Window window;
    private RenderSceneImpl scene;

    private OpenGLBindings bindings;

    private SkyBoxShaderProgram skyBoxShader;
    
    private Thread renderThread;
    private final Runnable renderRunnable = () -> {
        window.display();
    };

    public RenderingEngine() {
        scene = new RenderSceneImpl();
        bindings = new OpenGLBindings(scene);
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

    public RenderSceneImpl getScene() {
        return scene;
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
        this.window = window;
        window.registerEventListener(bindings);
    }

    public void clear() {
        SkyBox skyBox = scene.getSkyBox();
        scene = new RenderSceneImpl();
        scene.setSkyBox(skyBox);

        bindings.getLocations().reset();
        bindings.setScene(scene);
    }

    public void setSkyBox(SkyBox skyBox) {
        if (skyBoxShader == null) {
            skyBoxShader = new SkyBoxShaderProgram(skyBox);
            bindings.addShaderProgram(skyBoxShader);
        } else {
            skyBoxShader.setSkybox(skyBox);
        }
        scene.setSkyBox(skyBox);
    }

    @Override
    public Camera getActiveCamera() {
        return scene.getCamera();
    }

    @Override
    public DirectionalLight getDirectionalLight() {
        return scene.getDirectionalLight();
    }

    @Override
    public void setActiveCamera(Camera camera) {
        scene.setCamera(camera);
    }

    @Override
    public void setDirectionalLight(DirectionalLight light) {
        scene.setDirectionalLight(light);
    }

    @Override
    public void setGlobalAmbient(float r, float g, float b, float a) {
        scene.setGlobalAmbient(new Vector4f(r, g, b, a));
    }

    @Override
    public void setGlobalAmbient(Vector4f color) {
        scene.setGlobalAmbient(color);
    }
}
