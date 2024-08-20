package io.github.kuggek.engine.scripting;

import static java.awt.event.KeyEvent.*;

import io.github.kuggek.engine.ecs.components.ComponentField;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

public class GenericControllerScript extends Script {

    @ComponentField
    private float movementSpeed = 0.001f;
    
    @ComponentField
    private float rotationSpeed = 0.01f;

    @Override
    public void start(EngineRuntimeSettings settings) {
        
    }

    @Override
    public void update(KeyInput keyInput, float deltaTime, EngineRuntimeSettings settings) {
        float forwardVel = 0;

        if (keyInput.isKeyHeld(VK_W)) {
            forwardVel += movementSpeed;
        }
        if (keyInput.isKeyHeld(VK_S)) {
            forwardVel -= movementSpeed;
        }

        float rightVel = 0;

        if (keyInput.isKeyHeld(VK_D)) {
            rightVel += movementSpeed;
        }
        if (keyInput.isKeyHeld(VK_A)) {
            rightVel -= movementSpeed;
        }

        if (forwardVel != 0) {
            transform.moveTowards(transform.getForward(), forwardVel * deltaTime);
        }

        if (rightVel != 0) {
            transform.moveTowards(transform.getRight(), rightVel * deltaTime);
        }

        float rotY = 0;

        if (keyInput.isKeyHeld(VK_RIGHT)) {
            rotY -= rotationSpeed;
        }
        if (keyInput.isKeyHeld(VK_LEFT)) {
            rotY += rotationSpeed;
        }

        if (rotY != 0) {
            transform.setRotation(transform.getRotation().rotateLocalY(rotY));
        }

        float rotX = 0;

        if (keyInput.isKeyHeld(VK_UP)) {
            rotX += rotationSpeed;
        }
        if (keyInput.isKeyHeld(VK_DOWN)) {
            rotX -= rotationSpeed;
        }

        if (rotX != 0) {
            transform.setRotation(transform.getRotation().rotateX(rotX));
        }

        float horizontalVel = 0;

        if (keyInput.isKeyHeld(VK_SPACE)) {
            horizontalVel += movementSpeed;
        }

        if (keyInput.isKeyHeld(VK_SHIFT)) {
            horizontalVel -= movementSpeed;
        }

        if (horizontalVel != 0) {
            transform.moveTowards(transform.getUp(), horizontalVel * deltaTime);
        }

    }
    
}
