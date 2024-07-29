package kugge.engine.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DWorld;

/**
 * PhysicsBody
 */
public interface PhysicsBody {
    int getID();

    DBody getBody();

    int getColliderID();

    void linkToWorld(DWorld world);

    /**
     * Get whether the physics body is influenced by gravity or not. 
     * Note that this only has an effect if the body is not kinematic.
     * @return Whether the physics body is influenced by gravity or not.
     */
    boolean isInfluencedByGravity();

    /**
     * Set whether the physics body is influenced by gravity or not. 
     * This only has an effect if the body is not kinematic.
     * @param influencedByGravity Whether the physics body should be influenced by gravity or not.
     */
    void setInfluencedByGravity(boolean influencedByGravity);

    /**
     * Get the position of the physics body, which may differ from the position of the game object.
     * @return The position of the physics body.
     */
    Vector3f getPhysPosition();

    /**
     * Stores the position of the physics body, which may differ from the position of the game object,
     * into the given destination vector.
     * @param dest The destination vector to store the position in.
     * @return The destination vector.
     */
    Vector3f getPhysPosition(Vector3f dest);

    /**
     * Set the position of the physics body, which may differ from the position of the game object.
     * @param position The position to set.
     */
    void setPhysPosition(Vector3f position);
    /**
     * Set the position of the physics body, which may differ from the position of the game object.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @param z The z-coordinate of the position.
     */
    void setPhysPosition(float x, float y, float z);
    /**
     * Get the rotation of the physics body, which may differ from the rotation of the game object.
     * @return
     */
    Quaternionf getPhysRotation();
    /**
     * Stores the rotation of the physics body, which may differ from the rotation of the game object,
     * into the given destination quaternion.
     * @param dest The destination quaternion to store the rotation in.
     * @return The destination quaternion.
     */
    Quaternionf getPhysRotation(Quaternionf dest);

    /**
     * Set the rotation of the physics body, which may differ from the rotation of the game object.
     * @param rot The rotation to set.
     */
    void setPhysRotation(Quaternionf rot);

    /**
     * Set the rotation of the physics body, which may differ from the rotation of the game object.
     * @param x The x-coordinate of the rotation.
     * @param y The y-coordinate of the rotation.
     * @param z The z-coordinate of the rotation.
     * @param w The w-coordinate of the rotation.
     */
    void setPhysRotation(float x, float y, float z, float w);

    /**
     * Returns whether the physics body is kinematic or not.
     * @return True if the physics body is kinematic, false otherwise.
     */
    boolean isKinematic();

    /**
     * Set whether the physics body is kinematic or not.
     * @param isKinematic True if the physics body should be kinematic, false otherwise.
     */
    void setKinematic(boolean kinematic);

    double getRestitution();

    void setRestitution(double restitution);

    double getMass();

    /**
     * Set the mass of the physics body. This only has an effect if the body is not kinematic 
     * because kinematic bodies are treated as having infinite mass. 
     * The mass will however be stored and used if the body is later set to be dynamic.
     * @param mass The mass to set.
     */
    void setMass(double newMass);
    /**
     * Synchronize the position and rotation of the physics body to the position and rotation of the game object.
     */
    void syncToGameObject();

    /**
     * Synchronize the position and rotation of the game object to the position and rotation of the physics body.
     */
    void syncToPhysicsBody();

    void setLinearVel(float x, float y, float z);
    void setLinearVel(Vector3f vel);

    void addLinearVel(float x, float y, float z);
    void addLinearVel(Vector3f vel);

    void setAngularVel(float x, float y, float z);
    void setAngularVel(Vector3f vel);

    void addAngularVel(float x, float y, float z);
    void addAngularVel(Vector3f vel);

    void setLinearDamping(float damping);

    void setAngularDamping(float damping);
}