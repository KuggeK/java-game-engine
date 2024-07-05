package kugge.rendering.core.physics;

import org.joml.Vector3f;
import org.ode4j.ode.DBox;
import org.ode4j.ode.DCapsule;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DSphere;
import org.ode4j.ode.OdeHelper;

public class PhysicsCollider {
    public enum ColliderType {
        BOX,
        SPHERE,
        CAPSULE
    }

    private int ID;
    private ColliderType colliderType;
    private DGeom collider;

    public PhysicsCollider(int ID, ColliderType colliderType) {
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
    }

    public PhysicsCollider(int ID, ColliderType colliderType, DSpace space) {
        this(ID, colliderType);
        space.add(collider);
    }

    public int getID() {
        return ID;
    }

    public DGeom getCollider() {
        return collider;
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
}
