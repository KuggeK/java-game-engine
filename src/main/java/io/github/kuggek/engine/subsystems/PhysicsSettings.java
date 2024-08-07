package io.github.kuggek.engine.subsystems;

import org.joml.Vector3f;

public interface PhysicsSettings {
    void setGravity(float x, float y, float z);
    void setGravity(float[] gravity);
    void setGravity(Vector3f gravity);    
}
