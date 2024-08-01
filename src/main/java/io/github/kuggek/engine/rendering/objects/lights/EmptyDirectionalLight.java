package io.github.kuggek.engine.rendering.objects.lights;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class EmptyDirectionalLight implements DirectionalLight {

    private static final float[] ambient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] diffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] specular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] direction = new float[] {0.0f, 0.0f, 0.0f};

    @Override
    public Vector4f getAmbient() {
        return new Vector4f(ambient);
    }

    @Override
    public Vector4f getDiffuse() {
        return new Vector4f(diffuse);
    }

    @Override
    public Vector4f getSpecular() {
        return new Vector4f(specular);
    }

    @Override
    public Vector3f getDirection() {
        return new Vector3f(direction);
    }    
}
