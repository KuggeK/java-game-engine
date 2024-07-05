package kugge.rendering.core.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DQuaternion;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;
import org.ode4j.ode.internal.DxMass;

/**
 * PhysicsBody
 */
public class PhysicsBody {
    private int ID;
    private DBody body;
    private boolean isKinematic = false;
    private double restitution;
    private double mass;

    public PhysicsBody(int ID, boolean isKinematic, DWorld world) {
        this.ID = ID;
        this.body = OdeHelper.createBody(world);
        this.isKinematic = isKinematic;
        if (isKinematic) {
            body.setKinematic();
        } else {
            body.setDynamic();
        }
        this.restitution = 0.5;
        this.mass = 1;
    }

    public int getID() {
        return ID;
    }

    public DBody getBody() {
        return body;
    }

    public Vector3f getPosition() {
        DVector3C pos = body.getPosition();
        return new Vector3f((float) pos.get0(), (float) pos.get1(), (float) pos.get2());
    }

    public Vector3f getPosition(Vector3f dest) {
        DVector3C pos = body.getPosition();
        return dest.set((float) pos.get0(), (float) pos.get1(), (float) pos.get2());
    }

    public void setPosition(Vector3f position) {
        body.setPosition(position.x, position.y, position.z);
    }

    public void setPosition(float x, float y, float z) {
        body.setPosition(x, y, z);
    }

    public Quaternionf getRotation() {
        return getRotation(new Quaternionf());
    }

    public Quaternionf getRotation(Quaternionf dest) {
        DQuaternionC quat = body.getQuaternion();
        dest.set((float)quat.get1(), (float)quat.get2(), (float)quat.get3(), (float)quat.get0());
        return dest;
    }

    public void setRotation(Quaternionf rot) {
        DQuaternion quat = new DQuaternion(rot.w, rot.x, rot.y, rot.z);
        body.setQuaternion(quat);
    }

    public void setRotation(float x, float y, float z, float w) {
        setRotation(new Quaternionf(x, y, z, w));
    }

    public boolean isKinematic() {
        return isKinematic;
    }

    public void setKinematic(boolean isKinematic) {
        this.isKinematic = isKinematic;
        if (isKinematic) {
            body.setKinematic();
        } else {
            body.setDynamic();
        }
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        // Setting the mass on a kinematic body makes it dynamic in ODE.
        if (isKinematic) {
            return;
        }
        
        DxMass massData = (DxMass)body.getMass();
        massData.setMass(mass);
        body.setMass(massData);
    }
}