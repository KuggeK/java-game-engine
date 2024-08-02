package io.github.kuggek.engine.ecs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.kuggek.engine.core.Transform;

public class GameObject {
    private int ID;

    private Map<Class<? extends GameComponent>, GameComponent> components;
    
    private Transform transform;

    private Optional<GameObjectManager> manager;

    private Set<String> tags;

    private GameObject parent;
    private Set<GameObject> children;

    public GameObject(int ID, GameObjectManager manager) {
        this.ID = ID;
        this.components = new HashMap<>();
        this.transform = new Transform();
        this.tags = new HashSet<>();
        this.children = new HashSet<>();
        this.manager = Optional.ofNullable(manager);
    }

    public GameObject(int ID) {
        this(ID, null);
    }

    public void setManager(GameObjectManager manager) {
        this.manager = Optional.ofNullable(manager);
    }

    public Set<GameComponent> getComponents() {
        return Set.copyOf(components.values());
    }

    public int getID() {
        return ID;
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
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    /**
     * Check if the game object has a component of a specific type.
     * @param type The type of the component to check for (some subclass of GameComponent).
     * @return True if the game object has a component of the specified type, false otherwise.
     */
    public boolean hasComponentOfType(Class<? extends GameComponent> type) {
        return components.containsKey(type);
    }

    /**
     * Add a component to the game object. 
     * @param component The component to add.
     * @param force If true, the component will replace any existing component of the same type, 
     * otherwise the previous component will be kept.
     */
    public void addComponent(GameComponent component, boolean force) {
        if (!force) {
            if (components.containsKey(component.getClass())) {
                return;
            }
        }
        addComponent(component);
    }

    /**
     * Add a component to the game object.
     * @param <T> The type of game component to add.
     * @param component
     */
    private <T extends GameComponent> void addComponent(T component) {
        component.gameObject = this;
        component.transform = transform;
        components.put(component.getClass(), component);
        manager.ifPresent(m -> m.createComponent(component));
    }

    /**
     * Remove a component from the game object.
     * @param component
     */
    public void removeComponent(GameComponent component) {
        components.remove(component.getClass());
        manager.ifPresent(m -> m.disposeComponent(component));
    }

    /**
     * Remove a component of a specific type from the game object if it has one.
     * @param type The type of the component to remove.
     */
    public void removeComponentOfType(Class<? extends GameComponent> type) {
        components.remove(type);
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

    /**
     * Destroy the game object. This will remove all components from the game object and
     * dispose of each of them. The game object will be unlinked from its parent and all of its
     * children will be destroyed. Finally, the game object will be removed from the manager.
     */
    public void destroy() {
        // Dispose of all components.
        manager.ifPresent(m -> {
            m.removeGameObject(this);
            components.values().forEach(m::disposeComponent);
        });

        components.clear();
        
        ID = -1;
        transform = null;

        // Unlink from parent.
        unlinkFromParent(this);

        // Destroy all children.
        for (GameObject child : children) {
            child.destroy();
        }
    }

    public GameObject getParent() {
        return parent;
    }

    public Set<GameObject> getChildren() {
        return Set.copyOf(children);
    }

    public void deleteChildren() {
        for (GameObject child : children) {
            child.destroy();
        }
    }

    /**
     * Links two game objects into a parent-child relationship. The child game object will be added
     * to the parent's list of children, and the child's parent reference will be set to the parent.
     * If the child already has a parent, it will be totally unlinked from that parent first.
     * @param parent
     * @param child
     */
    public static void link(GameObject parent, GameObject child) {
        GameObject previousParent = child.parent;
        if (previousParent == parent) {
            return;
        }

        if (previousParent != null) {
            unlinkFromParent(child);
        }

        child.parent = parent;
        parent.children.add(child);
        child.transform.setParent(parent.transform);
    }

    /**
     * Unlink a game object from its parent. This will remove the game object from its parent's
     * list of children and set the game object's parent to null, making it a root-level game object.
     * @param child
     */
    public static void unlinkFromParent(GameObject child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
            child.parent = null;
            child.transform.setParent(null);
        }
    }

    /**
     * Unlink all children from a parent game object. This will remove all children from the parent's
     * list of children and set the parent reference of each child to null. 
     * @param parent
     */
    public static void unlinkChildren(GameObject parent) {
        for (GameObject child : parent.getChildren()) {
            child.parent = null;
            child.transform.setParent(null);
        }
        parent.children.clear();
    }
}
