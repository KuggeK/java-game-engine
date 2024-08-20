package io.github.kuggek.engine.ecs.components.physics.colliders;

import org.joml.Vector3f;
import org.ode4j.ode.DBox;

import io.github.kuggek.engine.ecs.components.EditableComponentField;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

public class BoxColliderComponent extends PhysicsColliderComponent {
    
    @EditableComponentField
    private Vector3f scale;

    public BoxColliderComponent() {
        super(ColliderType.BOX);
        scale = new Vector3f(1);
    }

    @Override
    public void setCollider(ColliderType colliderType) {
        throw new UnsupportedOperationException("Cannot change collider type of BoxColliderComponent");
    }

    @Override
    public void setScale(Vector3f scale) {
        DBox box = (DBox) getCollider();
        box.setLengths(scale.x, scale.y, scale.z);
        this.scale.set(scale);
    }

    public Vector3f getScale() {
        return scale;
    }

    @Override
    protected void onAwake(EngineRuntimeSettings settings) {
        setScale(scale);
    }
}
