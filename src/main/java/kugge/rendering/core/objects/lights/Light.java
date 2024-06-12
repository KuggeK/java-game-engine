package kugge.rendering.core.objects.lights;

import org.joml.Vector4f;

public class Light {
    
    protected Vector4f ambient;
    protected Vector4f diffuse;
    protected Vector4f specular;

    public Light(float[] ambient, float[] diffuse, float[] specular) {
        this.ambient = new Vector4f(ambient);
        this.diffuse = new Vector4f(diffuse);
        this.specular = new Vector4f(specular);
    }

    public Light() {
        this(
            new float[] {0.5f, 0.5f, 0.5f, 1.0f},
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {1.0f, 1.0f, 1.0f, 1.0f}
        );
    }

    public Vector4f getAmbient() {
        return ambient;
    }
    public void setAmbient(Vector4f ambient) {
        this.ambient = ambient;
    }
    public Vector4f getDiffuse() {
        return diffuse;
    }
    public void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
    }
    public Vector4f getSpecular() {
        return specular;
    }
    public void setSpecular(Vector4f specular) {
        this.specular = specular;
    }
}
