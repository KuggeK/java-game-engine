package io.github.kuggek.engine;

import static java.awt.event.KeyEvent.VK_ESCAPE;

import java.io.IOException;

import io.github.kuggek.engine.core.config.EngineProjectConfiguration;
import io.github.kuggek.engine.core.config.ProjectPaths;
import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.ecs.GameObject;
import io.github.kuggek.engine.ecs.GameObjectManager;
import io.github.kuggek.engine.ecs.GameScene;
import io.github.kuggek.engine.rendering.Window;
import io.github.kuggek.engine.rendering.opengl.OpenGLWindow;
import io.github.kuggek.engine.scripting.ScriptLoader;
import io.github.kuggek.engine.subsystems.EngineSubsystems;

public class GameEngine implements GameObjectManager {
    private EngineSubsystems subsystems;
    private Window window;
    private GameScene currentScene;
    private EngineProjectConfiguration projectConfig;

    // Game loop timing variables
    private long currentTime;
    private long previousTime;
    private long deltaTime;
    private long timeTaken;
    private int targetFPS;
    private boolean running = false;

    private Thread gameLoopThread;

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
        engine.startGameLoop();
    }

    /**
     * Initialize the game engine with the given configuration. This will set up the window, rendering engine, physics engine, and scripting engine.
     * @param config The configuration for the engine.
     * @throws IOException If the initial scene cannot be loaded.
     */
    public void initialize(EngineProjectConfiguration config) throws IOException {
        window = new OpenGLWindow(config);
        subsystems = new EngineSubsystems(window.getKeyInput());

        subsystems.getRenderingEngine().linkToWindow(window);

        projectConfig = config;

        targetFPS = projectConfig.getTargetFPS();

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
     * Starts the game engine. This will start the game loop and run the game until the window is closed.
     * {@link #initialize(EngineProjectConfiguration)} must be called before this method.
     * @throws Exception
     */
    public void startGameLoop() throws Exception {
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            gameLoopThread.join();
        }

        gameLoopThread = new Thread(() -> {
            previousTime = System.currentTimeMillis();

            running = true;
            // Game loop
            while (running) {
                currentTime = System.currentTimeMillis();
                deltaTime = currentTime - previousTime;
                previousTime = currentTime;
    
                if (window.getKeyInput().isKeyPressed(VK_ESCAPE)) {
                    destroy();
                }

                // Update the engine subsystems. Time taken is in nanoseconds.
                timeTaken = subsystems.update(deltaTime / 1000.0f);
    
                // Sleep to maintain target FPS
                long sleepTime = (1000 / targetFPS) - (timeTaken / 1000000);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        gameLoopThread.start();
    }

    public EngineSubsystems getSubsystems() {
        return subsystems;
    }

    public Window getWindow() {
        return window;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Stops the game engine loop after the current frame is done. To 
     * actually close the game, call {@link GameEngine#destroy()}.
     */
    public void stopGameLoop() {
        running = false;
    }

    /**
     * Destroys the game engine and the window. This will stop the game loop and close the window.
     */
    public void destroy() {
        stopGameLoop();
        window.destroy();
    }

    @Override
    public GameComponent createComponent(GameComponent component) {
        return subsystems.addComponent(component);
    }

    @Override
    public void disposeComponent(GameComponent component) {
        subsystems.removeComponent(component);
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
