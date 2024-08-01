package io.github.kuggek.engine.rendering;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.Texture;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;

public interface RenderScene {
    /**
     * Get a specific mesh by its ID.
     * @param meshID The ID of the mesh to get.
     * @return The mesh with the given ID or null if it does not exist.
     */
    Mesh getMesh(int meshID);

    /**
     * Get a specific texture by its ID.
     * @param textureID The ID of the texture to get.
     * @return The texture with the given ID or null if it does not exist.
     */
    Texture getTexture(int textureID);

    /**
     * Get the list of all instances in the scene.
     * @return The list of instances.
     */
    List<RenderInstance> getRenderInstances();

    /**
     * Get the view matrix of the scene.
     * @return The view matrix.
     */
    Matrix4f getViewMatrix();

    /**
     * Gets the current projection matrix of the scene.
     * @return The projection matrix.
     */
    Matrix4f getProjectionMatrix();

    /**
     * Update and return the projection matrix of the scene for a given aspect ratio.
     * @param aspectRatio The aspect ratio of the screen.
     * @return The projection matrix.
     */
    Matrix4f updateProjectionMatrix(float aspectRatio);

    /**
     * Get the light space matrix of the scene. This is used for shadow mapping.
     * @return The light space matrix.
     */
    Matrix4f getLightSpaceMatrix();

    /**
     * Get the list of all positional lights in the scene.
     * @return The list of positional lights.
     */
    List<PositionalLight> getPositionalLights();
    
    /**
     * Get the directional light in the scene.
     * @return The directional light.
     */
    DirectionalLight getDirectionalLight();

    /**
     * Get the global ambient light of the scene.
     * @return The global ambient light.
     */
    Vector4f getGlobalAmbient();
    
    /**
     * Get the skybox of the scene.
     * @return The skybox.
     */
    SkyBox getSkyBox();

    /**
     * Get a specific material by its ID.
     * @param materialID The ID of the material to get.
     * @return The material with the given ID or null if it does not exist.
     */
    Material getMaterial(int materialID);

    /**
     * Sorts the render instances to ensure an optimal rendering order.
     */
    void sortRenderInstances();
}
