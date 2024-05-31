package kugge.rendering.core.objects;

import java.util.List;

public class RenderScene {
    
    private Camera camera;
    private List<Mesh> meshes;
    private List<Instance> meshInstances;

    public RenderScene(Camera camera, List<Mesh> meshes, List<Instance> meshInstances) {
        this.camera = camera;
        this.meshes = meshes;
        this.meshInstances = meshInstances;
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
}
