package io.github.kuggek.engine.physics;

import org.joml.Vector3f;

public interface PhysicsSettings {
    void setGravity(float x, float y, float z);
    void setGravity(float[] gravity);
    void setGravity(Vector3f gravity);    

    /**
     * Attempt to link a physics body to a collider.
     * @param body 
     * @param collider
     * @return
     */
    boolean linkBodyAndCollider(PhysicsBody body, PhysicsCollider collider);
    boolean linkBodyAndCollider(PhysicsBody body, int colliderID);
}
