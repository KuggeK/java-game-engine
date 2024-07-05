package kugge.rendering.core.objects;

/**
 * Represents an instance of a mesh. Links to the mesh by ID. Defines a position, 
 * rotation, scale and materialID for this instance of the mesh.
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
     * The materialID of this instance
     */
    private int materialID = -1;

    private int textureIndex = -1;

    private int bodyID = -1;
    private int colliderID = -1;

    public Instance(int meshID, float[] position, float[] rotation, float[] scale, int materialID) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.transform.setPosition(position[0], position[1], position[2]);
        this.transform.setRotation(rotation[0], rotation[1], rotation[2]);
        this.transform.setScale(scale[0], scale[1], scale[2]);
        this.materialID = materialID;
    }

    public Instance(int meshID) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.materialID = -1;
    }

    public Instance(int meshID, int materialID) {
        this.meshID = meshID;
        this.transform = new Transform();
        this.materialID = materialID;
    }

    public int getMeshID() {
        return meshID;
    }

    public Transform getTransform() {
        return transform;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getBodyID() {
        return bodyID;
    }

    public void setBodyID(int bodyID) {
        this.bodyID = bodyID;
    }

    public int getColliderID() {
        return colliderID;
    }

    public void setColliderID(int colliderID) {
        this.colliderID = colliderID;
    }
}
