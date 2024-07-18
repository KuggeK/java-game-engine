package kugge.rendering.core.objects;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameObject {
    private int ID;

    private Set<GameComponent> components;
    
    private Transform transform;

    private GameObjectListener gameObjectListener;

    private Set<String> tags;

    private boolean setToBeDestroyed = false;

    public GameObject(int ID) {
        this.ID = ID;
        this.components = new HashSet<>();
        this.transform = new Transform();
        this.tags = new HashSet<>();
    }

    public Set<GameComponent> getComponents() {
        return components;
    }

    public int getID() {
        return ID;
    }

    public boolean isSetToBeDestroyed() {
        return setToBeDestroyed;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public Set<String> getTags() {
        return Set.copyOf(tags);
    }

    /**
     * Get a component of a specific type from the game object if it exists.
     * @param type The type of the component to get.
     * @return An optional containing the component if it exists. If this method
     * returns a non-empty optional, the component is guaranteed to be of the specified type, 
     * and can be safely cast to that type.
     */
    public <T extends GameComponent> Optional<T> getComponentOfType(Class<T> type) {
        for (GameComponent c : components) {
            if (c.getClass().equals(type)) {
                return Optional.of(type.cast(c));
            }
        }
        return Optional.empty();
    }

    /**
     * Check if the game object has a component of a specific type.
     * @param type The type of the component to check for (some subclass of GameComponent).
     * @return True if the game object has a component of the specified type, false otherwise.
     */
    public boolean hasComponentOfType(Class<? extends GameComponent> type) {
        for (GameComponent c : components) {
            if (c.getClass().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a component to the game object. 
     * @param component The component to add.
     * @param force If true, the component will replace any existing component of the same type, 
     * otherwise the previous component will be kept.
     */
    public void addComponent(GameComponent component, boolean force) {
        for (GameComponent oldComponent : components) {
            if (oldComponent.getClass().equals(component.getClass())) {
                if (force) {
                    boolean removed = removeComponent(oldComponent);
                    if (!removed) {
                        System.out.println("Failed to remove existing component of type " + component.getClass().getName() + " from game object.");
                        return;
                    }
                    System.out.println("Replacing component of type " + component.getClass().getName() + " on game object " + ID);
                    addComponent(component);
                } else {
                    System.out.println("Component of type " + component.getClass().getName() + " already exists on this game object.");
                }
                return;
            }
        }
        addComponent(component);
    }

    private void addComponent(GameComponent component) {
        component.gameObject = this;
        component.transform = transform;
        components.add(component);
        if (gameObjectListener != null) {
            gameObjectListener.onComponentAdded(component.getClass(), component);
        }
    }

    /**
     * Remove a component from the game object.
     * @param component
     */
    public boolean removeComponent(GameComponent component) {
        boolean removed = components.remove(component);
        if (removed) {
            if (gameObjectListener != null) {
                gameObjectListener.onComponentRemoved(component.getClass(), component);
            }
            component.dispose();
        }
        return removed;
    }

    /**
     * Remove a component of a specific type from the game object if it has one.
     * @param type The type of the component to remove.
     * @return True if a component of the specified type was removed, false otherwise.
     */
    public boolean removeComponentOfType(Class<? extends GameComponent> type) {
        for (GameComponent c : components) {
            if (c.getClass().equals(type)) {
                return removeComponent(c);
            }
        }
        return false;
    }

    public Transform getTransform() {
        return transform;
    }

    /**
     * Copies the values of the given transform into the game object's transform.
     * @param transform
     */
    public void setTransform(Transform transform) {
        this.transform.set(transform);
    }

    public void registerListener(GameObjectListener listener) {
        this.gameObjectListener = listener;
    }

    /**
     * Destroy the game object. This will remove all components from the game object and
     * dispose of each of them. 
     */
    public void destroy() {
        // Notify listeners that the game object is being destroyed.
        if (gameObjectListener != null) {
            gameObjectListener.onGameObjectDestroy(this);
        }

        // Dispose of all components.
        for (GameComponent c : components) {
            removeComponent(c);
        }
        
        ID = -1;
        transform = null;
    }

    public void setToBeDestroyed() {
        setToBeDestroyed = true;
    }
}
