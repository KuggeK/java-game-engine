package kugge.rendering.core.physics;

import java.util.List;
import java.util.Optional;

import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBox;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSphere;
import org.ode4j.ode.OdeMath;

public class Collisions {

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