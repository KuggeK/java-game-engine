package io.github.kuggek.engine.ecs.components.rendering.lights;

import org.joml.Vector3f;

import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;

public class PositionalLightComponent extends LightComponent implements PositionalLight {
    
    @EditableComponentField
    private float constant;

    @EditableComponentField
    private float linear;

    @EditableComponentField
    private float quadratic;

    @EditableComponentField
    private float radius;

    public PositionalLightComponent() {
        super();
        constant = 1.0f;
        linear = 0.09f;
        quadratic = 0.032f;
        radius = 10.0f;
    }

    public PositionalLightComponent(float[] ambient, float[] diffuse, float[] specular) {
        super(ambient, diffuse, specular);
        constant = 1.0f;
        linear = 0.09f;
        quadratic = 0.032f;
        radius = 10.0f;
    }

    public Vector3f getPosition() {
        return transform.getPosition();
    }

    public void setPosition(Vector3f position) {
        transform.setPosition(position);
    }
    
    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
