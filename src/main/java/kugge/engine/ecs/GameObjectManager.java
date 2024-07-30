package kugge.engine.ecs;

public interface GameObjectManager {
    GameComponent createComponent(GameComponent component);
    void disposeComponent(GameComponent component);
}
