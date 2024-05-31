package kugge.rendering.core.objects;

import org.joml.Matrix4f;

public class Transform {
    private float[] position;
    private float[] rotation;
    private float[] scale;

    private Matrix4f modelMatrix = new Matrix4f();
    private boolean matrixChanged;

    public Transform() {
        position = new float[3];
        rotation = new float[3];
        scale = new float[3];
        matrixChanged = true;
        getModelMatrix();
    }

    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
        matrixChanged = true;
    }

    public void setRotation(float x, float y, float z) {
        rotation[0] = x;
        rotation[1] = y;
        rotation[2] = z;
        matrixChanged = true;
    }

    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
        matrixChanged = true;
    }

    public void setPosition(float[] position) {
        setPosition(position[0], position[1], position[2]);
    }
    
    public void setRotation(float[] rotation) {
        setRotation(rotation[0], rotation[1], rotation[2]);
    }

    public void setScale(float[] scale) {
        setScale(scale[0], scale[1], scale[2]);
    }

    public Matrix4f getModelMatrix() {
        if (matrixChanged) {
            // TODO Implement a proper model matrix calculation
            modelMatrix.identity().translation(position[0], position[1], position[2]);
            matrixChanged = false;
        }
        return modelMatrix;
    }
}
