package kugge.rendering.core.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kugge.rendering.core.SceneStorage;
import kugge.rendering.core.config.ProjectPaths;
import kugge.rendering.core.objects.RenderScene;

public class SceneStorageJSON implements SceneStorage {

    private boolean debugMode = false;

    public SceneStorageJSON() {
        this(false);
    }

    /**
     * Create a new SceneStorageJSON object with the specified debug mode.
     * @param debugMode If true, the mesh and texture files will not be loaded and dummy objects will be created instead.
     */
    public SceneStorageJSON(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public RenderScene loadScene(int sceneID) throws IOException, URISyntaxException {
        File file = new File(ProjectPaths.getScenePath(sceneID));
        String json = Files.readString(Paths.get(file.toURI()));

        GsonBuilder builder = new GsonBuilder();

        RenderSceneAdapters.registerAdapters(builder, debugMode);
        
        Gson gson = builder.create();
        return gson.fromJson(json, RenderScene.class);
    }

    public void saveScene(RenderScene scene) {
        GsonBuilder builder = new GsonBuilder();
        RenderSceneAdapters.registerAdapters(builder, debugMode);
        Gson gson = builder.create();
        String json = gson.toJson(scene);
        
        try {
            File file = new File("C:/Users/Kilian/Documents/Programming/Java/rendering-engine/scenes/scene" + scene.getID() + ".json");
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
