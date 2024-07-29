package kugge.engine.ecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kugge.engine.core.json.GameSceneAdapters;

public class GameScene {
    private int ID;

    private String name;

    private Set<GameObject> gameObjects;

    public GameScene(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.gameObjects = new HashSet<>();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Removes a game object and all of its children from the scene
     * @param gameObject The game object to remove
     */
    public void removeGameObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
        for (GameObject child : gameObject.getChildren()) {
            removeGameObject(child);
        }
    }

    /**
     * Adds a game object and all of its children to the scene
     * @param gameObject The game object to add
     */
    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        for (GameObject child : gameObject.getChildren()) {
            addGameObject(child);
        }
    }

    public Set<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Loads a scene with the given ID from a JSON file
     * @param sceneID The ID of the scene to load
     * @return The loaded scene
     * @throws IOException If the file cannot be read
     */
    public static GameScene loadScene(String sceneName) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        GameSceneAdapters.registerAdapters(builder);
        Gson gson = builder.create();
        String jsonString = Files.readString(Paths.get(sceneName + ".json"));
        return gson.fromJson(jsonString, GameScene.class);
    }
}
