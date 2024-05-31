package kugge.rendering.core.objects;

import org.joml.Matrix4f;

public class Camera {
    
    private float[] position;
    private float[] forward;
    private float fov;
    private float near;
    private float far;

    public Camera() {
        this(60f, 0.01f, 1000);
    }

    public Camera(float fov, float near, float far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
        position = new float[] {0, 0, 7};
        forward = new float[] {0, 0, -1};
    }

    public Matrix4f getViewMatrix() {
        // TODO Implement an actual view matrix, this is just a simple translation as a placeholder.
        return new Matrix4f().translation(-position[0], -position[1], -position[2]);
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }
}
