package io.github.kuggek.engine.ecs.components.rendering.lights;

import org.joml.Vector3f;

import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

public class DirectionalLightComponent extends LightComponent implements DirectionalLight {

    @EditableComponentField
    private Vector3f direction;

    @EditableComponentField
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
    protected void onAwake(EngineRuntimeSettings settings) {
        if (activeOnStart) {
            settings.getRenderingSettings().setDirectionalLight(this);
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
