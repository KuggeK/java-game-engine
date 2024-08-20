package io.github.kuggek.engine.ecs.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a readable component field. Used for choosing fields that should be 
 * readable by external editor systems and fields included in serialization and deserialization 
 * of components.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReadableComponentField {
    
}
