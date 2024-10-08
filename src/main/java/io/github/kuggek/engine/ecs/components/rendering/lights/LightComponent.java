package io.github.kuggek.engine.ecs.components.rendering.lights;

import org.joml.Vector4f;

import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.rendering.objects.lights.Light;

public class LightComponent extends GameComponent implements Light {
     
    @EditableComponentField
    protected Vector4f ambient;

    @EditableComponentField
    protected Vector4f diffuse;

    @EditableComponentField
    protected Vector4f specular;

    public LightComponent(float[] ambient, float[] diffuse, float[] specular) {
        this.ambient = new Vector4f(ambient);
        this.diffuse = new Vector4f(diffuse);
        this.specular = new Vector4f(specular);
    }

    public LightComponent() {
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
