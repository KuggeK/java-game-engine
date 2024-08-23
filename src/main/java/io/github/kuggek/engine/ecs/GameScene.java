package io.github.kuggek.engine.ecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.kuggek.engine.core.config.ProjectPaths;
import io.github.kuggek.engine.core.json.GameSceneAdapters;
import io.github.kuggek.engine.subsystems.GameSceneSettings;

public class GameScene implements GameSceneSettings {
    private int ID;

    private String name;

    private Map<Integer, GameObject> gameObjects;

    public GameScene(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.gameObjects = new TreeMap<>();
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
     * @param ID The ID of the game object to remove
     * @return True if the game object was removed, false if it was not in the scene
     */
    public boolean removeGameObject(Integer ID) {
        GameObject gameObject = gameObjects.remove(ID);
        if (gameObject == null) {
            return false;
        }

        for (GameObject child : gameObject.getChildren()) {
            removeGameObject(child);
        }
        return true;
    } 

    /**
     * Removes a game object and all of its children from the scene
     * @param gameObject The game object to remove
     * @return True if the game object was removed, false if it was not in the scene
     */
    public boolean removeGameObject(GameObject gameObject) {
        return removeGameObject(gameObject.getID());
    }

    /**
     * Adds a game object and all of its children to the scene
     * @param gameObject The game object to add
     */
    public void addGameObject(GameObject gameObject) {
        GameObject occupier = gameObjects.putIfAbsent(gameObject.getID(), gameObject);
        if (occupier != null) {
            throw new IllegalArgumentException("A game object with the ID " + gameObject.getID() + " already exists in the scene");
        }

        for (GameObject child : gameObject.getChildren()) {
            addGameObject(child);
        }
    }

    /**
     * Returns a read-only set of all game objects in the scene
     * @return A read-only set of all game objects in the scene
     */
    public Set<GameObject> getGameObjects() {
        return Set.copyOf(gameObjects.values());
    }

    @Override
    public GameObject getGameObject(int ID) {
        return gameObjects.get(ID);
    }

    public Set<GameObject> getRootLevelGameObjects() {
        return gameObjects.values().stream().filter(gameObject -> gameObject.getParent() == null).collect(Collectors.toSet());
    }

    /**
     * Loads a scene with the given name from a JSON file
     * @param sceneName The name of the scene to load
     * @return The loaded scene
     * @throws IOException If the file cannot be read
     */
    public static GameScene loadScene(String sceneName) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        GameSceneAdapters.registerAdapters(builder);
        Gson gson = builder.create();

        String path = ProjectPaths.concatenatePaths(ProjectPaths.SCENES_PATH, sceneName + ".json");
        String jsonString = Files.readString(Paths.get(path));
        return gson.fromJson(jsonString, GameScene.class);
    }

    /**
     * Copies a scene by serializing it to JSON and then deserializing it
     * @param scene The scene to copy
     * @return The copied scene
     */
    public static GameScene copyScene(GameScene scene) {
        GsonBuilder builder = new GsonBuilder();
        GameSceneAdapters.registerAdapters(builder);
        Gson gson = builder.create();
        String jsonString = gson.toJson(scene);
        return gson.fromJson(jsonString, GameScene.class);
    }
}
