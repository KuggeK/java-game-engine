package io.github.kuggek.engine;

import java.io.IOException;

import io.github.kuggek.engine.core.assets.DefaultAssets;
import io.github.kuggek.engine.core.assets.SQLiteAssetManager;
import io.github.kuggek.engine.core.config.EngineProjectConfiguration;
import io.github.kuggek.engine.core.config.ProjectPaths;
import io.github.kuggek.engine.ecs.GameScene;
import io.github.kuggek.engine.rendering.Window;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.Texture;
import io.github.kuggek.engine.rendering.opengl.OpenGLWindow;
import io.github.kuggek.engine.scripting.ScriptLoader;
import io.github.kuggek.engine.subsystems.EngineSubsystems;

public class GameEngine {
    private EngineSubsystems subsystems;
    private Window window;
    private EngineProjectConfiguration projectConfig;

    // Game loop timing variables
    private long currentTime;
    private long previousTime;
    private long deltaTime;
    private long timeTaken;
    private boolean running = false;

    private Thread gameLoopThread;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("No project path provided.");
        }

        EngineProjectConfiguration config = EngineProjectConfiguration.loadProjectConfiguration(args[0]);
        System.out.println(config.getProjectAbsolutePath());
        ScriptLoader.compileAndPackageScripts("scripts.jar", ProjectPaths.SCRIPTS_PATH);
        ScriptLoader.addJarToClasspath("scripts.jar");

        DefaultAssets.loadDefaultAssets(SQLiteAssetManager.getInstance());

        GameEngine engine = new GameEngine();
        engine.initialize(config);
        engine.startGameLoop();

        engine.getSubsystems().getRenderingEngine().setSkyBox(SkyBox.unwrapSkyboxTexture(Texture.loadTexture("assets/textures/skybox2.png")));
    }

    public void initialize(EngineProjectConfiguration config) throws IOException {
        initialize(config, new OpenGLWindow(config));
    }

    public void initialize(EngineProjectConfiguration config, GameScene scene) {
        initialize(config, new OpenGLWindow(config), scene);
    }

    /**
     * Initialize the game engine with the given configuration. This will set up the window, rendering engine, physics engine, and scripting engine.
     * @param config The configuration for the engine.
     * @throws IOException If the initial scene cannot be loaded.
     */
    public void initialize(EngineProjectConfiguration config, Window suppliedWindow) throws IOException {
        initialize(config, suppliedWindow, GameScene.loadScene(config.getInitialSceneName()));
    }

    public void initialize(EngineProjectConfiguration config, Window suppliedWindow, GameScene scene) {
        window = suppliedWindow;
        window.setWindowSettings(config);

        subsystems = new EngineSubsystems(window.getKeyInput());

        subsystems.getRenderingEngine().linkToWindow(window);
        subsystems.getRenderingEngine().render();

        projectConfig = config;

        setupScene(scene);
    }

    /**
     * Starts the game engine. This will start the game loop and run the game until the window is closed.
     * {@link #initialize(EngineProjectConfiguration)} must be called before this method.
     */
    public void startGameLoop() {
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            try {
                gameLoopThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameLoopThread = new Thread(() -> {
            previousTime = System.currentTimeMillis();

            running = true;
            // Game loop
            while (running) {
                currentTime = System.currentTimeMillis();
                deltaTime = currentTime - previousTime;
                previousTime = currentTime;

                // Update the engine subsystems. Time taken is in nanoseconds.
                timeTaken = subsystems.update(deltaTime / 1000.0f);

                window.getKeyInput().clear();
    
                // Sleep to maintain target FPS
                long sleepTime = (1000 / projectConfig.getTargetFPS()) - (timeTaken / 1000000);
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

    public void setupScene(GameScene newScene) {
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            stopGameLoop();
            try {
                gameLoopThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        subsystems.setupScene(newScene);
    }

    public GameScene getCurrentScene() {
        return subsystems.getScene();
    }

}
