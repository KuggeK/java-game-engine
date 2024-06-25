package kugge.rendering.core.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.materials.Materials;

/**
 * Represents a 3D mesh. Contains vertex attributes and indices.
 */
public class Mesh {
    // Unique identifier    
    private int ID;

    // Vertex attributes
    private float[] positions;
    private float[] textureCoords;
    private float[] normals;

    // Indices of attributes. The index matches the position, textureID coordinate, and normal of the vertex.
    private int[] indices;

    private int numVertices;
    private int numIndices;

    // Can be null, and can be overridden by instance. If neither define a material,
    // set instance mat to default material (in Materials.java in this package).
    private Material material;

    private String fileName;

    /**
     * Textures that can be applied to the mesh. Can be empty. These textureIDs should all
     * be the same size and have the same properties.
     */
    private List<Integer> textureIDs = new ArrayList<>();

    /**
     * Texture parameters for the textureIDs. Can be empty.
     */
    private Map<Integer, Integer> textureParameters;

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material, List<Integer> textureIDs, Map<Integer, Integer> textureParameters, String fileName) {
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
        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.material = material;
        this.textureIDs = textureIDs;
        this.textureParameters = textureParameters;

        this.numVertices = positions.length / 3;
        this.numIndices = indices.length;

        this.fileName = fileName;
    }

    public Mesh(int ID, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material) {
        this(ID, positions, textureCoords, normals, indices, material, new ArrayList<>(), new HashMap<>(), null);
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

    public List<Integer> getTextureIDs() {
        return textureIDs;
    }

    public void setTextureIDs(List<Integer> textureIDs) {
        this.textureIDs = textureIDs;
    }

    public void addTexture(Integer textureID) {
        textureIDs.add(textureID);
    }

    public void removeTexture(Integer textureID) {
        textureIDs.remove(textureID);
    }

    public void removeTexture(int index) {
        textureIDs.remove(index);
    }

    public void clearTextures() {
        textureIDs.clear();
    }

    public Map<Integer, Integer> getTextureParameters() {
        return textureParameters;
    }

    public void setTextureParameters(Map<Integer, Integer> textureParameters) {
        this.textureParameters = textureParameters;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
