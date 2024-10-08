package io.github.kuggek.engine.rendering.opengl;

import java.util.HashMap;
import java.util.Map;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.Meshes;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.Texture;

import static com.jogamp.opengl.GL4.*;

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
     * Contains the tangent data locations for each mesh. The key is the mesh's ID.
     */
    private Map<Integer, Integer> meshTangents;

    /**
     * Contains the texture IDs for each texture. The key is the texture's ID.
     */
    private Map<Integer, Integer> textureLocations;
    
    /**
     * Contains the textures that are currently active per texture unit. 
     */
    private int[] activeTextureUnits;

    private int shadowMapTexture;

    private int maxNLights = 20;

    private boolean resetOnNextFrame = false;

    public GLLocations(int textureUnitAmount) {
        meshVBOs = new HashMap<>();
        meshIndices = new HashMap<>();
        meshTangents = new HashMap<>();

        textureLocations = new HashMap<>();
        activeTextureUnits = new int[textureUnitAmount];
        for (int i = 0; i < activeTextureUnits.length; ++i) {
            activeTextureUnits[i] = -1;
        }

        shadowMapTexture = -1;
    }

    public void init(GL4 gl) {
        System.out.println("Initializing OpenGL locations...");
        int[] meshVAOs = new int[1];
        gl.glGenVertexArrays(1, meshVAOs, 0);
        meshVAO = meshVAOs[0];
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
     * Get the VBO location for a meshes tangent data by its ID.
     * @param meshID The ID of the mesh.
     * @return The VBO location or -1 if the mesh does not exist.
     */
    public Integer getMeshTangentLoc(int meshID) {
        return meshTangents.getOrDefault(meshID, -1);
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

    public void reset(GL4 gl) {
        clear(gl);

        // Generate new VAO
        int[] meshVAOs = new int[1];
        gl.glGenVertexArrays(1, meshVAOs, 0);
        meshVAO = meshVAOs[0];
    }

    public void reset() {
        resetOnNextFrame = true;
    }

    public void update(GL4 gl) {
        if (resetOnNextFrame) {
            reset(gl);
            resetOnNextFrame = false;
        }
    }

    /**
     * Clear all data from the OpenGL context and reset the locations.
     */
    private void clear(GL4 gl) {
        if (meshVAO != -1) {
            gl.glDeleteVertexArrays(1, new int[] { meshVAO }, 0);
            meshVAO = -1;
        }

        if (!meshVBOs.isEmpty()) {
            int[] meshVBOArray = meshVBOs.values().stream().mapToInt(Integer::intValue).toArray();
            gl.glDeleteBuffers(meshVBOArray.length, meshVBOArray, 0);
            meshVBOs.clear();
        }

        if (!meshIndices.isEmpty()) {
            int[] meshIndexArray = meshIndices.values().stream().mapToInt(Integer::intValue).toArray();
            gl.glDeleteBuffers(meshIndexArray.length, meshIndexArray, 0);
            meshIndices.clear();
        }

        if (!meshTangents.isEmpty()) {
            int[] meshTangentArray = meshTangents.values().stream().mapToInt(Integer::intValue).toArray();
            gl.glDeleteBuffers(meshTangentArray.length, meshTangentArray, 0);
            meshTangents.clear();
        }

        if (!textureLocations.isEmpty()) {
            int[] textureArray = textureLocations.values().stream().mapToInt(Integer::intValue).toArray();
            gl.glDeleteTextures(textureArray.length, textureArray, 0);
            textureLocations.clear();
        }

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

    public void loadMeshTangentData(GL4 gl, Mesh mesh) {
        gl.glBindVertexArray(meshVAO);

        int[] VBO = new int[1];
        gl.glGenBuffers(1, VBO, 0);

        // Bind tangent data buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);

        // Get mesh tangent data
        float[] tangents = mesh.getTangents();
        if (tangents == null) {
            tangents = Meshes.calculateTangents(mesh);
        }

        gl.glBufferData(GL_ARRAY_BUFFER, tangents.length * Float.BYTES, Buffers.newDirectFloatBuffer(tangents), GL_STATIC_DRAW);

        // Add mesh data to locations
        meshTangents.put(mesh.getID(), VBO[0]);
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
            GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, Buffers.newDirectIntBuffer(texture.getPixels())
        );

        setTextureLocation(texture.getID(), textureName[0]);
    }

    /**
     * Sets up a texture unit to a texture and texture parameters. Leaves the texture unit active.
     * @param gl The OpenGL context.
     * @param textureParameters The texture parameters to set.
     * @param textureID The ID of the texture.
     * @param textureUnit The texture unit to set up.
     * @return True if the texture unit was set up successfully, false otherwise.
     */
    public boolean setupTextureUnit(GL4 gl, Map<Integer, Integer> textureParameters, int textureID, int textureUnit) {
        int textureName = getTextureLocation(textureID);

        if (textureName == -1 || textureUnit < 0 || textureUnit >= activeTextureUnits.length) {
            return false;
        }

        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);

        gl.glBindTexture(GL_TEXTURE_2D, textureName);

        for (var param : textureParameters.entrySet()) {
            gl.glTexParameteri(GL_TEXTURE_2D, param.getKey(), param.getValue());
        }

        return true;
    }

    /**
     * Sets up a texture unit to an instance's texture and texture parameters.
     * @param gl The OpenGL context.
     * @param instance The render instance.
     * @param textureUnit The texture unit to set up.
     * @return True if the texture unit was set up successfully, false otherwise.
     */
    public boolean setupTextureUnit(GL4 gl, RenderInstance instance, int textureUnit) {
        return setupTextureUnit(gl, instance.getTextureParameters(), instance.getTextureID(), textureUnit);
    }

    public int getMaxNLights() {
        return maxNLights;
    }

    public void setMaxNLights(int maxNLights) {
        this.maxNLights = maxNLights;
    }
}
