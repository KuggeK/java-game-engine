package io.github.kuggek.engine.ecs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.kuggek.engine.core.Transform;
import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.ecs.components.ReadableComponentField;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

/**
 * Represents an abstract component of a game object. Components are used to add functionality
 * to game objects. For example, a game object can have a physics component, a rendering component,
 * a sound component, etc.
 */
public abstract class GameComponent {
    protected Transform transform;

    protected GameObject gameObject;

    private boolean disabled = true;

    public GameComponent(GameObject gameObject) {
        this.transform = gameObject.getTransform();
        this.gameObject = gameObject;
    }

    public GameComponent() {
        this.transform = null;
        this.gameObject = null;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public Transform getTransform() {
        return transform;
    }
    
    /**
     * Strip the component of its references to it's game object.
     */
    public void strip() {
        this.transform = null;
        this.gameObject = null;
    }

    /**
     * Subclasses can override this method to perform any operations that need to be done
     * when the component is disposed of.
     */
    protected void onDispose(EngineRuntimeSettings settings) {

    }

    protected void onAwake(EngineRuntimeSettings settings) {

    }
    
    protected void onEnable() {

    }

    protected void onDisable() {

    }


    public static void awake(GameComponent component, EngineRuntimeSettings settings) {
        component.onAwake(settings);
    }

    public static void dispose(GameComponent component, EngineRuntimeSettings settings) {
        component.onDispose(settings);
    }

    /**
     * Get all fields of a component that are annotated with {@link EditableComponentField} or
     * {@link ReadableComponentField}. 
     * This method will return all fields of the component class and all superclasses
     * @param componentClass
     * @return
     */
    public static Set<Field> getComponentFields(Class<? extends GameComponent> componentClass) {
        Set<Field> fields = new HashSet<>();
        Class<?> type = componentClass;
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(EditableComponentField.class) || field.isAnnotationPresent(ReadableComponentField.class)) {
                    fields.add(field);
                }
            }
            type = type.getSuperclass();
        }
        return fields;
    }

    /**
     * Get the values of all fields of a component that are annotated with {@link EditableComponentField} 
     * or {@link ReadableComponentField}.
     * This method will return a map of field names to field values.
     * @param component The component to get the field values from.
     * @return A map of field names to field values.
     */
    public static Map<String, Object> getComponentFieldValues(GameComponent component) {
        Map<String, Object> fieldValues = new HashMap<>();
        Set<Field> fields = getComponentFields(component.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(component);
                fieldValues.put(field.getName(), fieldValue);
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal argument exception for field " + field.getName());
            } catch (IllegalAccessException e) {
                System.out.println("Illegal access exception for field " + field.getName());
            }
        }
        return fieldValues;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;

        if (disabled) {
            onDisable();
        } else {
            onEnable();
        }
    }

    public boolean isDisabled() {
        // If the component is part of a game object, check if the game object is disabled
        if (gameObject != null && gameObject.isDisabled()) {
            return true;
        }
        
        return disabled;
    }
}
