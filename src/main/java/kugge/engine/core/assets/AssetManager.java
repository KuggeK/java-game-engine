package kugge.engine.core.assets;

import java.util.List;

import kugge.engine.rendering.objects.Texture;
import kugge.engine.rendering.objects.Material;
import kugge.engine.rendering.objects.Mesh;

public interface AssetManager {
    void saveMesh(Mesh mesh) throws Exception;
    Mesh fetchMesh(int ID) throws Exception;
    void deleteMesh(int ID) throws Exception;
    void updateMesh(int ID, Mesh mesh) throws Exception;
    List<Mesh> fetchAllMeshes() throws Exception;
    
    void saveTexture(Texture texture) throws Exception;
    Texture fetchTexture(int ID) throws Exception;
    void deleteTexture(int ID) throws Exception;
    void updateTexture(int ID, Texture texture) throws Exception;
    List<Texture> fetchAllTextures() throws Exception;

    void saveMaterial(Material material) throws Exception;
    Material fetchMaterial(int ID) throws Exception;
    void deleteMaterial(int ID) throws Exception;
    void updateMaterial(int ID, Material material) throws Exception;
    List<Material> fetchAllMaterials() throws Exception;
}
