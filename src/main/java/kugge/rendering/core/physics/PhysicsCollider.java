package kugge.rendering.core.physics;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DQuaternion;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DBox;
import org.ode4j.ode.DCapsule;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DSphere;
import org.ode4j.ode.OdeHelper;

import kugge.rendering.core.objects.ComponentField;
import kugge.rendering.core.objects.GameComponent;

public class PhysicsCollider extends GameComponent {
    public enum ColliderType {
        BOX,
        SPHERE,
        CAPSULE
    }

    @ComponentField
    private int ID;

    @ComponentField
    private ColliderType colliderType;
    
    private DGeom collider;

    @ComponentField
    private Set<Consumer<PhysicsCollider>> collisionListeners = new HashSet<>();

    public PhysicsCollider(int ID, ColliderType colliderType) {
        super();
        this.ID = ID;
        switch (colliderType) {
            case BOX:
                collider = OdeHelper.createBox(1, 1, 1);
                break; 
            case SPHERE:
                collider = OdeHelper.createSphere(1);
                break;
            case CAPSULE:
                collider = OdeHelper.createCapsule(1, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid collider type");
        }
        this.colliderType = colliderType;
        collider.setData(this);
    }

    public PhysicsCollider(int ID, ColliderType colliderType, DSpace space) {
        this(ID, colliderType);
        space.add(collider);
    }

    public void registerCollisionListener(Consumer<PhysicsCollider> listener) {
        // Lazy initialization, because component fields don't get initialized correctly
        if (collisionListeners == null) {
            collisionListeners = new HashSet<>();
        }
        collisionListeners.add(listener);
    }

    public void unregisterCollisionListener(Consumer<PhysicsCollider> listener) {
        if (collisionListeners == null) {
            return;
        }
        collisionListeners.remove(listener);
    }

    public void onCollision(PhysicsCollider other) {
        if (collisionListeners == null) {
            return;
        }

        for (Consumer<PhysicsCollider> listener : collisionListeners) {
            listener.accept(other);
        }
    }

    public int getID() {
        return ID;
    }

    protected DGeom getCollider() {
        return collider;
    }

    public void setCollider(ColliderType colliderType) {
        this.colliderType = colliderType;

        DBody linkedBody = null;
        if (collider != null) {
            linkedBody = collider.getBody();
            collider.destroy();
        }

        switch (colliderType) {
            case BOX:
                collider = OdeHelper.createBox(1, 1, 1);
                break; 
            case SPHERE:
                collider = OdeHelper.createSphere(1);
                break;
            case CAPSULE:
                collider = OdeHelper.createCapsule(1, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid collider type");
        }
        collider.setData(this);
        
        if (linkedBody != null) {
            collider.setBody(linkedBody);
        }
        setScale(transform.getScale());
    }
    
    public void setBoxDimensions(float x, float y, float z) {
        if (collider instanceof DBox) {
            DBox box = (DBox) collider;
            box.setLengths(x, y, z);
        }
    }

    public void setSphereRadius(float radius) {
        if (collider instanceof DSphere) {
            DSphere sphere = (DSphere) collider;
            sphere.setRadius(radius);
        }
    }

    public void setCapsuleDimensions(float radius, float length) {
        if (collider instanceof DCapsule) {
            DCapsule capsule = (DCapsule) collider;
            capsule.setParams(radius, length);
        }
    }

    public void setScale(Vector3f scale) {
        switch (colliderType) {
            case BOX:
                setBoxDimensions(scale.x, scale.y, scale.z);
                break;
            case SPHERE:
                setSphereRadius(scale.x);
                break;
            case CAPSULE:
                setCapsuleDimensions(scale.x, scale.y);
                break;
            default:
                throw new IllegalArgumentException("Invalid collider type");
        }
    }

    public void setToSpace(DSpace space) {
        space.add(collider);
    }

    public ColliderType getColliderType() {
        return colliderType;
    }

    public void updateScale(Vector3f scale) {
        switch (colliderType) {
            case BOX:
                setBoxDimensions(scale.x, scale.y, scale.z);
                break;
            case SPHERE:
                setSphereRadius(scale.x);
                break;
            case CAPSULE:
                setCapsuleDimensions(scale.x, scale.y);
                break;
            default:
                throw new IllegalArgumentException("Invalid collider type");
        }
    }

    public Vector3f setPhysPosition(Vector3f position) {
        collider.setPosition(position.x, position.y, position.z);
        return position;
    }

    public Vector3f setPhysPosition(float x, float y, float z) {
        collider.setPosition(x, y, z);
        return new Vector3f(x, y, z);
    }

    public Vector3f getPhysPosition(Vector3f dest) {
        DVector3C pos = collider.getPosition();
        return dest.set((float) pos.get0(), (float) pos.get1(), (float) pos.get2());
    }

    public Vector3f getPhysPosition() {
        return getPhysPosition(new Vector3f());
    }

    public void setPhysRotation(float x, float y, float z, float w) {
        collider.setQuaternion(new DQuaternion(w, x, y, z));
    }

    public void setPhysRotation(Quaternionf rotation) {
        collider.setQuaternion(new DQuaternion(rotation.w, rotation.x, rotation.y, rotation.z));
    }

    public Quaternionf getPhysRotation(Quaternionf dest) {
        DQuaternionC quat = collider.getQuaternion();
        return dest.set((float) quat.get1(), (float) quat.get2(), (float) quat.get3(), (float) quat.get0());
    }

    public Quaternionf getPhysRotation() {
        return getPhysRotation(new Quaternionf());
    }

    /**
     * Sync the physics collider's position and rotation to the game object's transform.
     */
    public void syncToGameObject() {
        setPhysPosition(gameObject.getTransform().getPosition());
        setPhysRotation(gameObject.getTransform().getRotation());
    }
}
