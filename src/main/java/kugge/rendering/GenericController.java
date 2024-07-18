package kugge.rendering;

import org.joml.Vector3f;

import java.awt.event.KeyEvent;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.objects.ComponentField;
import kugge.rendering.core.scripting.Script;

public class GenericController extends Script {
    
    @ComponentField
    private float velocity = 0.01f;

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
            Vector3f forward = transform.getForward();
            transform.moveTowards(forward, forwardVelocity);
        }
        if (horizontalVelocity != 0) {          
            horizontalVelocity *= deltaTime;
            Vector3f right = transform.getRight();
            right.y = 0;
            right.normalize();
            transform.moveTowards(right, horizontalVelocity);
        } 
        if (verticalVelocity != 0) {
            verticalVelocity *= deltaTime;
            Vector3f up = new Vector3f(0, 1, 0);
            transform.moveTowards(up, verticalVelocity);
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
            transform.rotate(xRot * deltaTime, 0, 0);
        }

        if (yRot != 0) {
            // When rotating the camera we want to rotate the camera around the 
            // y-axis of the world, not the y-axis of the camera.
            transform.getRotation().rotateLocalY(yRot * deltaTime);
        }
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public void start() {
        System.out.println("GenericController started");
    }
}
