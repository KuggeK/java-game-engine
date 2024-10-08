package io.github.kuggek.engine.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import io.github.kuggek.engine.core.assets.AssetManager;
import io.github.kuggek.engine.core.assets.SQLiteAssetManager;
import io.github.kuggek.engine.rendering.objects.Camera;
import io.github.kuggek.engine.rendering.objects.CameraFrustum;
import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.Texture;
import io.github.kuggek.engine.rendering.objects.defaults.Materials;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;
import io.github.kuggek.engine.rendering.objects.lights.PositionalLight;

public class RenderSceneImpl implements RenderScene {

    private Map<Integer, Mesh> meshes;
    private Map<Integer, Texture> textures;
    private Set<RenderInstance> renderInstances;
    private Map<Integer, Material> materials;
    private Camera camera;
    private Matrix4f projectionMatrix;
    private DirectionalLight directionalLight;
    private Vector4f globalAmbient;
    private SkyBox skyBox;
    private Matrix4f lightSpaceMatrix;
    private List<PositionalLight> positionalLights;
    private AssetManager assetManager;
    private float aspectRatio;

    private final Vector3f UP = new Vector3f(0, 1, 0);

    private float[] frustumBB;
    private int frustumMargin = 10;

    public RenderSceneImpl() {
        meshes = new HashMap<>();
        textures = new HashMap<>();
        renderInstances = new LinkedHashSet<>();
        materials = new HashMap<>();
        projectionMatrix = new Matrix4f();
        globalAmbient = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
        lightSpaceMatrix = new Matrix4f();
        positionalLights = new ArrayList<>();
        assetManager = SQLiteAssetManager.getInstance();
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public Mesh getMesh(int meshID) {
        if (meshID == Mesh.NO_ID) {
            return null;
        }

        Mesh mesh = meshes.get(meshID);
        if (mesh == null) {
            try {
                mesh = assetManager.fetchMesh(meshID);
                meshes.put(meshID, mesh);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
        if (textureID == Texture.NO_ID) {
            return null;
        }

        Texture texture = textures.get(textureID);
        if (texture == null) {
            try {
                texture = assetManager.fetchTexture(textureID);
                textures.put(textureID, texture);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
        return renderInstances.stream().filter(ri -> !ri.isDisabled()).collect(Collectors.toList());
    }

    public void addRenderInstance(RenderInstance instance) {
        renderInstances.add(instance);
    }

    public void removeRenderInstance(RenderInstance instance) {
        renderInstances.remove(instance);
    }

    @Override
    public Matrix4f getViewMatrix() {
        if (camera == null) {
            return null;
        }
        return camera.getViewMatrix();
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f updateProjectionMatrix(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        if (camera == null) {
            return new Matrix4f();
        }
        projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        return projectionMatrix;
    }

    @Override
    public Matrix4f getLightSpaceMatrix() {
        if (camera == null || directionalLight == null) {
            return new Matrix4f();
        }

        // Calculate the camera frustum
        CameraFrustum camFrustum = CameraFrustum.calculateFrustumCorners(camera, aspectRatio);

        Vector3f bbCenter = camFrustum.nearCenter().add(camFrustum.farCenter(), new Vector3f()).mul(0.5f);
        
        // Form the light view matrix
        Vector3f lightDir = directionalLight.getDirection().normalize();

        Matrix4f lightViewMatrix = new Matrix4f().lookAt(
            bbCenter, 
            bbCenter.add(lightDir, new Vector3f()),
            UP
        );

        // Transform the frustum corners to light space
        camFrustum = camFrustum.toSpace(lightViewMatrix);

        // Check if the frustum is still inside the previous bounding box
        if (frustumBB != null) {
            boolean inside = true;
            for (Vector3f point : List.of(
                camFrustum.nearTopLeft(),
                camFrustum.nearTopRight(),
                camFrustum.nearBottomLeft(),
                camFrustum.nearBottomRight(),
                camFrustum.farTopLeft(),
                camFrustum.farTopRight(),
                camFrustum.farBottomLeft(),
                camFrustum.farBottomRight()
            )) {
                if (point.x < frustumBB[0] || point.x > frustumBB[3] ||
                    point.y < frustumBB[1] || point.y > frustumBB[4] ||
                    point.z < frustumBB[2] || point.z > frustumBB[5]) {
                    inside = false;
                    break;
                }
            }

            if (inside) {
                return lightSpaceMatrix;
            }
        }

        // Calculate the bounding box of the frustum
        frustumBB = calculateFrustumBoundingBox(camFrustum, frustumMargin);

        // Create a light projection matrix
        lightSpaceMatrix.setOrtho(
            frustumBB[0], frustumBB[3], 
            frustumBB[1], frustumBB[4], 
            frustumBB[2], frustumBB[5]
        );

        lightSpaceMatrix.mul(lightViewMatrix);
        return lightSpaceMatrix;
    }

    /**
     * Calculate the bounding box of the frustum in light space.
     * @param frustum The camera frustum in light space
     * @param margin The margin to apply to the bounding box
     * @return The bounding box of the frustum as a float array. The array is in the format 
     * [minX, minY, minZ, maxX, maxY, maxZ]
     */
    private float[] calculateFrustumBoundingBox(CameraFrustum frustum, float margin) {
        List<Vector3f> points = List.of(
            frustum.nearTopLeft(),
            frustum.nearTopRight(),
            frustum.nearBottomLeft(),
            frustum.nearBottomRight(),
            frustum.farTopLeft(),
            frustum.farTopRight(),
            frustum.farBottomLeft(),
            frustum.farBottomRight()
        );

        // Calculate the min and max values for each axis
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float maxZ = Float.MIN_VALUE;

        for (Vector3f point : points) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            maxZ = Math.max(maxZ, point.z);
        }

        return new float[] {
            minX - margin, minY - margin, minZ - margin,
            maxX + margin, maxY + margin, maxZ + margin
        };
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
        if (materialID == Material.NO_ID) {
            return Materials.DEFAULT;
        }

        Material material = materials.get(materialID);
        if (material == null) {
            try {
                material = assetManager.fetchMaterial(materialID);
                materials.put(materialID, material);
            } catch (Exception e) {
                material = Materials.DEFAULT;
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
        renderInstances = renderInstances.stream()
            .sorted((a, b) -> {
                if (a.getMeshID() != b.getMeshID()) {
                    return a.getMeshID() - b.getMeshID();
                }
                if (a.getMaterialID() != b.getMaterialID()) {
                    return a.getMaterialID() - b.getMaterialID();
                }
                return a.getTextureID() - b.getTextureID();
            })
            .collect(Collectors.toSet());
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
