package kugge.rendering;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kugge.rendering.core.config.EngineProjectConfiguration;
import kugge.rendering.core.json.GameSceneAdapters;
import kugge.rendering.core.objects.GameScene;

public class GameEngine {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("No project path provided.");
        }

        GameEngine engine = new GameEngine();
        String projectPath = args[0];
        EngineProjectConfiguration config = EngineProjectConfiguration.loadProjectConfiguration(projectPath);
        engine.start(config);
    }

    public void start(EngineProjectConfiguration config) throws Exception {
        // Populate the global paths from the configuration
        EngineProjectConfiguration.populateGlobalPaths(config);

        // Load the initial scene
        GsonBuilder builder = new GsonBuilder();
        GameSceneAdapters.registerAdapters(builder);
        Gson gson = builder.create();
        String jsonString = Files.readString(Paths.get("scene1.json"));
        GameScene scene = gson.fromJson(jsonString, GameScene.class);

        // Timing variables
        long currentTime = 0;
        long previousTime = System.currentTimeMillis();
        long deltaTime = 0;
        long timeTaken = 0;
        int targetFPS = config.getTargetFPS();


        // Game loop
        while (true) {
            currentTime = System.currentTimeMillis();
            deltaTime = currentTime - previousTime;
            previousTime = currentTime;

            scene.update(deltaTime);

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
