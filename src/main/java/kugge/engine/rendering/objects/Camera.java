package kugge.engine.rendering.objects;

import org.joml.Matrix4f;

import kugge.engine.core.Transform;

public interface Camera {
     public Matrix4f getViewMatrix();

    public Matrix4f getProjectionMatrix(float aspectRatio);

    public float getFov();

    public float getNear();

    public float getFar();

    public boolean isOrthographic();

    public Transform getTransform();

}
