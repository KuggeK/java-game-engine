package kugge.rendering.graphics.opengl;

import java.util.HashMap;
import java.util.Map;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL4.*;

import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.meshes.Mesh;

public class GLLocations {
    
    /**
     * The VAO for mesh data.
     */
    private int meshVAO;

    /**
     * Contains the vertex data locations for each mesh. The key is the mesh's ID.
     */
    private Map<Integer, Integer> meshVBOs;

    /**
     * Contains the index data locations for each mesh. The key is the mesh's ID.
     */
    private Map<Integer, Integer> meshIndices;
    
    /**
     * Contains the texture IDs for each texture. The key is the texture's ID.
     */
    private Map<Integer, Integer> textureLocations;
    
    /**
     * Contains the textures that are currently active per texture unit. 
     */
    private int[] activeTextureUnits;

    private int shadowMapTexture;

    public GLLocations(GL4 gl, int textureUnitAmount) {
        int[] meshVAOs = new int[1];
        gl.glGenVertexArrays(1, meshVAOs, 0);
        meshVAO = meshVAOs[0];

        meshVBOs = new HashMap<>();
        meshIndices = new HashMap<>();

        textureLocations = new HashMap<>();
        activeTextureUnits = new int[textureUnitAmount];
        for (int i = 0; i < activeTextureUnits.length; ++i) {
            activeTextureUnits[i] = -1;
        }

        shadowMapTexture = -1;
    }

    /**
     * Get the VAO location for mesh data.
     * @return The VAO location.
     */
    public int getMeshVAO() {
        return meshVAO;
    }

    public void setMeshVAO(int meshVAO) {
        this.meshVAO = meshVAO;
    }

    /**
     * Get the VBO location for a mesh by its ID.
     * @param meshID The ID of the mesh.
     * @return The VBO location or -1 if the mesh does not exist.
     */
    public Integer getMeshVertexLoc(int meshID) {
        return meshVBOs.getOrDefault(meshID, -1);
    }

    /**
     * Set the VBO location for a meshes vertex data by its ID.
     * @param meshID The ID of the mesh.
     * @param meshVBO The VBO location.
     */
    public void setMeshVertexLoc(int meshID, int meshVBO) {
        meshVBOs.put(meshID, meshVBO);
    }

    /**
     * Get the VBO location for a meshes index data by its ID.
     * @param meshID The ID of the mesh.
     * @return The VBO location or -1 if the mesh does not exist.
     */
    public Integer getMeshIndexLoc(int meshID) {
        return meshIndices.getOrDefault(meshID, -1);
    }

    /**
     * Set the VBO location for a meshes index data by its ID.
     * @param meshID The ID of the mesh.
     * @param meshIndex The VBO location.
     */
    public void setMeshIndexLoc(int meshID, int meshIndex) {
        meshIndices.put(meshID, meshIndex);
    }

    /**
     * Get the texture location for a texture by its ID.
     * @param textureID The ID of the texture.
     * @return The texture location or -1 if the texture does not exist.
     */
    public Integer getTextureLocation(int textureID) {
        return textureLocations.getOrDefault(textureID, -1);
    }

    /**
     * Set the location for a texture by its ID.
     * @param textureID The ID of the texture.
     * @param textureLocation The texture location.
     */
    public void setTextureLocation(int textureID, int textureLocation) {
        textureLocations.put(textureID, textureLocation);
    }

    /**
     * Get the ID of the texture that is currently active on the given texture unit.
     * @param textureUnit The texture unit to check.
     * @return The texture ID or -1 if no texture is active. 
     * @throws IndexOutOfBoundsException If the texture unit does not exist.
     */
    public int getActiveTextureUnit(int textureUnit) throws IndexOutOfBoundsException {
        if (textureUnit < 0 || textureUnit >= activeTextureUnits.length) {
            throw new IndexOutOfBoundsException("No such texture unit exists.");
        }
        return activeTextureUnits[textureUnit];
    }

    public int getShadowMapTexture() {
        return shadowMapTexture;
    }

    public void setShadowMapTexture(int shadowMapTexture) {
        this.shadowMapTexture = shadowMapTexture;
    }

    /**
     * Clear all data in the locations. 
     */
    public void clear() {
        meshVAO = -1;
        meshVBOs.clear();
        meshIndices.clear();
        textureLocations.clear();
        for (int i = 0; i < activeTextureUnits.length; ++i) {
            activeTextureUnits[i] = -1;
        }
    }

    /**
     * Loads a single mesh into the OpenGL context.
     * @param mesh The mesh to load.
     */
    public void loadMesh(GL4 gl, Mesh mesh) {
        gl.glBindVertexArray(meshVAO);

        int[] VBO = new int[2];
        gl.glGenBuffers(2, VBO, 0);

        // Bind vertex data buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);

        // Get mesh vertex data
        float[] vPositions = mesh.getPositions();
        float[] vTexCoords = mesh.getTextureCoords();
        float[] vNormals = mesh.getNormals();

        // Concatenate vertex data into a single array
        float[] meshVertexData = new float[vPositions.length + vTexCoords.length + vNormals.length];
        System.arraycopy(vPositions, 0, meshVertexData, 0, vPositions.length);
        System.arraycopy(vTexCoords, 0, meshVertexData, vPositions.length, vTexCoords.length);
        System.arraycopy(vNormals, 0, meshVertexData, vPositions.length + vTexCoords.length, vNormals.length);

        gl.glBufferData(GL_ARRAY_BUFFER, meshVertexData.length * Float.BYTES, Buffers.newDirectFloatBuffer(meshVertexData), GL_STATIC_DRAW);

        // Set index data buffer
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBO[1]);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.getIndices().length * Integer.BYTES, Buffers.newDirectIntBuffer(mesh.getIndices()), GL_STATIC_DRAW);

        // Add mesh data to locations
        setMeshVertexLoc(mesh.getID(), VBO[0]);
        setMeshIndexLoc(mesh.getID(), VBO[1]);
    }

    /**
     * Loads a single texture into the OpenGL context.
     * @param gl The OpenGL context.
     * @param texture The texture to load.
     */
    public void loadTexture(GL4 gl, Texture texture) {
        int[] textureName = new int[1];
        gl.glGenTextures(1, textureName, 0);

        gl.glBindTexture(GL_TEXTURE_2D, textureName[0]);

        gl.glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA8, 
            texture.getWidth(), texture.getHeight(), 0, 
            GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, Buffers.newDirectIntBuffer(textureName)
        );

        setTextureLocation(texture.getID(), textureName[0]);
    }
}
