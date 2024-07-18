package kugge.rendering.core.objects;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Transform {
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;

    private Vector3f helperVector;

    private Matrix4f modelMatrix;
    private boolean modelMatrixChanged;

    public Transform() {
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
        helperVector = new Vector3f();
        modelMatrix = new Matrix4f();
        modelMatrixChanged = true;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        modelMatrixChanged = true;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        modelMatrixChanged = true;
    }

    public void setPosition(Vector4f position) {
        this.position.set(position.x, position.y, position.z);
        modelMatrixChanged = true;
    }

    public void setPosition(float[] position) {
        this.position.set(position[0], position[1], position[2]);
        modelMatrixChanged = true;
    }

    public void setRotation(float x, float y, float z) {
        rotation.identity().rotateXYZ(x, y, z);
        modelMatrixChanged = true;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.identity().rotateXYZ(rotation.x, rotation.y, rotation.z);
        modelMatrixChanged = true;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
        modelMatrixChanged = true;
    }

    public void setRotation(float[] rotation) {
        this.rotation.set(rotation[0], rotation[1], rotation[2], rotation[3]);
        modelMatrixChanged = true;
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        modelMatrixChanged = true;
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        modelMatrixChanged = true;
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        modelMatrixChanged = true;
    }

    public void rotate(float x, float y, float z) {
        rotation.rotateXYZ(x, y, z);
        modelMatrixChanged = true;
    }

    public void scale(float x, float y, float z) {
        scale.mul(x, y, z);
        modelMatrixChanged = true;
    }

    public void translate(Vector3f translation) {
        position.add(translation);
        modelMatrixChanged = true;
    }

    public void rotate(Vector3f rotation) {
        this.rotation.rotateXYZ(rotation.x, rotation.y, rotation.z);
        modelMatrixChanged = true;
    }

    public void scale(Vector3f scale) {
        this.scale.mul(scale);
        modelMatrixChanged = true;
    }

    /**
     * Moves the object towards the specified direction by the specified distance
     * @param xDir
     * @param yDir
     * @param zDir
     * @param distance
     */
    public void moveTowards(Vector3f direction, float distance) {
        helperVector.set(direction).normalize().mul(distance);
        position.add(helperVector);
        modelMatrixChanged = true;
    }

    public void moveTowards(float xDir, float yDir, float zDir, float distance) {
        moveTowards(new Vector3f(xDir, yDir, zDir), distance);
    }

    public Vector3f getForward() {
        return new Vector3f(0, 0, -1).rotate(rotation);
    }

    public Vector3f getRight() {
        return new Vector3f(1, 0, 0).rotate(rotation);
    }

    public Vector3f getUp() {
        return new Vector3f(0, 1, 0).rotate(rotation);
    }

    public Matrix4f getModelMatrix() {
        if (modelMatrixChanged) {
            // Model matrix is calculated as translation * rotation * scale
            modelMatrix.identity().translate(position).rotate(rotation).scale(scale);
            modelMatrixChanged = false;
        }
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void lookAt(Vector3f target) {
        Vector3f forward = new Vector3f(target).sub(position).normalize();
        Vector3f right = new Vector3f(0, 1, 0).cross(forward).normalize();
        Vector3f up = new Vector3f(forward).cross(right).normalize();
        rotation.identity().lookAlong(forward, up);
        modelMatrixChanged = true;
    }

    public void set(Transform transform) {
        this.position.set(transform.position);
        this.rotation.set(transform.rotation);
        this.scale.set(transform.scale);
        modelMatrixChanged = true;
    }
}
