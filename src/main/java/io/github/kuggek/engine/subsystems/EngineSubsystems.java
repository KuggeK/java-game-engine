package io.github.kuggek.engine.subsystems;

import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.ecs.GameObject;
import io.github.kuggek.engine.ecs.GameObjectManager;
import io.github.kuggek.engine.ecs.GameScene;
import io.github.kuggek.engine.physics.PhysicsBody;
import io.github.kuggek.engine.physics.PhysicsCollider;
import io.github.kuggek.engine.physics.PhysicsEngine;
import io.github.kuggek.engine.physics.PhysicsSettings;
import io.github.kuggek.engine.rendering.RenderingEngine;
import io.github.kuggek.engine.rendering.RenderingSettings;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;
import io.github.kuggek.engine.scripting.KeyInput;
import io.github.kuggek.engine.scripting.Script;
import io.github.kuggek.engine.scripting.ScriptingEngine;

/**
 * Handles the engine that are used to update the engine. Provides an interface
 * to handle subsystem settings.
 */
public class EngineSubsystems implements EngineRuntimeSettings, GameObjectManager {

    private RenderingEngine renderingEngine;
    private PhysicsEngine physicsEngine;
    private ScriptingEngine scriptingEngine;

    private GameScene scene;

    public EngineSubsystems(KeyInput keyInput) {
        renderingEngine = new RenderingEngine();
        physicsEngine = new PhysicsEngine();
        scriptingEngine = new ScriptingEngine(keyInput);
    }

    /**
     * Updates the engine returning the time it took to update in nanoseconds
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
    
    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }

    public ScriptingEngine getScriptingEngine() {
        return scriptingEngine;
    }

    @Override
    public RenderingSettings getRenderingSettings() {
        return renderingEngine;
    }

    @Override
    public PhysicsSettings getPhysicsSettings() {
        return physicsEngine;
    }

    @Override
    public GameSceneSettings getGameSceneSettings() {
        return scene;
    }
    
    public void setupScene(GameScene scene) {
        clearSubsystems();
        this.scene = scene;
        
        for (GameObject gameObject : scene.getGameObjects()) {
            gameObject.setManager(this);
            addGOComponents(gameObject, false);
        }

        for (GameObject gameObject : scene.getGameObjects()) {
            for (GameComponent component : gameObject.getComponents()) {
                GameComponent.awake(component, this);
            }
        }
    }

    private void addGOComponents(GameObject gameObject, boolean awake) {
        for (GameComponent component : gameObject.getComponents()) {
            createComponent(component, awake);
        }
    }

    @Override
    public void addGameObject(GameObject gameObject) {
        addGameObject(gameObject, true);
    }

    public void addGameObject(GameObject gameObject, boolean awake) {
        gameObject.setManager(this);
        scene.addGameObject(gameObject);

        addGOComponents(gameObject, awake);
    }

    @Override
    public void removeGameObject(GameObject gameObject) {
        scene.removeGameObject(gameObject);
    }

    @Override
    public void disposeComponent(GameComponent component) {
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
    
    @Override
    public GameComponent createComponent(GameComponent component) {
        return createComponent(component, true);
    }

    public GameComponent createComponent(GameComponent component, boolean awake) {
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
        if (awake) {
            GameComponent.awake(component, this);
        }
        return component;
    }

    public void clearSubsystems() {
        scriptingEngine.clear();
        physicsEngine.clear();
        renderingEngine.clear();
    }

    public GameScene getScene() {
        return scene;
    }
}
