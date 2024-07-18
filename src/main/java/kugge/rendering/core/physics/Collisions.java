package kugge.rendering.core.physics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ode4j.math.DMatrix3;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DBox;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMassC;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DSphere;
import org.ode4j.ode.OdeMath;

public class Collisions {

    public static Map<Integer, DVector3> collideSpace(DSpace space) {
        Map<Integer, DVector3> corrections = new HashMap<>();
        space.collide(null, (_data, o1, o2) -> {
            // Get collision info
            Optional<Collision> potentialCollision = Collisions.collide(o1, o2);

            if (potentialCollision.isEmpty()) {
                return;
            }

            // Call onCollision for both colliders
            PhysicsCollider collider1 = (PhysicsCollider) o1.getData();
            PhysicsCollider collider2 = (PhysicsCollider) o2.getData();
               
            if (collider1 != null) {
                collider1.onCollision(collider2);
            }         
            if (collider2 != null) {
                collider2.onCollision(collider1);
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

            PhysicsBody body1 = (PhysicsBody) b1.getData();
            PhysicsBody body2 = (PhysicsBody) b2.getData();

            // Calculate the coefficient of restitution
            double e = body1.getRestitution() * body2.getRestitution();

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
                    corrections.put(body1.getID(), corrections.getOrDefault(body1.getID(), new DVector3()).reSub(correction));
                }   
                
                if (!b2.isKinematic()) {
                    // Apply linear velocity impulseScalar
                    b2NewLinearVel.add(normal.reScale(impulseScalar * invMass2));

                    // Apply angular impulseScalar
                    OdeMath.dMultiply0_133(matrixRes, r2.cross(normal.reScale(impulseScalar)), I2inv);
                    b1NewAngularVel.add(matrixRes);
                    
                    // Positional correction.
                    corrections.put(body2.getID(), corrections.getOrDefault(body2.getID(), new DVector3()).reAdd(correction));
                }          
            }

            b1.addLinearVel(b1NewLinearVel);
            b2.addLinearVel(b2NewLinearVel);

            b1.setAngularVel(b1NewAngularVel);
            b2.setAngularVel(b2NewAngularVel);             
        });

        return corrections;
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
    private static DVector3 calculateRelativeVelocityAt(DBody b1, DBody b2, DVector3C contactPoint) {
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
    private static double calculateImpulseAt(DVector3C relVel, DVector3C normal, double e, double invMass1, double invMass2, DVector3C r1, DVector3C r2, DMatrix3C I1inv, DMatrix3C I2inv) {
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
    private static double calculateImpulseAt(DVector3C vel, DVector3C normal, double e, double invMass, DVector3C r1, DMatrix3C I1inv) {
        DVector3 matrixMulPortion1 = new DVector3();
        OdeMath.dMultiply0_133(matrixMulPortion1, r1.cross(normal), I1inv);

        double numerator = -(1 + e) * vel.dot(normal);
        double denominator = invMass + matrixMulPortion1.cross(r1).dot(normal);
        return numerator / denominator;
    }

    /**
     * Check for collision between two geometries. 
     * @param geom1 Geometry 1
     * @param geom2 Geometry 2
     * @return Empty if no collision, otherwise contanins a Collision object with the details of the collision.
     */
    public static Optional<Collision> collide(DGeom geom1, DGeom geom2) {
        if (geom1 instanceof DSphere && geom2 instanceof DSphere) {
            return collideSphereToSphere((DSphere) geom1, (DSphere) geom2);
        } 
        else if (geom1 instanceof DSphere && geom2 instanceof DBox) {
            return collideSphereToBox((DSphere) geom1, (DBox) geom2);
        } 
        else if (geom1 instanceof DBox && geom2 instanceof DSphere) {
            return collideSphereToBox((DSphere) geom2, (DBox) geom1);
        } 
        else if (geom1 instanceof DBox && geom2 instanceof DBox) {
            return collideBoxToBox((DBox) geom1, (DBox) geom2);
        } 
        else {
            return Optional.empty();
        }
    }

    /**
     * Sphere to sphere collision.
     * @param sphere1
     * @param sphere2
     * @return
     */
    public static Optional<Collision> collideSphereToSphere(DSphere sphere1, DSphere sphere2) {
        if (sphere1.getRadius() + sphere2.getRadius() < sphere1.getPosition().distance(sphere2.getPosition())) {
            return Optional.empty();
        } // No collision (spheres are too far apart)

        DVector3C pos1 = sphere1.getPosition();
        DVector3C pos2 = sphere2.getPosition();
        double radius1 = sphere1.getRadius();
        double radius2 = sphere2.getRadius();

        DVector3 n = new DVector3();
        n.eqDiff(pos2, pos1);
        n.normalize();

        DVector3 contactPoint = new DVector3();
        contactPoint.eqSum(pos1, n.scale(radius1));

        double penetrationDepth = Math.abs(radius1 + radius2 - pos1.distance(pos2)); 

        return Optional.of(new Collision(sphere1, sphere2, List.of(contactPoint), List.of(n), penetrationDepth));
    }

    /**
     * Sphere to box collision.
     * @param sphere
     * @param box
     * @return
     */
    public static Optional<Collision> collideSphereToBox(DSphere sphere, DBox box) {
        // Transform the sphere's position to the box's local space
        DVector3C spherePos = sphere.getPosition();
        DVector3C rPos = box.getPosition();
        DMatrix3C rRot = box.getRotation();
        DVector3 localSpherePos = spherePos.reSub(rPos);
        OdeMath.dMultiply1_331(localSpherePos, rRot, localSpherePos);

        // Get the closest point on the box to the sphere.
        DVector3 closestPoint = new DVector3();
        DVector3C lengths = box.getLengths();
        double halfLengthX = lengths.get0() / 2;
        double halfLengthY = lengths.get1() / 2;
        double halfLengthZ = lengths.get2() / 2;
        closestPoint.set(0, Math.max(-halfLengthX, Math.min(localSpherePos.get0(), halfLengthX)));
        closestPoint.set(1, Math.max(-halfLengthY, Math.min(localSpherePos.get1(), halfLengthY)));
        closestPoint.set(2, Math.max(-halfLengthZ, Math.min(localSpherePos.get2(), halfLengthZ)));

        // Transform the closest point back to world space
        OdeMath.dMultiply0_331(closestPoint, rRot, closestPoint);
        
        closestPoint.eqSum(closestPoint, rPos);

        if (closestPoint.distance(spherePos) > sphere.getRadius()) {
            return Optional.empty();
        } // No collision (sphere is too far from box)

        DVector3 n = spherePos.reSub(closestPoint);
        // The DVector3 function normalize() will throw an exception if the vector is zero
        if (n.length() == 0) {
            System.out.println("Zero length vector");
            n.eqDiff(spherePos, box.getPosition());
        }
        n = n.normalize();

        double contactToSphereDistance = spherePos.reSub(closestPoint).length();
        double penetrationDepth = Math.abs(sphere.getRadius() - contactToSphereDistance);
        return Optional.of(new Collision(box, sphere, List.of(closestPoint), List.of(n), penetrationDepth));
    }

    /**
     * Box to box collision.
     * @param box1
     * @param box2
     * @return
     */
    public static Optional<Collision> collideBoxToBox(DBox box1, DBox box2) {
        System.err.println("Box to box collision not implemented yet.");
        return Optional.empty();
    }
}