package kugge.rendering.core.objects;

import java.util.List;

import org.joml.Vector4f;

import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;

public class RenderScene {
    
    private Camera camera;
    private List<Mesh> meshes;
    private List<Instance> meshInstances;

    private List<PositionalLight> positionalLights;
    private Vector4f ambientLight;
    private DirectionalLight directionalLight;

    public RenderScene(Camera camera, List<Mesh> meshes, List<Instance> meshInstances) {
        this.camera = camera;
        this.meshes = meshes;
        this.meshInstances = meshInstances;

        positionalLights = List.of();
        ambientLight = new Vector4f(0.5f);
        directionalLight = new DirectionalLight();
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

    public List<Instance> getInstances() {
        return meshInstances;
    }

    public void setInstances(List<Instance> meshInstances) {
        this.meshInstances = meshInstances;
    }

    public List<PositionalLight> getPositionalLights() {
        return positionalLights;
    }

    public void setPositionalLights(List<PositionalLight> positionalLights) {
        this.positionalLights = positionalLights;
    }

    public Vector4f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(float ambientLight) {
        this.ambientLight.set(ambientLight);
    }

    public void setAmbientLight(Vector4f ambientLight) {
        this.ambientLight.set(ambientLight);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }
}
