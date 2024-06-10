package kugge.rendering.core.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    
    private Transform transform;
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
        transform = new Transform();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(
            transform.getPosition(),
            transform.getPosition().add(transform.getForward(), new Vector3f()),
            transform.getUp()
        );
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

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
