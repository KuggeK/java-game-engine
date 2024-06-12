package kugge.rendering.core.objects.lights;

import org.joml.Vector4f;

public class PositionalLight extends Light {

    private Vector4f position;
    private float constant;
    private float linear;
    private float quadratic;
    private float radius;

    public PositionalLight(float[] ambient, float[] diffuse, float[] specular, float[] position) {
        super(ambient, diffuse, specular);
        this.position = new Vector4f(position);
        constant = 1.0f;
        linear = 0.09f;
        quadratic = 0.032f;
        radius = 10.0f;
    }

    public Vector4f getPosition() {
        return position;
    }

    public void setPosition(Vector4f position) {
        this.position = position;
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
