package kugge.engine.rendering.objects.lights;

import org.joml.Vector3f;

public interface PositionalLight extends Light {

    public Vector3f getPosition();

    public float getConstant();

    public float getLinear();

    public float getQuadratic();

    public float getRadius();
}
