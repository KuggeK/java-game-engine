package kugge.rendering;

import java.awt.event.KeyEvent;
import java.util.Set;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.SceneStorage;
import kugge.rendering.core.config.EngineProjectConfiguration;
import kugge.rendering.core.json.SceneStorageJSON;
import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.core.physics.PhysicsBody;
import kugge.rendering.graphics.Renderer;
import kugge.rendering.graphics.Window;
import kugge.rendering.graphics.opengl.OpenGLRenderer;
import kugge.rendering.graphics.opengl.OpenGLWindow;

public class RenderingEngine {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("No project path provided.");
        }

        RenderingEngine engine = new RenderingEngine();
        String projectPath = args[0];
        EngineProjectConfiguration config = EngineProjectConfiguration.loadProjectConfiguration(projectPath);
        engine.start(config);
    }

    public void start(EngineProjectConfiguration config) {
        // Populate the global paths from the configuration
        EngineProjectConfiguration.populateGlobalPaths(config);

        // Load the initial scene
        RenderScene scene = null;
        try {
            SceneStorage storage = new SceneStorageJSON();
            scene = storage.loadScene(config.getInitialSceneID());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Create the window and renderer and link them together
        Renderer renderer = new OpenGLRenderer(scene);
        Window window = new OpenGLWindow(config);                
        renderer.linkToWindow(window);
        
        // Bind the key input to listen to the window
        KeyInput keyInput = new KeyInput();
        keyInput.bindToWindow(window);
        
        // Set of objects that need to be updated every frame
        CameraController cameraController = new CameraController(scene.getCamera());
        Set<Updateable> updateables = Set.of(
            cameraController
        );

        // Timing variables
        long currentTime = 0;
        long previousTime = System.currentTimeMillis();
        long deltaTime = 0;
        long timeTaken = 0;
        int targetFPS = config.getTargetFPS();

        boolean paused = false; 

        // Game loop
        while (true) {
            currentTime = System.currentTimeMillis();
            deltaTime = currentTime - previousTime;
            previousTime = currentTime;

            if (keyInput.isKeyPressed(KeyEvent.VK_ESCAPE)) {
                window.destroy();
                System.exit(0);
            }

            if (keyInput.isKeyPressed(KeyEvent.VK_F11)) {
                window.toggleFullscreen();
            }

            if (keyInput.isKeyPressed(KeyEvent.VK_S)) {
                if (keyInput.isKeyHeld(KeyEvent.VK_CONTROL)) {
                    try {
                        SceneStorage storage = new SceneStorageJSON();
                        storage.saveScene(scene);
                        System.out.println("Scene saved");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } 
            } 

            // Update
            for (Updateable updateable : updateables) {
                updateable.update(keyInput, deltaTime);
            }

            if (keyInput.isKeyPressed(KeyEvent.VK_P)) {
                paused = !paused;
            }

            if (!paused) {
                // Update the physics bodies
                for (Instance instance : scene.getInstances().parallelStream().filter(i -> i.getBodyID() != -1).toList()) {
                    PhysicsBody body = scene.getPhysicsWorld().getBody(instance.getBodyID());
                    body.setPosition(instance.getTransform().getPosition());
                    body.setRotation(instance.getTransform().getRotation());
                }

                // Update physics simulation
                scene.getPhysicsWorld().tick(1.0 / targetFPS);
                
                // Update the instance positions
                for (Instance instance : scene.getInstances().parallelStream().filter(i -> i.getBodyID() != -1).toList()) {
                    PhysicsBody body = scene.getPhysicsWorld().getBody(instance.getBodyID());
                    if (body != null) {
                        instance.getTransform().setPosition(body.getPosition());
                        instance.getTransform().setRotation(body.getRotation());
                    }
                }
            }

            keyInput.clear();
            renderer.render();

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
            }
        }
    }
}
