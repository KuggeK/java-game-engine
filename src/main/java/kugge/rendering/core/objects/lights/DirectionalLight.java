package kugge.rendering.core.objects.lights;

import org.joml.Vector4f;

public class DirectionalLight extends Light {

    private Vector4f direction;

    public DirectionalLight(float[] ambient, float[] diffuse, float[] specular, float[] direction) {
        super(ambient, diffuse, specular);
        this.direction = new Vector4f(direction);
    }

    public DirectionalLight() {
        super();
        direction = new Vector4f(0.5f, -1.0f, -1.0f, 0.0f);
    }

    public Vector4f getDirection() {
        return direction;
    }

    public void setDirection(Vector4f direction) {
        this.direction = direction;
    }
}
