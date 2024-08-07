package io.github.kuggek.engine.rendering.objects.lights;

import org.joml.Vector3f;

public interface DirectionalLight extends Light {
    public Vector3f getDirection();
}
