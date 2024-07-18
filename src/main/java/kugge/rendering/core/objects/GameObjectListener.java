package kugge.rendering.core.objects;

public interface GameObjectListener {
    void onGameObjectDestroy(GameObject gameObject);
    void onComponentAdded(Class<? extends GameComponent> componentClass, GameComponent component);
    void onComponentRemoved(Class<? extends GameComponent> componentClass, GameComponent component);
}
