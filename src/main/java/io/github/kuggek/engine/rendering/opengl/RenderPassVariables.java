package io.github.kuggek.engine.rendering.opengl;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import io.github.kuggek.engine.rendering.objects.RenderInstance;

/**
 * Contains variables that are used by multiple shader programs per frame to avoid 
 * calculating the same values multiple times.
 */
public class RenderPassVariables {
    
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f lightSpaceMatrix;
    private List<RenderInstance> instancesToRender;

    public RenderPassVariables() {
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        lightSpaceMatrix = new Matrix4f();
        instancesToRender = new ArrayList<>();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix.set(viewMatrix);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix.set(projectionMatrix);
    }

    public Matrix4f getLightSpaceMatrix() {
        return lightSpaceMatrix;
    }

    public void setLightSpaceMatrix(Matrix4f lightSpaceMatrix) {
        this.lightSpaceMatrix.set(lightSpaceMatrix);
    }

    /**
     * Returns a list of instances that should be rendered in the current frame. 
     * This list should be populated with instances that pass culling tests.
     * @return A list of instances to render
     */
    public List<RenderInstance> getInstancesToRender() {
        return instancesToRender;
    }

    public void addInstanceToRender(RenderInstance instance) {
        instancesToRender.add(instance);
    }

    public void setInstancesToRender(List<RenderInstance> instancesToRender) {
        this.instancesToRender = new ArrayList<>(instancesToRender);
    }

    /**
     * Resets all variables to their default values.
     */
    public void reset() {
        viewMatrix.identity();
        projectionMatrix.identity();
        lightSpaceMatrix.identity();
        instancesToRender.clear();
    }
}
