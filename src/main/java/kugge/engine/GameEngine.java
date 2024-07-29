package kugge.engine;

import static java.awt.event.KeyEvent.VK_ESCAPE;

import java.io.IOException;

import kugge.engine.rendering.RenderingEngine;
import kugge.engine.rendering.Window;
import kugge.engine.rendering.objects.Camera;
import kugge.engine.rendering.objects.RenderInstance;
import kugge.engine.rendering.objects.lights.DirectionalLight;
import kugge.engine.core.config.EngineProjectConfiguration;
import kugge.engine.ecs.GameScene;
import kugge.engine.physics.PhysicsBody;
import kugge.engine.physics.PhysicsCollider;
import kugge.engine.physics.PhysicsEngine;
import kugge.engine.rendering.opengl.OpenGLWindow;
import kugge.engine.scripting.Script;
import kugge.engine.scripting.ScriptingEngine;

public class GameEngine {

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

        GameEngine engine = new GameEngine();
        String projectPath = args[0];
        EngineProjectConfiguration config = EngineProjectConfiguration.loadProjectConfiguration(projectPath);
        engine.initialize(config);
        engine.start();
    }

    public void initialize(EngineProjectConfiguration config) throws IOException {
        window = new OpenGLWindow(config);
        
        renderingEngine = new RenderingEngine();
        renderingEngine.linkToWindow(window);

        physicsEngine = new PhysicsEngine();

        // Link the window's key input to the scripting engine to allow user input to be used in scripts
        scriptingEngine = new ScriptingEngine(renderingEngine.getWindow().getKeyInput());

        // Add the subsystems to the scene
        projectConfig = config;

        currentScene = GameScene.loadScene(projectConfig.getInitialSceneID());

        // Populate subsystems with the scene components
        System.out.println(currentScene.getGameObjects().size());
        currentScene.getGameObjects().forEach(gameObject -> {
            for (var component : gameObject.getComponents()) {
                if (component instanceof Script) {
                    scriptingEngine.addScript((Script) component);
                    continue;
                }
                if (component instanceof PhysicsBody) {
                    physicsEngine.addBody((PhysicsBody) component);
                    continue;
                }
                if (component instanceof PhysicsCollider) {
                    physicsEngine.addCollider((PhysicsCollider) component);
                    continue;
                }
                if (component instanceof RenderInstance) {
                    System.out.println("Adding render instance");
                    renderingEngine.addInstance((RenderInstance) component);
                    continue;
                }
                if (component instanceof Camera) {
                    renderingEngine.getScene().setCamera((Camera) component);
                    continue;
                } 
                if (component instanceof DirectionalLight) {
                    renderingEngine.getScene().setDirectionalLight((DirectionalLight) component);
                    continue;
                }
            }
        });
    }

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
}
