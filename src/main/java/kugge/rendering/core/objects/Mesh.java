package kugge.rendering.core.objects;

import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.materials.Materials;

/**
 * Represents a 3D mesh. Contains vertex attributes and indices.
 */
public class Mesh {
    // Unique identifier    
    private int id;

    // Vertex attributes
    private float[] positions;
    private float[] textureCoords;
    private float[] normals;

    // Indices of attributes. One index per vertex.
    private int[] indices;

    private int numVertices;
    private int numIndices;

    // Can be null, and can be overridden by instance. If neither define a material,
    // set instance mat to default material (in Materials.java in this package).
    private Material material;
    
    public Mesh(int id, float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        this.id = id;
        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        numVertices = positions.length / 3;
        numIndices = indices.length;
        material = Materials.DEFAULT;
    }

    public Mesh(int id, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material) {
        this.id = id;
        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.material = material;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    public int getNumIndices() {
        return numIndices;
    }

    public void setNumIndices(int numIndices) {
        this.numIndices = numIndices;
    }

    public float[] getPositions() {
        return positions;
    }

    public void setPositions(float[] positions) {
        this.positions = positions;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
