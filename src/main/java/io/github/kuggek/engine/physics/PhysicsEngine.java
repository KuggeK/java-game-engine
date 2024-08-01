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

public class PhysicsEngine {
    
    private DWorld world;
    private DSpace space;

    private Map<Integer, PhysicsBody> bodies;
    private Map<Integer, PhysicsCollider> colliders;

    private Map<Integer, DVector3> corrections;

    private Vector3f gravity;

    private Map<Integer, Integer> bodyColliderLinks;
    private Set<Integer> unlinkedBodies;

    private Set<DBody> bodiesToDestroy;
    private Set<DGeom> collidersToDestroy;

    public PhysicsEngine() {
        world = OdeHelper.createWorld();
        space = OdeHelper.createSimpleSpace();

        bodies = new HashMap<>();
        colliders = new HashMap<>();
        corrections = new HashMap<>();
        
        gravity = new Vector3f(0, -9.81f, 0);
        world.setGravity(gravity.x, gravity.y, gravity.z);
        world.setDamping(0.005, 0.005);

        bodyColliderLinks = new HashMap<>();
        unlinkedBodies = new HashSet<>();

        bodiesToDestroy = new HashSet<>();
        collidersToDestroy = new HashSet<>();
    }

    public Vector3f getGravity() {
        return new Vector3f(gravity);
    }

    public void setGravity(double x, double y, double z) {
        gravity.set(x, y, z);
        world.setGravity(gravity.x, gravity.y, gravity.z);
    }

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

        linkUnlinkedBodies();

        // Sync the physics bodies with the game objects
        for (PhysicsBody body : bodies.values()) {
            body.syncToGameObject();
        }

        // Step the world
        world.quickStep(timeStep);
        
        // Collide the space
        corrections = Collisions.collideSpace(space);
        
        // Apply positional corrections from collisions
        for (Map.Entry<Integer, DVector3> entry : corrections.entrySet()) {
            PhysicsBody physBody = bodies.get(entry.getKey());
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
        for (PhysicsBody body : bodies.values()) {
            body.syncToPhysicsBody();
        }
    }

    public PhysicsBody getBody(int ID) {
        return bodies.get(ID);
    }

    public void addBody(PhysicsBody body) {
        body.linkToWorld(world);
        if (body.getColliderID() != -1) {
            linkBodyAndCollider(body.getID(), body.getColliderID());
        }
        bodies.put(body.getID(), body);
    }

    public void addCollider(PhysicsCollider collider) {
        if (collider.getCollider() == null) {
            collider.setCollider(collider.getColliderType());
        }
        space.add(collider.getCollider());
        colliders.put(collider.getID(), collider);
    }

    public PhysicsCollider getCollider(int ID) {
        return colliders.get(ID);
    }

    public boolean linkBodyAndCollider(int bodyID, int colliderID) {
        PhysicsBody body = getBody(bodyID);
        PhysicsCollider collider = getCollider(colliderID);
        if (body == null || collider == null) {
            unlinkedBodies.add(bodyID);
            return false;
        }
        if (collider.getCollider().getBody() != null) {
            unlinkedBodies.add(bodyID);
            return false;
        }

        DGeom geom = collider.getCollider();
        geom.setBody(body.getBody());
        bodyColliderLinks.put(bodyID, colliderID);
        return true;
    }

    public void linkUnlinkedBodies() {
        Set<Integer> toRemove = new HashSet<>();
        for (int bodyID : unlinkedBodies) {
            if (linkBodyAndCollider(bodyID, bodies.get(bodyID).getColliderID())) {
                toRemove.add(bodyID);
            }
        }
        unlinkedBodies.removeAll(toRemove);
    }

    public List<PhysicsBody> getBodies() {
        return new ArrayList<>(bodies.values());
    }

    public List<PhysicsCollider> getColliders() {
        return new ArrayList<>(colliders.values());
    }

    public void removeBody(PhysicsBody body) {
        bodiesToDestroy.add(body.getBody());
        bodies.remove(body.getID());
        bodyColliderLinks.remove(body.getID());
    }

    public void removeCollider(PhysicsCollider collider) {
        collidersToDestroy.add(collider.getCollider());
        colliders.remove(collider.getID());
    }
}
