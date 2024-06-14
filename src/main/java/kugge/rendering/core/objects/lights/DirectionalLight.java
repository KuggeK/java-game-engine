package kugge.rendering.core.objects.lights;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

    private Vector3f direction;

    public DirectionalLight(float[] ambient, float[] diffuse, float[] specular, float[] direction) {
        super(ambient, diffuse, specular);
        this.direction = new Vector3f(direction);
    }

    public DirectionalLight() {
        super();
        direction = new Vector3f(0.5f, -1.0f, -1.0f).normalize();
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction).normalize();
    }
}
