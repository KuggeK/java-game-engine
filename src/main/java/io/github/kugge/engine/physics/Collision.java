package io.github.kugge.engine.physics;

import java.util.List;

import org.ode4j.math.DVector3;
import org.ode4j.ode.DGeom;

public class Collision {
    private DGeom geom1;
    private DGeom geom2;
    private List<DVector3> contactPoints;
    private List<DVector3> normals;
    private double penetrationDepth;

    public Collision(DGeom geom1, DGeom geom2, List<DVector3> contactPoints, List<DVector3> normals, double penetrationDepth) {
        this.geom1 = geom1;
        this.geom2 = geom2;
        this.contactPoints = contactPoints;
        this.normals = normals;
        this.penetrationDepth = penetrationDepth;
    }

    public DGeom getGeom1() {
        return geom1;
    }

    public void setGeom1(DGeom geom1) {
        this.geom1 = geom1;
    }

    public DGeom getGeom2() {
        return geom2;
    }

    public void setGeom2(DGeom geom2) {
        this.geom2 = geom2;
    }

    public List<DVector3> getContactPoints() {
        return contactPoints;
    }

    public List<DVector3> getNormals() {
        return normals;
    }

    public double getPenetrationDepth() {
        return penetrationDepth;
    }

    public void setNormals(List<DVector3> normals) {
        this.normals = normals;
    }
}
