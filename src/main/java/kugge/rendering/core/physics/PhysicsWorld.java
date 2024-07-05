package kugge.rendering.core.physics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joml.Vector3f;
import org.ode4j.math.DMatrix3;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMassC;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;
import org.ode4j.ode.OdeMath;

import kugge.rendering.core.physics.PhysicsCollider.ColliderType;

public class PhysicsWorld {
    
    private DWorld world;
    private DSpace space;

    private Map<Integer, PhysicsBody> bodies;
    private Map<Integer, PhysicsCollider> colliders;

    private Map<Integer, DVector3> corrections;

    private Vector3f gravity;

    public PhysicsWorld() {
        world = OdeHelper.createWorld();
        space = OdeHelper.createSimpleSpace();

        bodies = new HashMap<>();
        colliders = new HashMap<>();
        corrections = new HashMap<>();
        
        gravity = new Vector3f(0, -9.81f, 0);
        world.setGravity(gravity.x, gravity.y, gravity.z);
        world.setDamping(0.005, 0.005);
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

    public void tick(double timeStep) {
        world.quickStep(timeStep);
        Map<Integer, Integer> bodyIDs = bodies.entrySet().stream().collect(Collectors.toMap(e -> e.getValue().getBody().hashCode(), Map.Entry::getKey));
        space.collide(bodyIDs, (Object data, DGeom o1, DGeom o2) -> {
            long bit1 = o1.getCollideBits();
            long bit2 = o2.getCollideBits();
            if ((bit1 & bit2) == 0) {
                return;
            }

            // Check if the bodies are actually colliding
            o1.collide2(o2, data, (bodyData, c1, c2) -> {
                // Get collision info
                Optional<Collision> potentialCollision = Collisions.collide(c1, c2);

                if (potentialCollision.isEmpty()) {
                    return;
                }
                Collision collision = potentialCollision.get();

                DBody b1 = collision.getGeom1().getBody();
                DBody b2 = collision.getGeom2().getBody();

                if (b1 == null || b2 == null) {
                    return;
                }

                if (b1.isKinematic() && b2.isKinematic()) {
                    return;
                }

                // TODO Send collision event
                DMassC mass1 = b1.getMass();
                DMassC mass2 = b2.getMass();

                // If one of the bodies is kinematic, set it's inverse mass to 0 
                // to avoid applying an impulse to it and to avoid division by 0.
                double invMass1 = b1.isKinematic() ? 0 : 1 / mass1.getMass();
                double invMass2 = b2.isKinematic() ? 0 : 1 / mass2.getMass();

                // Get the inverse inertia matrices
                DMatrix3 I1inv = new DMatrix3(); 
                OdeMath.dInvertMatrix3(I1inv, mass1.getI());
                DMatrix3 I2inv = new DMatrix3();
                OdeMath.dInvertMatrix3(I2inv, mass2.getI());

                DVector3 b1NewLinearVel = new DVector3();
                DVector3 b2NewLinearVel = new DVector3();
                DVector3 b1NewAngularVel = new DVector3(b1.getAngularVel());
                DVector3 b2NewAngularVel = new DVector3(b2.getAngularVel());

                double e = bodies.get(bodyIDs.get(b1.hashCode())).getRestitution() * bodies.get(bodyIDs.get(b2.hashCode())).getRestitution();

                for (int i = 0; i < collision.getContactPoints().size(); i++) {
                    DVector3 contactPoint = collision.getContactPoints().get(i);
                    DVector3 normal = collision.getNormals().get(i);
                    DVector3 r1 = contactPoint.reSub(b1.getPosition());
                    DVector3 r2 = contactPoint.reSub(b2.getPosition());

                    DVector3 relVel = calculateRelativeVelocityAt(b1, b2, contactPoint);

                    // Check if the bodies are moving away from each other
                    double velocityAlongNormal = relVel.dot(normal);
                    if (velocityAlongNormal > 0) {
                        continue;
                    }

                    // Calculate impulse, if one of the bodies is kinematic, we ignore it's contribution
                    double impulseScalar;
                    if (b1.isKinematic()) {
                        impulseScalar = calculateImpulseAt(relVel, normal, e, 0, invMass2, new DVector3(0, 0, 0), r2, I1inv, I2inv);
                    } else if (b2.isKinematic()) {
                        impulseScalar = calculateImpulseAt(relVel, normal, e, invMass1, r1, I1inv);
                    } else {
                        impulseScalar = calculateImpulseAt(relVel, normal, e, invMass2, r2, I2inv);
                    }
                    
                    // Apply impulses
                    DVector3 matrixRes = new DVector3();
                    DVector3 correction = normal.reScale(collision.getPenetrationDepth());

                    if (!b1.isKinematic()) { 
                        // Apply linear velocity impulseScalar
                        b1NewLinearVel.add(normal.reScale(- impulseScalar * invMass1));

                        // Apply angular impulseScalar
                        OdeMath.dMultiply0_133(matrixRes, r1.cross(normal.reScale(impulseScalar)), I1inv);
                        b1NewAngularVel.sub(matrixRes);

                        // Positional correction.
                        corrections.put(bodyIDs.get(b1.hashCode()), corrections.getOrDefault(bodyIDs.get(b1.hashCode()), new DVector3()).reSub(correction));
                    }   
                    
                    if (!b2.isKinematic()) {
                        // Apply linear velocity impulseScalar
                        b2NewLinearVel.add(normal.reScale(impulseScalar * invMass2));

                        // Apply angular impulseScalar
                        OdeMath.dMultiply0_133(matrixRes, r2.cross(normal.reScale(impulseScalar)), I2inv);
                        b1NewAngularVel.add(matrixRes);
                        
                        // Positional correction.
                        corrections.put(bodyIDs.get(b2.hashCode()), corrections.getOrDefault(bodyIDs.get(b2.hashCode()), new DVector3()).reAdd(correction));
                    }          
                }

                b1.addLinearVel(b1NewLinearVel);
                b2.addLinearVel(b2NewLinearVel);

                b1.setAngularVel(b1NewAngularVel);
                b2.setAngularVel(b2NewAngularVel);
            });                
        });

        // Apply positional corrections
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
    }

    /**
     * Calculate the relative velocity at a contact point
     * @param lin1 Linear velocity of body 1
     * @param ang1 Angular velocity of body 1
     * @param lin2 Linear velocity of body 2
     * @param ang2 Angular velocity of body 2
     * @param com1 Center of mass of body 1
     * @param com2 Center of mass of body 2
     * @param contactPoint Point of contact
     * @return Relative velocity at the contact point
     */
    private DVector3 calculateRelativeVelocityAt(DBody b1, DBody b2, DVector3C contactPoint) {
        DVector3 vel1 = new DVector3();
        b1.getPointVel(contactPoint, vel1);
        DVector3 vel2 = new DVector3();
        b2.getPointVel(contactPoint, vel2);
        return vel2.reSub(vel1);
    }

    /**
     * Calculate the impulse at a contact point between two bodies
     * @param relVel Relative velocity at the contact point
     * @param normal Normal of the contact point
     * @param e Combined restitution of the two bodies
     * @param invMass1 Inverse mass of body 1
     * @param invMass2 Inverse mass of body 2
     * @param r1 Vector from the center of mass of body 1 to the contact point
     * @param r2 Vector from the center of mass of body 2 to the contact point
     * @param I1inv Inverse inertia matrix of body 1
     * @param I2inv Inverse inertia matrix of body 2
     * @return Impulse scalar
     */
    private double calculateImpulseAt(DVector3C relVel, DVector3C normal, double e, double invMass1, double invMass2, DVector3C r1, DVector3C r2, DMatrix3C I1inv, DMatrix3C I2inv) {
        DVector3 matrixMulPortion1 = new DVector3();
        OdeMath.dMultiply0_133(matrixMulPortion1, r1.cross(normal), I1inv);

        DVector3 matrixMulPortion2 = new DVector3();
        OdeMath.dMultiply0_133(matrixMulPortion2, r2.cross(normal), I2inv);

        double numerator = -(1 + e) * relVel.dot(normal);
        double denominator = (invMass1 + invMass2) + (matrixMulPortion1.cross(r1).reAdd(matrixMulPortion2.cross(r2))).dot(normal);
        return numerator / denominator;
    }

    /**
     * Calculate the impulse at a contact point for a single body
     * @param vel Velocity of the body
     * @param normal Normal of the contact point
     * @param e Restitution
     * @param invMass Inverse mass of the body
     * @param r1 Vector from the center of mass to the contact point
     * @param I1inv Inverse inertia matrix of the body
     * @return Impulse scalar
     */
    private double calculateImpulseAt(DVector3C vel, DVector3C normal, double e, double invMass, DVector3C r1, DMatrix3C I1inv) {
        DVector3 matrixMulPortion1 = new DVector3();
        OdeMath.dMultiply0_133(matrixMulPortion1, r1.cross(normal), I1inv);

        double numerator = -(1 + e) * vel.dot(normal);
        double denominator = invMass + matrixMulPortion1.cross(r1).dot(normal);
        return numerator / denominator;
    }
    
    public PhysicsBody getBody(int ID) {
        return bodies.get(ID);
    }

    public void addBody(PhysicsBody body) {
        bodies.put(body.getID(), body);
    }

    public void addCollider(PhysicsCollider collider) {
        space.add(collider.getCollider());
        colliders.put(collider.getID(), collider);
    }

    public PhysicsCollider getCollider(int ID) {
        return colliders.get(ID);
    }

    public PhysicsCollider addSphereCollider(int ID, float radius) {
        PhysicsCollider collider = new PhysicsCollider(ID, ColliderType.SPHERE, space);
        collider.setSphereRadius(radius);
        addCollider(collider);
        return collider;
    }

    public PhysicsCollider addBoxCollider(int ID, float x, float y, float z) {
        PhysicsCollider collider = new PhysicsCollider(ID, ColliderType.BOX, space);
        collider.setBoxDimensions(x, y, z);
        addCollider(collider);
        return collider;
    }

    public PhysicsCollider addCapsuleCollider(int ID, float radius, float length) {
        PhysicsCollider collider = new PhysicsCollider(ID, ColliderType.CAPSULE, space);
        collider.setCapsuleDimensions(radius, length);
        addCollider(collider);
        return collider;
    }

    public PhysicsBody addBody(int ID, boolean isKinematic) {
        PhysicsBody body = new PhysicsBody(ID, isKinematic, world);
        addBody(body);
        return body;
    }

    public boolean linkBodyAndCollider(int bodyID, int colliderID) {
        PhysicsBody body = getBody(bodyID);
        PhysicsCollider collider = getCollider(colliderID);
        if (body == null || collider == null) {
            return false;
        }
        DGeom geom = collider.getCollider();
        geom.setBody(body.getBody());
        return true;
    }

    public List<PhysicsBody> getBodies() {
        return bodies.values().stream().collect(Collectors.toList());
    }

    public List<PhysicsCollider> getColliders() {
        return colliders.values().stream().collect(Collectors.toList());
    }
}
