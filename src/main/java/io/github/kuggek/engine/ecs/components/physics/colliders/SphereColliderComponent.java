package io.github.kuggek.engine.ecs.components.physics.colliders;

import org.joml.Vector3f;
import org.ode4j.ode.DSphere;

import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

public class SphereColliderComponent extends PhysicsColliderComponent {

    @EditableComponentField
    private float radius;

    public SphereColliderComponent() {
        super(ColliderType.SPHERE);
    }

    @Override
    public void setCollider(ColliderType colliderType) {
        throw new UnsupportedOperationException("Cannot change collider type of SphereColliderComponent");
    }

    public void setRadius(float radius) {
        ((DSphere) getCollider()).setRadius(radius);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public void setScale(Vector3f scale) {
        setRadius(scale.x);
    }

    @Override
    protected void onAwake(EngineRuntimeSettings settings) {
        setRadius(radius);
    }
}
