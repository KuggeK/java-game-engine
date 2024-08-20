package io.github.kuggek.engine.rendering.objects;

import org.joml.Matrix4f;

import io.github.kuggek.engine.core.Transform;

public class EmptyCamera implements Camera {

    @Override
    public boolean isDisabled() {
        return true;
    }

    @Override
    public Matrix4f getViewMatrix() {
        return new Matrix4f();
    }

    @Override
    public Matrix4f getProjectionMatrix(float aspectRatio) {
        return new Matrix4f();
    }

    @Override
    public float getFov() {
        return 60;
    }

    @Override
    public float getNear() {
        return 0.1f;
    }

    @Override
    public float getFar() {
        return 100;
    }

    @Override
    public boolean isOrthographic() {
        return false;
    }

    @Override
    public Transform getTransform() {
        return new Transform();
    }
    
}
