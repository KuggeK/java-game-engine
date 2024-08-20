package io.github.kuggek.engine.subsystems;

import io.github.kuggek.engine.ecs.GameObject;

public interface GameSceneSettings {
    /**
     * Get the game object with the given ID from the scene
     * @param ID The ID of the game object
     * @return The game object with the given ID or null if it does not exist
     */
    GameObject getGameObject(int ID);
}
