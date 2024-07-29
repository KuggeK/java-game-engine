package kugge.engine.ecs.components.rendering.lights;

import org.joml.Vector3f;

import kugge.engine.ecs.components.ComponentField;
import kugge.engine.rendering.objects.lights.DirectionalLight;

public class DirectionalLightComponent extends LightComponent implements DirectionalLight {

    @ComponentField
    private Vector3f direction;

    public DirectionalLightComponent(float[] ambient, float[] diffuse, float[] specular, float[] direction) {
        super(ambient, diffuse, specular);
        this.direction = new Vector3f(direction);
    }

    public DirectionalLightComponent() {
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
