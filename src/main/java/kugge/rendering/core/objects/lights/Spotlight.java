package kugge.rendering.core.objects.lights;

import org.joml.Vector4f;

public class Spotlight extends PositionalLight {

    private Vector4f direction;
    private float cutoff;
    private float exponent;
    
    public Spotlight(float[] ambient, float[] diffuse, float[] specular, float[] position, float[] direction, float cutoff, float exponent) {
        super(ambient, diffuse, specular, position);
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
