package kugge.rendering.core.objects.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import kugge.rendering.core.AssetManager;
import kugge.rendering.core.SQLiteAssetManager;
import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.SkyBox;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.meshes.Mesh;

public class RenderSceneImpl implements RenderScene {

    private Map<Integer, Mesh> meshes;
    private Map<Integer, Texture> textures;
    private Map<Integer, RenderInstance> renderInstances;
    private Map<Integer, Material> materials;
    private Camera camera;
    private Matrix4f projectionMatrix;
    private DirectionalLight directionalLight;
    private Vector4f globalAmbient;
    private SkyBox skyBox;
    private Matrix4f lightSpaceMatrix;
    private List<PositionalLight> positionalLights;
    private AssetManager assetManager;

    public RenderSceneImpl() {
        meshes = new HashMap<>();
        textures = new HashMap<>();
        renderInstances = new LinkedHashMap<>();
        materials = new HashMap<>();
        projectionMatrix = new Matrix4f();
        globalAmbient = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
        lightSpaceMatrix = new Matrix4f();
        positionalLights = new ArrayList<>();
        assetManager = SQLiteAssetManager.getInstance();
    }

    @Override
    public Mesh getMesh(int meshID) {
        Mesh mesh = meshes.get(meshID);
        if (mesh == null) {
            try {
                mesh = assetManager.fetchMesh(meshID);
                meshes.put(meshID, mesh);
            } catch (Exception e) {
                e.printStackTrace();
                mesh = null;
            }
        }
        return mesh;
    }

    public void addMesh(Mesh mesh) {
        meshes.put(mesh.getID(), mesh);
    }

    @Override
    public Texture getTexture(int textureID) {
        Texture texture = textures.get(textureID);
        if (texture == null) {
            try {
                texture = assetManager.fetchTexture(textureID);
                textures.put(textureID, texture);
            } catch (Exception e) {
                e.printStackTrace();
                texture = null;
            }
        }
        return texture;
    }

    public void addTexture(Texture texture) {
        textures.put(texture.getID(), texture);
    }

    @Override
    public List<RenderInstance> getRenderInstances() {
        return List.copyOf(renderInstances.values());
    }

    public void addRenderInstance(RenderInstance instance) {
        renderInstances.put(instance.getID(), instance);
    }

    public void removeRenderInstance(RenderInstance instance) {
        renderInstances.remove(instance.getID());
    }

    @Override
    public Matrix4f getViewMatrix() {
        if (camera == null) {
            return new Matrix4f();
        }
        return camera.getViewMatrix();
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f updateProjectionMatrix(float aspectRatio) {
        if (camera == null) {
            return new Matrix4f();
        }
        projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        return projectionMatrix;
    }

    @Override
    public Matrix4f getLightSpaceMatrix() {
        lightSpaceMatrix.setOrtho(-10, 10, -10, 10, 1, 20);
        Matrix4f lightViewMatrix = new Matrix4f().lookAt(camera.getTransform().getPosition(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        return lightSpaceMatrix.mul(lightViewMatrix);
    }

    @Override
    public List<PositionalLight> getPositionalLights() {
        return List.copyOf(positionalLights);
    }

    public void addPositionalLight(PositionalLight light) {
        positionalLights.add(light);
    }

    public void removePositionalLight(PositionalLight light) {
        positionalLights.remove(light);
    }

    @Override
    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight light) {
        directionalLight = light;
    }

    @Override
    public Vector4f getGlobalAmbient() {
        return globalAmbient;
    }

    public void setGlobalAmbient(Vector4f ambient) {
        globalAmbient = ambient;
    }

    @Override
    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox box) {
        skyBox = box;
    }

    @Override
    public Material getMaterial(int materialID) {
        Material material = materials.get(materialID);
        if (material == null) {
            try {
                material = assetManager.fetchMaterial(materialID);
                materials.put(materialID, material);
            } catch (Exception e) {
                e.printStackTrace();
                material = null;
            }
        }
        return material;
    }

    public void addMaterial(Material material) {
        materials.put(material.getID(), material);
    }

    @Override
    public void sortRenderInstances() {
        // Sort the render instances by mesh, material, and texture
        renderInstances = renderInstances.entrySet().stream()
            .sorted((a, b) -> {
                RenderInstance ra = a.getValue();
                RenderInstance rb = b.getValue();
                if (ra.getMeshID() != rb.getMeshID()) {
                    return ra.getMeshID() - rb.getMeshID();
                }
                if (ra.getMaterialID() != rb.getMaterialID()) {
                    return ra.getMaterialID() - rb.getMaterialID();
                }
                return ra.getTextureID() - rb.getTextureID();
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)
        );
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
