package io.github.kuggek.engine.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;
import org.ode4j.math.DVector3;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

public class PhysicsEngine implements PhysicsSettings {
    
    private DWorld world;
    private DSpace space;

    private Set<PhysicsBody> bodies;
    private Map<Integer, PhysicsCollider> colliders;

    private Map<PhysicsBody, DVector3> corrections;

    private Vector3f gravity;

    private Map<PhysicsBody, PhysicsCollider> bodyColliderLinks;

    private Set<DBody> bodiesToDestroy;
    private Set<DGeom> collidersToDestroy;

    public PhysicsEngine() {
        world = OdeHelper.createWorld();
        space = OdeHelper.createSimpleSpace();

        bodies = new HashSet<>();
        colliders = new HashMap<>();
        corrections = new HashMap<>();
        
        gravity = new Vector3f(0, -9.81f, 0);
        world.setGravity(gravity.x, gravity.y, gravity.z);
        world.setDamping(0.005, 0.005);

        bodyColliderLinks = new HashMap<>();

        bodiesToDestroy = new HashSet<>();
        collidersToDestroy = new HashSet<>();
    }

    public Vector3f getGravity() {
        return new Vector3f(gravity);
    }

    @Override
    public void setGravity(float x, float y, float z) {
        gravity.set(x, y, z);
        world.setGravity(gravity.x, gravity.y, gravity.z);
    }

    @Override
    public void setGravity(float[] gravity) {
        setGravity(gravity[0], gravity[1], gravity[2]);
    }

    @Override
    public void setGravity(Vector3f gravity) {
        setGravity(gravity.x, gravity.y, gravity.z);
    }

    public void updateSimulation(double timeStep) {

        // Destroy bodies and colliders
        for (DBody body : bodiesToDestroy) {
            body.destroy();
        }
        bodiesToDestroy.clear();

        for (DGeom collider : collidersToDestroy) {
            collider.destroy();
        }
        collidersToDestroy.clear();

        // Sync the physics bodies with the game objects
        for (PhysicsBody body : bodies) {
            if (body.isDisabled()) {
                continue;
            }

            body.syncToGameObject();
        }

        for (PhysicsCollider collider : colliders.values()) {
            if (collider.isDisabled()) {
                continue;
            }

            collider.syncToGameObject();
        }

        // Step the world
        world.quickStep(timeStep);
        
        // Collide the space
        corrections = Collisions.collideSpace(space);
        
        // Apply positional corrections from collisions
        for (Map.Entry<PhysicsBody, DVector3> entry : corrections.entrySet()) {
            PhysicsBody physBody = entry.getKey();
            if (physBody == null) {
                System.out.println("Body not found");
                continue;
            }
            DBody body = physBody.getBody();
            DVector3 correction = entry.getValue();
            body.setPosition(body.getPosition().reAdd(correction));
        }
        corrections.clear();

        // Sync the game objects with the physics bodies
        for (PhysicsBody body : bodies) {
            body.syncToPhysicsBody();
        }
    }

    public void addBody(PhysicsBody body) {
        body.linkToWorld(world);
        if (body.getColliderID() != null) {
            linkBodyAndCollider(body, body.getColliderID());
        }
        bodies.add(body);
    }

    public void addCollider(PhysicsCollider collider) {
        if (collider.getCollider() == null) {
            collider.setCollider(collider.getColliderType());
        }
        space.add(collider.getCollider());
        colliders.put(collider.getID(), collider);
    }

    @Override
    public boolean linkBodyAndCollider(PhysicsBody body, int colliderID) {
        PhysicsCollider collider = colliders.get(colliderID);
        if (collider == null) {
            return false;
        }
        return linkBodyAndCollider(body, collider);
    }

    @Override
    public boolean linkBodyAndCollider(PhysicsBody body, PhysicsCollider collider) {
        if (body == null || collider == null) {
            return false;
        }

        DGeom geom = collider.getCollider();
        geom.setBody(body.getBody());
        bodyColliderLinks.put(body, collider);
        return true;
    }

    public List<PhysicsBody> getBodies() {
        return new ArrayList<>(bodies);
    }

    public List<PhysicsCollider> getColliders() {
        return new ArrayList<>(colliders.values());
    }

    public void removeBody(PhysicsBody body) {
        bodiesToDestroy.add(body.getBody());
        bodies.remove(body);
        bodyColliderLinks.remove(body);
    }

    public void removeCollider(PhysicsCollider collider) {
        collidersToDestroy.add(collider.getCollider());
        colliders.remove(collider.getID());
    }

    public void clear() {
        bodies.clear();
        colliders.clear();
        corrections.clear();
        bodyColliderLinks.clear();
        bodiesToDestroy.clear();
        collidersToDestroy.clear();

        world.destroy();
        space.destroy();

        world = OdeHelper.createWorld();
        space = OdeHelper.createSimpleSpace();

        world.setGravity(gravity.x, gravity.y, gravity.z);
        world.setDamping(0.005, 0.005);
    }
}
