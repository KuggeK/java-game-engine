package kugge.rendering.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;

public class RenderScene {
    
    private int ID;

    private Camera camera;
    private List<Mesh> meshes;
    private List<Instance> meshInstances;
    private List<Material> materials;
    private List<Texture> textures;

    private List<PositionalLight> positionalLights;
    private Vector4f globalAmbient;
    private DirectionalLight directionalLight;

    private SkyBox skyBox;

    public RenderScene(int ID, Camera camera, List<Mesh> meshes, List<Instance> meshInstances, List<Material> materials, List<Texture> textures, List<PositionalLight> positionalLights, Vector4f globalAmbient, DirectionalLight directionalLight, SkyBox skyBox) {
        this.ID = ID;
        this.camera = camera;
        this.meshes = meshes;
        this.meshInstances = meshInstances;
        this.materials = materials;
        this.textures = textures;
        this.positionalLights = positionalLights;
        this.globalAmbient = globalAmbient;
        this.directionalLight = directionalLight;
        this.skyBox = skyBox;
    }

    public RenderScene(Camera camera, List<Mesh> meshes, List<Instance> meshInstances) {
        this(-1, camera, meshes, meshInstances, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new Vector4f(0.1f), new DirectionalLight(), null);
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }
    
    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<Instance> getInstances() {
        return meshInstances;
    }

    public void setInstances(List<Instance> meshInstances) {
        this.meshInstances = meshInstances;
    }

    public List<Texture> getTextures() {
        return textures;
    }

    public void setTextures(List<Texture> textures) {
        this.textures = textures;
    }

    public List<PositionalLight> getPositionalLights() {
        return positionalLights;
    }

    public void setPositionalLights(List<PositionalLight> positionalLights) {
        this.positionalLights = positionalLights;
    }

    public Vector4f getGlobalAmbient() {
        return globalAmbient;
    }

    public void setGlobalAmbient(float globalAmbient) {
        this.globalAmbient.set(globalAmbient);
    }

    public void setGlobalAmbient(Vector4f globalAmbient) {
        this.globalAmbient.set(globalAmbient);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
}
