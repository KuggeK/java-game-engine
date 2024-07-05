package kugge.rendering;

import org.joml.Vector3f;

import java.awt.event.KeyEvent;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.objects.Camera;

public class CameraController implements Updateable {
    
    private Camera camera;
    private float velocity = 0.01f;

    public CameraController(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(KeyInput keyInput, float deltaTime) {
        float forwardVelocity = 0.0f;
        float horizontalVelocity = 0.0f;
        float verticalVelocity = 0.0f;

        // Forward and backward
        if (keyInput.isKeyHeld(KeyEvent.VK_W)) {
            forwardVelocity += velocity;
        } 
        if (keyInput.isKeyHeld(KeyEvent.VK_S)) {
            forwardVelocity -= velocity;
        }

        // Left and right
        if (keyInput.isKeyHeld(KeyEvent.VK_A)) {
            horizontalVelocity -= velocity;
        }
        if (keyInput.isKeyHeld(KeyEvent.VK_D)) {
            horizontalVelocity += velocity;
        }

        // Up and down
        if (keyInput.isKeyHeld(KeyEvent.VK_SPACE)) {
            verticalVelocity += velocity;
        }
        if (keyInput.isKeyHeld(KeyEvent.VK_SHIFT)) {
            verticalVelocity -= velocity;
        }

        if (forwardVelocity != 0) {
            forwardVelocity *= deltaTime;
            Vector3f forward = camera.getTransform().getForward();
            camera.getTransform().moveTowards(forward, forwardVelocity);
        }
        if (horizontalVelocity != 0) {          
            horizontalVelocity *= deltaTime;
            Vector3f right = camera.getTransform().getRight();
            right.y = 0;
            right.normalize();
            camera.getTransform().moveTowards(right, horizontalVelocity);
        } 
        if (verticalVelocity != 0) {
            verticalVelocity *= deltaTime;
            Vector3f up = new Vector3f(0, 1, 0);
            camera.getTransform().moveTowards(up, verticalVelocity);
        }

        // Rotation
        float rotationSpeed = 0.0015f;
        float xRot = 0;
        float yRot = 0;

        if (keyInput.isKeyHeld(KeyEvent.VK_UP)) {
            xRot += rotationSpeed;
        }
        if (keyInput.isKeyHeld(KeyEvent.VK_DOWN)) {
            xRot -= rotationSpeed;
        }

        if (keyInput.isKeyHeld(KeyEvent.VK_LEFT)) {
            yRot += rotationSpeed;
        }
        if (keyInput.isKeyHeld(KeyEvent.VK_RIGHT)) {
            yRot -= rotationSpeed;
        }

        if (xRot != 0) {
            camera.getTransform().rotate(xRot * deltaTime, 0, 0);
        }

        if (yRot != 0) {
            // When rotating the camera we want to rotate the camera around the 
            // y-axis of the world, not the y-axis of the camera.
            camera.getTransform().getRotation().rotateLocalY(yRot * deltaTime);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
