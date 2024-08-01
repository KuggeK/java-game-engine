package io.github.kuggek.engine.rendering.objects;

import java.util.Map;

import org.joml.Matrix4f;

import io.github.kuggek.engine.core.Transform;

/**
 * Represents an instance of a mesh to be rendered.
 */
public interface RenderInstance {

  int getID();

    Matrix4f getModelMatrix();

    int getMeshID();

    int getMaterialID();

    boolean isTexturingEnabled();

    int getTextureID();

    Transform getTransform();

    boolean isNormalMapEnabled();

    int getNormalMapID();

    boolean isLit();

    boolean castsShadows();

    Map<Integer, Integer> getTextureParameters();
}
