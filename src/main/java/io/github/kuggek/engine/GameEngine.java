package io.github.kuggek.engine;

import static java.awt.event.KeyEvent.*;

import java.io.IOException;

import io.github.kuggek.engine.core.config.EngineProjectConfiguration;
import io.github.kuggek.engine.core.config.ProjectPaths;
import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.ecs.GameObject;
import io.github.kuggek.engine.ecs.GameObjectManager;
import io.github.kuggek.engine.ecs.GameScene;
import io.github.kuggek.engine.physics.PhysicsBody;
import io.github.kuggek.engine.physics.PhysicsCollider;
import io.github.kuggek.engine.physics.PhysicsEngine;
import io.github.kuggek.engine.rendering.RenderingEngine;
import io.github.kuggek.engine.rendering.Window;
import io.github.kuggek.engine.rendering.objects.Camera;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;
import io.github.kuggek.engine.rendering.opengl.OpenGLWindow;
import io.github.kuggek.engine.scripting.Script;
import io.github.kuggek.engine.scripting.ScriptLoader;
import io.github.kuggek.engine.scripting.ScriptingEngine;

public class GameEngine implements GameObjectManager {
    private Window window;
    private RenderingEngine renderingEngine;
    private PhysicsEngine physicsEngine;
    private ScriptingEngine scriptingEngine;
    private GameScene currentScene;
    private EngineProjectConfiguration projectConfig;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("No project path provided.");
        }

        ScriptLoader.compileAndPackageScripts("scripts.jar", ProjectPaths.getScriptsPath());
        ScriptLoader.addJarToClasspath("scripts.jar");

        GameEngine engine = new GameEngine();
        String projectPath = args[0];
        EngineProjectConfiguration config = EngineProjectConfiguration.loadProjectConfiguration(projectPath);
        engine.initialize(config);
        engine.start();
    }

    /**
     * Initialize the game engine with the given configuration. This will set up the window, rendering engine, physics engine, and scripting engine.
     * @param config The configuration for the engine.
     * @throws IOException If the initial scene cannot be loaded.
     */
    public void initialize(EngineProjectConfiguration config) throws IOException {
        window = new OpenGLWindow(config);
        
        renderingEngine = new RenderingEngine();
        renderingEngine.linkToWindow(window);

        physicsEngine = new PhysicsEngine();

        // Link the window's key input to the scripting engine to allow user input to be used in scripts
        scriptingEngine = new ScriptingEngine(renderingEngine.getWindow().getKeyInput());

        // Add the subsystems to the scene
        projectConfig = config;

        currentScene = GameScene.loadScene(projectConfig.getInitialSceneName());

        // Populate subsystems with the scene components
        System.out.println(currentScene.getGameObjects().size());
        currentScene.getGameObjects().forEach(gameObject -> {
            gameObject.setManager(this);
            for (GameComponent component : gameObject.getComponents()) {
                createComponent(component);
            }
        });
    }

    /**
     * Start the game engine. This will start the game loop and run the game until the window is closed.
     * @throws Exception
     */
    public void start() throws Exception {
        // Timing variables
        long currentTime = 0;
        long previousTime = System.currentTimeMillis();
        long deltaTime = 0;
        long timeTaken = 0;
        int targetFPS = projectConfig.getTargetFPS();


        // Game loop
        while (true) {
            currentTime = System.currentTimeMillis();
            deltaTime = currentTime - previousTime;
            previousTime = currentTime;

            renderingEngine.render();
            
            physicsEngine.updateSimulation(1 / 60.0);
            
            scriptingEngine.updateScripts(deltaTime / 1000.0f);

            if (window.getKeyInput().isKeyPressed(VK_ESCAPE)) {
                System.exit(0);
                break;
            }

            window.getKeyInput().clear();



            // If the time taken by this frame is less than the target time, sleep for the difference
            timeTaken = System.currentTimeMillis() - currentTime;
            if (timeTaken < 1000 / targetFPS) {
                try {
                    if (targetFPS - timeTaken != 0) {
                        Thread.sleep(1000 / targetFPS - timeTaken);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Frame time did not match target FPS for this frame (" + targetFPS + "). Total time taken was " + timeTaken);
            }
        }
    }

    @Override
    public GameComponent createComponent(GameComponent component) {
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
        else if (component instanceof Camera) {
            renderingEngine.getScene().setCamera((Camera) component);
        }
        else if (component instanceof DirectionalLight) {
            renderingEngine.getScene().setDirectionalLight((DirectionalLight) component);
        }
        else if (component instanceof PositionalLight) {
            renderingEngine.getScene().addPositionalLight((PositionalLight) component);
        }
        return component;
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
        else if (component instanceof Camera) {
            if (renderingEngine.getScene().getCamera() == component) {
                renderingEngine.getScene().setCamera(null);
            }
        }
        else if (component instanceof DirectionalLight) {
            if (renderingEngine.getScene().getDirectionalLight() == component) {
                renderingEngine.getScene().setDirectionalLight(null);
            }
        }
        else if (component instanceof PositionalLight) {
            renderingEngine.getScene().removePositionalLight((PositionalLight) component);
        }
    }

    @Override
    public void addGameObject(GameObject gameObject) {
        gameObject.setManager(this);
        currentScene.addGameObject(gameObject);

        for (GameComponent component : gameObject.getComponents()) {
            createComponent(component);
        }
    }

    @Override
    public void removeGameObject(GameObject gameObject) {
        currentScene.removeGameObject(gameObject);
    }
}
