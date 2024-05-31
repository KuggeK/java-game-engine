package kugge.rendering.core.objects;

import kugge.rendering.core.materials.Material;

/**
 * Represents an instance of a mesh. Links to the mesh by ID. Defines a position, 
 * rotation, scale and material for this instance of the mesh.
 */
public class Instance {
    /**
     * The ID of the mesh that this instance is using
     */
    private int meshID;

    /**
     * The transform of this instance
     */
    private Transform transform;

    /**
     * The material of this instance
     */
    private Material material;

    public Instance(int meshID, float[] position, float[] rotation, float[] scale, Material material) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.transform.setPosition(position);
        this.transform.setRotation(rotation);
        this.transform.setScale(scale);
        this.material = material;
    }

    public Instance(int meshID) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.material = null;
    }

    public Instance(int meshID, Material material) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.material = material;
    }

    public int getMeshID() {
        return meshID;
    }

    public Transform getTransform() {
        return transform;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
