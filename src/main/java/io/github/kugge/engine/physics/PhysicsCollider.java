package io.github.kugge.engine.physics;

import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;

public interface PhysicsCollider {
    public enum ColliderType {
        BOX,
        SPHERE,
        CAPSULE
    }

    void registerCollisionListener(Consumer<PhysicsCollider> listener);

    void unregisterCollisionListener(Consumer<PhysicsCollider> listener);

    void onCollision(PhysicsCollider other);

    int getID();

    DGeom getCollider();

    void setCollider(ColliderType colliderType);
    
    void setBoxDimensions(float x, float y, float z);

    void setSphereRadius(float radius);

    void setCapsuleDimensions(float radius, float length);

    void setScale(Vector3f scale);

    void setToSpace(DSpace space);

    ColliderType getColliderType();

    void updateScale(Vector3f scale);

    Vector3f setPhysPosition(Vector3f position);

    Vector3f setPhysPosition(float x, float y, float z);

    Vector3f getPhysPosition(Vector3f dest);

    Vector3f getPhysPosition();

    void setPhysRotation(float x, float y, float z, float w);

    void setPhysRotation(Quaternionf rotation);

    Quaternionf getPhysRotation(Quaternionf dest);

    Quaternionf getPhysRotation();

    /**
     * Sync the physics collider's position and rotation to the game object's transform.
     */
    void syncToGameObject();
}
