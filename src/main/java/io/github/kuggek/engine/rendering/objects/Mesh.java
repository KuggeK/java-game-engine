package io.github.kuggek.engine.rendering.objects;

import io.github.kuggek.engine.rendering.objects.defaults.Materials;

/**
 * Represents a 3D mesh. Contains vertex attributes and indices.
 */
public class Mesh {
    public static final int NO_ID = -1;

    // Unique identifier    
    private int ID;

    private String name;

    // Vertex attributes
    private float[] positions;
    private float[] textureCoords;
    private float[] normals;
    private float[] tangents;

    // Indices of attributes. The index matches the position, textureID coordinate, and normal of the vertex.
    private int[] indices;

    private int numVertices;
    private int numIndices;

    // Can be null, and can be overridden by instance. If neither define a material,
    // set instance mat to default material (in Materials.java in this package).
    private Material material;

    private String fileName;

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices, float[] tangents, Material material, String fileName) {
        if (positions.length % 3 != 0) {
            throw new IllegalArgumentException("Positions array must have a length that is a multiple of 3");
        }

        if (normals.length != positions.length) {
            throw new IllegalArgumentException("Normals array must have the same length as the positions array");
        }

        if (textureCoords.length % 2 != 0) {
            throw new IllegalArgumentException("Texture coordinates array must have a length that is a multiple of 2");
        }

        if (textureCoords.length / 2 != positions.length / 3) {
            throw new IllegalArgumentException("Texture coordinates must be defined for each vertex!");
        }
        this.ID = ID;
        this.name = "Mesh" + ID;
        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.material = material;

        this.numVertices = positions.length / 3;
        this.numIndices = indices.length;

        this.fileName = fileName;
    }

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material, String fileName) {
        this(ID, positions, textureCoords, normals, indices, null, material, fileName);
    }

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material) {
        this(ID, positions, textureCoords, normals, indices, material, null);
    }

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        this(ID, positions, textureCoords, normals, indices, Materials.DEFAULT);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public float[] getTangents() {
        return tangents;
    }

    public void setTangents(float[] tangents) {
        this.tangents = tangents;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
