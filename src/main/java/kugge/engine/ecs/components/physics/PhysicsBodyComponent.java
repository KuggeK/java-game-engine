package kugge.engine.ecs.components.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DQuaternion;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;
import org.ode4j.ode.internal.DxMass;

import kugge.engine.ecs.GameComponent;
import kugge.engine.ecs.components.ComponentField;
import kugge.engine.physics.PhysicsBody;

public class PhysicsBodyComponent extends GameComponent implements PhysicsBody {
    @ComponentField
    private int ID;

    @ComponentField
    private boolean isKinematic = false;

    @ComponentField
    private double restitution;
    
    @ComponentField
    private double mass;
    
    @ComponentField
    private int colliderID;

    @ComponentField
    private boolean influencedByGravity;

    private DBody body;

    public PhysicsBodyComponent(int ID, boolean isKinematic) {
        super();
        this.ID = ID;
        this.isKinematic = isKinematic;
        this.restitution = 0.5;
        this.mass = 1;
        this.influencedByGravity = !isKinematic;
    }

    public int getID() {
        return ID;
    }

    public DBody getBody() {
        return body;
    }

    public int getColliderID() {
        return colliderID;
    }

    /**
     * Get whether the physics body is influenced by gravity or not. 
     * Note that this only has an effect if the body is not kinematic.
     * @return Whether the physics body is influenced by gravity or not.
     */
    public boolean isInfluencedByGravity() {
        return influencedByGravity;
    }

    /**
     * Set whether the physics body is influenced by gravity or not. 
     * This only has an effect if the body is not kinematic.
     * @param influencedByGravity Whether the physics body should be influenced by gravity or not.
     */
    public void setInfluencedByGravity(boolean influencedByGravity) {
        body.setGravityMode(influencedByGravity);
        this.influencedByGravity = influencedByGravity;
    }

    /**
     * Get the position of the physics body, which may differ from the position of the game object.
     * @return The position of the physics body.
     */
    public Vector3f getPhysPosition() {
        DVector3C pos = body.getPosition();
        return new Vector3f((float) pos.get0(), (float) pos.get1(), (float) pos.get2());
    }

    /**
     * Stores the position of the physics body, which may differ from the position of the game object,
     * into the given destination vector.
     * @param dest The destination vector to store the position in.
     * @return The destination vector.
     */
    public Vector3f getPhysPosition(Vector3f dest) {
        DVector3C pos = body.getPosition();
        return dest.set((float) pos.get0(), (float) pos.get1(), (float) pos.get2());
    }

    /**
     * Set the position of the physics body, which may differ from the position of the game object.
     * @param position The position to set.
     */
    public void setPhysPosition(Vector3f position) {
        body.setPosition(position.x, position.y, position.z);
    }

    /**
     * Set the position of the physics body, which may differ from the position of the game object.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @param z The z-coordinate of the position.
     */
    public void setPhysPosition(float x, float y, float z) {
        body.setPosition(x, y, z);
    }

    /**
     * Get the rotation of the physics body, which may differ from the rotation of the game object.
     * @return
     */
    public Quaternionf getPhysRotation() {
        return getPhysRotation(new Quaternionf());
    }

    /**
     * Stores the rotation of the physics body, which may differ from the rotation of the game object,
     * into the given destination quaternion.
     * @param dest The destination quaternion to store the rotation in.
     * @return The destination quaternion.
     */
    public Quaternionf getPhysRotation(Quaternionf dest) {
        DQuaternionC quat = body.getQuaternion();
        dest.set((float)quat.get1(), (float)quat.get2(), (float)quat.get3(), (float)quat.get0());
        return dest;
    }

    /**
     * Set the rotation of the physics body, which may differ from the rotation of the game object.
     * @param rot The rotation to set.
     */
    public void setPhysRotation(Quaternionf rot) {
        DQuaternion quat = new DQuaternion(rot.w, rot.x, rot.y, rot.z);
        body.setQuaternion(quat);
    }

    /**
     * Set the rotation of the physics body, which may differ from the rotation of the game object.
     * @param x The x-coordinate of the rotation.
     * @param y The y-coordinate of the rotation.
     * @param z The z-coordinate of the rotation.
     * @param w The w-coordinate of the rotation.
     */
    public void setPhysRotation(float x, float y, float z, float w) {
        setPhysRotation(new Quaternionf(x, y, z, w));
    }

    /**
     * Returns whether the physics body is kinematic or not.
     * @return True if the physics body is kinematic, false otherwise.
     */
    public boolean isKinematic() {
        return isKinematic;
    }

    /**
     * Set whether the physics body is kinematic or not.
     * @param isKinematic True if the physics body should be kinematic, false otherwise.
     */
    public void setKinematic(boolean kinematic) {
        this.isKinematic = kinematic;
        if (isKinematic) {
            body.setKinematic();
        } else {
            body.setDynamic();
        }
        setMass(mass);
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

    /**
     * Set the mass of the physics body. This only has an effect if the body is not kinematic 
     * because kinematic bodies are treated as having infinite mass. 
     * The mass will however be stored and used if the body is later set to be dynamic.
     * @param mass The mass to set.
     */
    public void setMass(double newMass) {
        this.mass = newMass;

        // Setting the mass on a kinematic body makes it dynamic in ODE.
        if (isKinematic) {
            return;
        }

        if (body == null) {
            return;
        }
        
        DxMass odeMass = (DxMass)body.getMass();
        odeMass.setMass(newMass);
        body.setMass(odeMass);
    }

    /**
     * Synchronize the position and rotation of the physics body to the position and rotation of the game object.
     */
    public void syncToGameObject() {
        setPhysPosition(transform.getPosition());
        setPhysRotation(transform.getRotation());
    }

    /**
     * Synchronize the position and rotation of the game object to the position and rotation of the physics body.
     */
    public void syncToPhysicsBody() {
        transform.setPosition(getPhysPosition());
        transform.setRotation(getPhysRotation());
    }

    /**
     * Link the physics body to the given ODE world so that it can participate in the physics simulation.
     * @param this The physics body to link.
     * @param world The ODE world to link to.
     */
    public void linkToWorld(DWorld world) {
        DBody odeBody = OdeHelper.createBody(world);
        this.body = odeBody;
        this.body.setData(this);
        
        // Update the properties of the ODE body to match the physics body.
        this.setMass(this.getMass());
        if (this.isKinematic) {
            odeBody.setKinematic();
        } else {
            odeBody.setDynamic();
        }
        odeBody.setGravityMode(this.influencedByGravity);

        this.syncToGameObject();
    }

    public void setLinearVel(float x, float y, float z) {
        body.setLinearVel(x, y, z);
    }
    public void setLinearVel(Vector3f vel) {
        body.setLinearVel(vel.x, vel.y, vel.z);
    }
    public void addLinearVel(float x, float y, float z) {
        body.addLinearVel(x, y, z);
    }
    public void addLinearVel(Vector3f vel) {
        body.addLinearVel(vel.x, vel.y, vel.z);
    }
    public void setAngularVel(float x, float y, float z) {
        body.setAngularVel(x, y, z);
    }
    public void setAngularVel(Vector3f vel) {
        body.setAngularVel(vel.x, vel.y, vel.z);
    }
    public void addAngularVel(float x, float y, float z) {
        DVector3C currentVel = body.getAngularVel();
        body.setAngularVel(currentVel.get0() + x, currentVel.get1() + y, currentVel.get2() + z);
    }
    public void addAngularVel(Vector3f vel) {
        DVector3C currentVel = body.getAngularVel();
        body.setAngularVel(currentVel.get0() + vel.x, currentVel.get1() + vel.y, currentVel.get2() + vel.z);
    }
    public void setLinearDamping(float damping) {
        body.setLinearDamping(damping);
    }
    public void setAngularDamping(float damping) {
        body.setAngularDamping(damping);
    }

    public Vector3f getLinearVel() {
        DVector3C vel = body.getLinearVel();
        return new Vector3f((float) vel.get0(), (float) vel.get1(), (float) vel.get2());
    }

    public Vector3f getLinearVel(Vector3f dest) {
        DVector3C vel = body.getLinearVel();
        return dest.set((float) vel.get0(), (float) vel.get1(), (float) vel.get2());
    }

    public Vector3f getAngularVel() {
        DVector3C vel = body.getAngularVel();
        return new Vector3f((float) vel.get0(), (float) vel.get1(), (float) vel.get2());
    }

    public Vector3f getAngularVel(Vector3f dest) {
        DVector3C vel = body.getAngularVel();
        return dest.set((float) vel.get0(), (float) vel.get1(), (float) vel.get2());
    }
}
