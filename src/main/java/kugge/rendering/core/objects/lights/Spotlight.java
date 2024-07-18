package kugge.rendering.core.objects.lights;

import org.joml.Vector4f;

import kugge.rendering.core.objects.ComponentField;

public class Spotlight extends PositionalLight {

    @ComponentField
    private Vector4f direction;

    @ComponentField
    private float cutoff;

    @ComponentField
    private float exponent;
    
    public Spotlight(float[] ambient, float[] diffuse, float[] specular, float[] direction, float cutoff, float exponent) {
        super(ambient, diffuse, specular);
        this.direction = new Vector4f(direction);
        this.cutoff = cutoff;
        this.exponent = exponent;
    }

    public Vector4f getDirection() {
        return direction;
    }

    public void setDirection(Vector4f direction) {
        this.direction = direction;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }    
}
