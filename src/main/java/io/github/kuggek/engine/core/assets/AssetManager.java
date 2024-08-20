package io.github.kuggek.engine.core.assets;

import java.util.List;

import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.Texture;

/**
 * Represents an asset manager that can be used to save, fetch, update, and delete assets such as meshes, textures, and materials.
 */
public interface AssetManager {
    /**
     * Saves a mesh to the asset manager.
     * @param mesh The mesh to save
     * @throws Exception If the mesh could not be saved
     */
    void saveMesh(Mesh mesh) throws Exception;

    /**
     * Saves a mesh to the asset manager, but checks first if a mesh with the same ID already exists. 
     * If it does, do not save the mesh.
     * @param mesh The mesh to save
     * @throws Exception If the mesh could not be saved
     */
    void saveMeshChecked(Mesh mesh) throws Exception;

    /**
     * Fetches a mesh from the asset manager by ID.
     * @param ID The ID of the mesh to fetch
     * @return The mesh
     * @throws Exception If the mesh could not be fetched
     */
    Mesh fetchMesh(int ID) throws Exception;

    /**
     * Deletes a mesh from the asset manager by ID.
     * @param ID The ID of the mesh to delete
     * @throws Exception If the mesh could not be deleted
     */
    void deleteMesh(int ID) throws Exception;

    /**
     * Updates a mesh in the asset manager. The mesh is identified by ID and the new mesh is provided.
     * @param ID The ID of the mesh to update
     * @param mesh The new mesh
     * @throws Exception If the mesh could not be updated
     */
    void updateMesh(int ID, Mesh mesh) throws Exception;

    /**
     * Fetches all meshes from the asset manager.
     * @return A list of all meshes
     * @throws Exception If the meshes could not be fetched
     */
    List<Mesh> fetchAllMeshes() throws Exception;
    

    /**
     * Saves a texture to the asset manager.
     * @param texture The texture to save
     * @throws Exception If the texture could not be saved
     */
    void saveTexture(Texture texture) throws Exception;

    /**
     * Saves a texture to the asset manager, but checks first if a texture with the same ID already exists. 
     * If it does, do not save the texture.
     * @param texture The texture to save
     * @throws Exception If the texture could not be saved
     */
    void saveTextureChecked(Texture texture) throws Exception;

    /**
     * Fetches a texture from the asset manager by ID.
     * @param ID The ID of the texture to fetch
     * @return The texture
     * @throws Exception If the texture could not be fetched
     */
    Texture fetchTexture(int ID) throws Exception;

    /**
     * Deletes a texture from the asset manager by ID.
     * @param ID The ID of the texture to delete
     * @throws Exception If the texture could not be deleted
     */
    void deleteTexture(int ID) throws Exception;

    /**
     * Updates a texture in the asset manager. The texture is identified by ID and the new texture is provided.
     * @param ID The ID of the texture to update
     * @param texture The new texture
     * @throws Exception If the texture could not be updated
     */
    void updateTexture(int ID, Texture texture) throws Exception;

    /**
     * Fetches all textures from the asset manager.
     * @return A list of all textures
     * @throws Exception If the textures could not be fetched
     */
    List<Texture> fetchAllTextures() throws Exception;


    /**
     * Saves a material to the asset manager.
     * @param material The material to save
     * @throws Exception If the material could not be saved
     */
    void saveMaterial(Material material) throws Exception;

    /**
     * Saves a material to the asset manager, but checks first if a material with the same ID already exists. 
     * If it does, do not save the material.
     * @param material The material to save
     * @throws Exception If the material could not be saved
     */
    void saveMaterialChecked(Material material) throws Exception;

    /**
     * Fetches a material from the asset manager by ID.
     * @param ID The ID of the material to fetch
     * @return The material
     * @throws Exception If the material could not be fetched
     */
    Material fetchMaterial(int ID) throws Exception;

    /**
     * Deletes a material from the asset manager by ID.
     * @param ID The ID of the material to delete
     * @throws Exception If the material could not be deleted
     */
    void deleteMaterial(int ID) throws Exception;

    /**
     * Updates a material in the asset manager. The material is identified by ID and the new material is provided.
     * @param ID The ID of the material to update
     * @param material The new material
     * @throws Exception If the material could not be updated
     */
    void updateMaterial(int ID, Material material) throws Exception;

    /**
     * Fetches all materials from the asset manager.
     * @return A list of all materials
     * @throws Exception If the materials could not be fetched
     */
    List<Material> fetchAllMaterials() throws Exception;
}
