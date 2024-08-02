package io.github.kuggek.engine.ecs.components.rendering.lights;

import org.joml.Vector3f;

import io.github.kuggek.engine.ecs.components.ComponentField;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.subsystems.SubsystemSettings;

public class DirectionalLightComponent extends LightComponent implements DirectionalLight {

    @ComponentField
    private Vector3f direction;

    @ComponentField
    private boolean activeOnStart = false;

    public DirectionalLightComponent(float[] ambient, float[] diffuse, float[] specular, float[] direction) {
        super(ambient, diffuse, specular);
        this.direction = new Vector3f(direction);
    }

    public DirectionalLightComponent() {
        super();
        direction = new Vector3f(0.5f, -1.0f, -1.0f).normalize();
    }

    @Override
    protected void onAwake(SubsystemSettings settings) {
        if (activeOnStart) {
            settings.setDirectionalLight(this);
        }
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction).normalize();
    }

    public boolean isActiveOnStart() {
        return activeOnStart;
    }
}
