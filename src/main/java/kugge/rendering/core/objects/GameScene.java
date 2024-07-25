package kugge.rendering.core.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.physics.PhysicsWorld;
import kugge.rendering.core.scripting.ScriptingEngine;
import kugge.rendering.graphics.RenderingEngine;

public class GameScene implements GameObjectListener {
    private int ID;

    private List<GameObject> gameObjects;

    private List<Subsystem> subsystems;

    private final Map<Class<? extends GameComponent>, List<Consumer<GameComponent>>> componentDestroyListeners;
    private final Map<Class<? extends GameComponent>, List<Consumer<GameComponent>>> componentInitListeners;

    public GameScene(int ID) {
        this.ID = ID;
        this.gameObjects = new ArrayList<>();
        this.componentDestroyListeners = new HashMap<>();
        this.componentInitListeners = new HashMap<>();

        RenderingEngine renderingEngine = new RenderingEngine();

        KeyInput keyInput = new KeyInput();
        renderingEngine.getWindow().registerEventListener(keyInput.getKeyRegisterer());

        subsystems = List.of(
            new PhysicsWorld(1.0 / 60.0),
            renderingEngine,
            new ScriptingEngine(keyInput)
            // new AudioEngine()
        );
        
        // Add component destroy and init listeners
        for (Subsystem subsystem : subsystems) {
            subsystem.getComponentDestroyListeners().forEach((type, listener) -> {
                if (!componentDestroyListeners.containsKey(type)) {
                    componentDestroyListeners.put(type, new ArrayList<>());
                }
                componentDestroyListeners.get(type).add(listener);
            });

            subsystem.getComponentInitListeners().forEach((type, listener) -> {
                if (!componentInitListeners.containsKey(type)) {
                    componentInitListeners.put(type, new ArrayList<>());
                }
                componentInitListeners.get(type).add(listener);
            });
        }
    }

    @Override
    public void onGameObjectDestroy(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }

    @Override
    public void onComponentAdded(Class<? extends GameComponent> componentClass, GameComponent component) {
        var componentListeners = getComponentListeners(componentClass, componentInitListeners);
        if (componentListeners != null) {
            for (Consumer<GameComponent> listener : componentListeners) {
                listener.accept(component);
            }
        }
    }

    private List<Consumer<GameComponent>> getComponentListeners(Class<? extends GameComponent> componentClass, Map<Class<? extends GameComponent>, List<Consumer<GameComponent>>> componentListeners) {
        for (var listeners : componentListeners.entrySet()) {
            if (listeners.getKey().isAssignableFrom(componentClass)) {
                return listeners.getValue();
            }
        }
        return null;
    }

    @Override
    public void onComponentRemoved(Class<? extends GameComponent> componentClass, GameComponent component) {
        var componentListeners = getComponentListeners(componentClass, componentDestroyListeners);
        if (componentListeners != null) {
            for (Consumer<GameComponent> listener : componentListeners) {
                listener.accept(component);
            }
        }
    }

    public void update(float dt) {
        for (Subsystem subsystem : subsystems) {
            subsystem.update(dt);
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void removeGameObject(GameObject gameObject) {
        for (GameComponent component : gameObject.getComponents()) {
            var listeners = componentDestroyListeners.get(component.getClass());
            if (listeners != null) {
                for (Consumer<GameComponent> listener : listeners) {
                    listener.accept(component);
                }
            }
        }
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.registerListener(this);
        for (GameComponent component : gameObject.getComponents()) {
            onComponentAdded(component.getClass(), component);
        }

        for (GameObject child : gameObject.getChildren()) {
            addGameObject(child);
        }
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
