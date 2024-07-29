package kugge.engine.ecs;

public interface GameObjectListener {
    void onGameObjectDestroy(GameObject gameObject);
    void onComponentAdded(Class<? extends GameComponent> componentClass, GameComponent component);
    void onComponentRemoved(Class<? extends GameComponent> componentClass, GameComponent component);
}
