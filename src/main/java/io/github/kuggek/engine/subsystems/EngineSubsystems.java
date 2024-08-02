package io.github.kuggek.engine.subsystems;

import org.joml.Vector3f;
import org.joml.Vector4f;

import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.physics.PhysicsBody;
import io.github.kuggek.engine.physics.PhysicsCollider;
import io.github.kuggek.engine.physics.PhysicsEngine;
import io.github.kuggek.engine.rendering.RenderingEngine;
import io.github.kuggek.engine.rendering.objects.Camera;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;
import io.github.kuggek.engine.scripting.KeyInput;
import io.github.kuggek.engine.scripting.Script;
import io.github.kuggek.engine.scripting.ScriptingEngine;

/**
 * Handles the engine subsystems that are used to update the engine. Provides an interface
 * to handle subsystem settings.
 */
public class EngineSubsystems implements SubsystemSettings {

    private RenderingEngine renderingEngine;
    private PhysicsEngine physicsEngine;
    private ScriptingEngine scriptingEngine;

    public EngineSubsystems(KeyInput keyInput) {
        renderingEngine = new RenderingEngine();
        physicsEngine = new PhysicsEngine();
        scriptingEngine = new ScriptingEngine(keyInput);
    }

    /**
     * Updates the engine subsystems returning the time it took to update in nanoseconds
     * @param dt the time since the last update
     * @return the time it took to update in nanoseconds
     */
    public long update(float dt) {
        long start = System.nanoTime();
        renderingEngine.render(false);
        physicsEngine.updateSimulation(1 / 60.0);
        scriptingEngine.updateScripts(dt, this);
        return System.nanoTime() - start;
    }

    @Override
    public void setGravity(float x, float y, float z) {
        physicsEngine.setGravity(x, y, z);
    }

    @Override
    public void setGravity(float[] gravity) {
        physicsEngine.setGravity(gravity[0], gravity[1], gravity[2]);
    }

    @Override
    public void setGravity(Vector3f gravity) {
        physicsEngine.setGravity(gravity);
    }

    @Override
    public void setActiveCamera(Camera camera) {
        renderingEngine.getScene().setCamera(camera);
    }

    @Override
    public Camera getActiveCamera() {
        return renderingEngine.getScene().getCamera();
    }

    @Override
    public void setGlobalAmbient(Vector4f color) {
        renderingEngine.getScene().setGlobalAmbient(color);
    }

    @Override
    public void setGlobalAmbient(float r, float g, float b, float a) {
        renderingEngine.getScene().setGlobalAmbient(new Vector4f(r, g, b, a));
    }

    @Override
    public void setSkyBox(SkyBox skyBox) {
        renderingEngine.getScene().setSkyBox(skyBox);
    }

    @Override
    public void setDirectionalLight(DirectionalLight light) {
        renderingEngine.getScene().setDirectionalLight(light);
    }

    @Override
    public DirectionalLight getDirectionalLight() {
        return renderingEngine.getScene().getDirectionalLight();
    }
    
    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }

    public ScriptingEngine getScriptingEngine() {
        return scriptingEngine;
    }

    public void removeComponent(GameComponent component) {
        if (component instanceof Script) {
            scriptingEngine.setForRemoval((Script) component);
        }
        else if (component instanceof PhysicsBody) {
            physicsEngine.removeBody((PhysicsBody) component);
        }
        else if (component instanceof PhysicsCollider) {
            physicsEngine.removeCollider((PhysicsCollider) component);
        }
        else if (component instanceof RenderInstance) {
            renderingEngine.removeInstance((RenderInstance) component);
        }
        else if (component instanceof PositionalLight) {
            renderingEngine.getScene().removePositionalLight((PositionalLight) component);
        }
        GameComponent.dispose(component, this);
    }

    public GameComponent addComponent(GameComponent component) {
        if (component instanceof Script) {
            scriptingEngine.setToBeAdded((Script) component);
        }
        else if (component instanceof PhysicsBody) {
            physicsEngine.addBody((PhysicsBody) component);
        }
        else if (component instanceof PhysicsCollider) {
            physicsEngine.addCollider((PhysicsCollider) component);
        }
        else if (component instanceof RenderInstance) {
            renderingEngine.addInstance((RenderInstance) component);
        }
        else if (component instanceof PositionalLight) {
            renderingEngine.getScene().addPositionalLight((PositionalLight) component);
        }
        GameComponent.awake(component, this);
        return component;
    }
}
