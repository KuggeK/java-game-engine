package io.github.kugge.engine.ecs;

public interface GameObjectManager {
    GameComponent createComponent(GameComponent component);
    void disposeComponent(GameComponent component);
    void addGameObject(GameObject gameObject);
    void removeGameObject(GameObject gameObject);
}
