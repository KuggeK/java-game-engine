package kugge.engine.ecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kugge.engine.core.json.GameSceneAdapters;

public class GameScene {
    private int ID;

    private List<GameObject> gameObjects;

    public GameScene(int ID) {
        this.ID = ID;
        this.gameObjects = new ArrayList<>();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void removeGameObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
        for (GameComponent component : gameObject.getComponents()) {
            component.dispose();
        }

        for (GameObject child : gameObject.getChildren()) {
            removeGameObject(child);
        }
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        for (GameObject child : gameObject.getChildren()) {
            addGameObject(child);
        }
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Loads a scene with the given ID from a JSON file
     * @param sceneID The ID of the scene to load
     * @return The loaded scene
     * @throws IOException If the file cannot be read
     */
    public static GameScene loadScene(int sceneID) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        GameSceneAdapters.registerAdapters(builder);
        Gson gson = builder.create();
        String jsonString = Files.readString(Paths.get("scene" + sceneID + ".json"));
        return gson.fromJson(jsonString, GameScene.class);
    }
}
