package kugge.rendering.core.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends GameComponent {
    @ComponentField
    private float fov;

    @ComponentField
    private float near;

    @ComponentField
    private float far;

    @ComponentField
    private boolean orthographic;

    public Camera() {
        super();
    }

    public Camera(GameObject gameObject) {
        this(gameObject, 70f, 0.01f, 1000, false);
    }

    public Camera(GameObject gameObject, float fov, float near, float far, boolean orthographic) {
        super(gameObject);
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.orthographic = orthographic;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(
            transform.getPosition(),
            transform.getPosition().add(transform.getForward(), new Vector3f()),
            transform.getUp()
        );
    }

    public Matrix4f getProjectionMatrix(float aspectRatio) {
        if (orthographic) {
            return new Matrix4f().ortho(-1, 1, -1, 1, near, far);
        } else {
            return new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
        }
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

    public boolean isOrthographic() {
        return orthographic;
    }

    public void setOrthographic(boolean orthographic) {
        this.orthographic = orthographic;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public record CameraFrustum(
        Vector3f nearTopLeft,
        Vector3f nearTopRight,
        Vector3f nearBottomLeft,
        Vector3f nearBottomRight,
        Vector3f farTopLeft,
        Vector3f farTopRight,
        Vector3f farBottomLeft,
        Vector3f farBottomRight,
        Vector3f nearCenter,
        Vector3f farCenter
    ) {
        public CameraFrustum toSpace(Matrix4f space) {
            return new CameraFrustum(
                new Vector3f(nearTopLeft).mulPosition(space),
                new Vector3f(nearTopRight).mulPosition(space),
                new Vector3f(nearBottomLeft).mulPosition(space),
                new Vector3f(nearBottomRight).mulPosition(space),
                new Vector3f(farTopLeft).mulPosition(space),
                new Vector3f(farTopRight).mulPosition(space),
                new Vector3f(farBottomLeft).mulPosition(space),
                new Vector3f(farBottomRight).mulPosition(space),
                new Vector3f(nearCenter).mulPosition(space),
                new Vector3f(farCenter).mulPosition(space)
            );
        }
    }

    
    /**
     * Calculates the camera frustum's corners in world space.
     * @return The camera frustum's corners
     */
    public CameraFrustum calculateFrustumCorners(float aspectRatio) {
        float near = this.getNear();
        float far = this.getFar();
        float fov = this.getFov();

        Vector3f camPos = new Vector3f(this.getTransform().getPosition());
        Vector3f camDir = new Vector3f(this.getTransform().getForward()).normalize();

        float nearHeight = (float) (2 * Math.tan(Math.toRadians(fov / 2)) * near);
        float nearWidth = nearHeight * aspectRatio;
        float farHeight = (float) (2 * Math.tan(Math.toRadians(fov / 2)) * far);
        float farWidth = farHeight * aspectRatio;

        Vector3f nearCenter = new Vector3f(camPos).add(camDir.mul(near, new Vector3f()));
        Vector3f farCenter = new Vector3f(camPos).add(camDir.mul(far, new Vector3f()));

        Vector3f nearUp = new Vector3f(this.getTransform().getUp()).mul(nearHeight / 2);
        Vector3f nearRight = new Vector3f(this.getTransform().getRight()).mul(nearWidth / 2);
        Vector3f farUp = new Vector3f(this.getTransform().getUp()).mul(farHeight / 2);
        Vector3f farRight = new Vector3f(this.getTransform().getRight()).mul(farWidth / 2);

        return new CameraFrustum(
            new Vector3f(nearCenter).add(nearUp).sub(nearRight), // Near top left
            new Vector3f(nearCenter).add(nearUp).add(nearRight), // Near top right
            new Vector3f(nearCenter).sub(nearUp).sub(nearRight), // Near bottom left
            new Vector3f(nearCenter).sub(nearUp).add(nearRight), // Near bottom right
            new Vector3f(farCenter).add(farUp).sub(farRight),    // Far top left
            new Vector3f(farCenter).add(farUp).add(farRight),    // Far top right
            new Vector3f(farCenter).sub(farUp).sub(farRight),    // Far bottom left
            new Vector3f(farCenter).sub(farUp).add(farRight),    // Far bottom right
            nearCenter,
            farCenter
        );
    }
}
