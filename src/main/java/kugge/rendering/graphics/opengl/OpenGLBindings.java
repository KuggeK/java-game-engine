package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL4.*;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.Mesh;
import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.graphics.opengl.Shaders.Shader;
public class OpenGLBindings implements GLEventListener {

    record MeshData(int id, int VBOi, int numVertices, int numIndices, Material material, int textureArrayID) {}
    private List<Mesh> meshes;
    private List<MeshData> meshData;

    private Map<Integer, List<Instance>> meshInstances;

    // Buffer for transferring matrix data to the GPU
    private FloatBuffer matrixVals = Buffers.newDirectFloatBuffer(16);
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f inverseViewMatrix;
    private Vector3f viewPos;

    private int[] VAO;
    private int[] VBOs;

    // Number of VBOs per mesh and the corresponding offsets in the VBO array
    private final int VBO_AMOUNT = 5;
    private final int VERTEX_DATA_VBO = 0;
    private final int INDEX_DATA_VBO = 1;
    private final int MODEL_MATRIX_VBO = 2;
    private final int TEXTURE_INDEX_VBO = 3;
    private final int MATERIAL_VBO = 4;

    private final int MAX_N_LIGHTS = 50;

    private int shaderProgram;
    private Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER,
                    "C:/Users/Kilian/Documents/Programming/Java/rendering-engine/src/main/resources/shaders/basic.vert"),
            new Shader(GL_FRAGMENT_SHADER,
                    "C:/Users/Kilian/Documents/Programming/Java/rendering-engine/src/main/resources/shaders/basic.frag")
    };

    // Camera settings
    private float fov = 60f;
    private float near = 0.01f;
    private float far = 1000f;

    // Lighting
    private float globalAmbient = 0.1f;
    private DirectionalLight directionalLight = new DirectionalLight();
    private List<PositionalLight> posLights = new ArrayList<>();

    public OpenGLBindings(List<Mesh> meshes) {
        meshData = new ArrayList<>();
        meshInstances = new HashMap<>();

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        viewPos = new Vector3f();
        
        this.meshes = meshes;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.6f, 0.2f, 1.0f);

        VAO = new int[1];
        gl.glGenVertexArrays(VAO.length, VAO, 0);

        try {
            shaderProgram = Shaders.loadShaders(shaders, gl);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle exception
            throw new RuntimeException();
        }

        gl.glBindVertexArray(VAO[0]);

        loadMeshes(meshes, gl);

        // Configure depth test
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);
        gl.glFrontFace(GL_CCW);

        updateProjectionMatrix((float) drawable.getSurfaceWidth() / drawable.getSurfaceHeight());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        // Clear screen
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);

        // Get uniform locations
        int projectionMxLox = gl.glGetUniformLocation(shaderProgram, "projectionMx");
        int viewMxLoc = gl.glGetUniformLocation(shaderProgram, "viewMx");

        int viewPosLoc = gl.glGetUniformLocation(shaderProgram, "viewPos");
        int globalAmbientLoc = gl.glGetUniformLocation(shaderProgram, "globalAmbient");

        int dirLightAmbientLoc = gl.glGetUniformLocation(shaderProgram, "dirLight.ambient");
        int dirLightDiffuseLoc = gl.glGetUniformLocation(shaderProgram, "dirLight.diffuse");
        int dirLightSpecularLoc = gl.glGetUniformLocation(shaderProgram, "dirLight.specular");
        int dirLightDirectionLoc = gl.glGetUniformLocation(shaderProgram, "dirLight.direction");

        int nLightsLoc = gl.glGetUniformLocation(shaderProgram, "nLights");

        // Set projection, view and normal matrices
        gl.glUniformMatrix4fv(projectionMxLox, 1, false, projectionMatrix.get(matrixVals));
        gl.glUniformMatrix4fv(viewMxLoc, 1, false, viewMatrix.get(matrixVals));

        // Set view position uniform
        inverseViewMatrix.set(viewMatrix).invert().getColumn(3, viewPos);
        gl.glUniform3fv(viewPosLoc, 1, viewPos.get(matrixVals));

        // Set global ambient uniform
        gl.glUniform1f(globalAmbientLoc, globalAmbient);

        // Set directional light uniforms
        gl.glUniform4fv(dirLightAmbientLoc, 1, directionalLight.getAmbient().get(matrixVals));
        gl.glUniform4fv(dirLightDiffuseLoc, 1, directionalLight.getDiffuse().get(matrixVals));
        gl.glUniform4fv(dirLightSpecularLoc, 1, directionalLight.getSpecular().get(matrixVals));
        gl.glUniform3fv(dirLightDirectionLoc, 1, directionalLight.getDirection().get(matrixVals));

        // Set positional light uniforms
        gl.glUniform1i(nLightsLoc, Math.min(MAX_N_LIGHTS, posLights.size()));
        for (int i = 0; i < posLights.size(); ++i) {
            PositionalLight light = posLights.get(i);
            int ambientLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].ambient");
            int diffuseLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].diffuse");
            int specularLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].specular");
            int positionLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].position");
            int constantLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].attenuation.constant");
            int linearLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].attenuation.linear");
            int quadraticLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].attenuation.quadratic");
            int radiusLoc = gl.glGetUniformLocation(shaderProgram, "posLights[" + i + "].attenuation.radius");
            gl.glUniform4fv(ambientLoc, 1, light.getAmbient().get(matrixVals));
            gl.glUniform4fv(diffuseLoc, 1, light.getDiffuse().get(matrixVals));
            gl.glUniform4fv(specularLoc, 1, light.getSpecular().get(matrixVals));
            gl.glUniform4fv(positionLoc, 1, light.getPosition().get(matrixVals));
            gl.glUniform1f(constantLoc, light.getConstant());
            gl.glUniform1f(linearLoc, light.getLinear());
            gl.glUniform1f(quadraticLoc, light.getQuadratic());
            gl.glUniform1f(radiusLoc, light.getRadius());
        }

        matrixVals.clear();

        // Get attribute locations
        int positionLoc = gl.glGetAttribLocation(shaderProgram, "vPosition");
        int texCoordLoc = gl.glGetAttribLocation(shaderProgram, "vTextureCoord");
        int normalLoc = gl.glGetAttribLocation(shaderProgram, "vNormal");
        int modelMxLoc = gl.glGetAttribLocation(shaderProgram, "modelMx");

        gl.glBindVertexArray(VAO[0]);


        // Render all meshes
        for (MeshData mesh : meshData) {
            List<Instance> instances = meshInstances.get(mesh.id);

            // Skip if there are no instances of this mesh
            if (instances == null || instances.size() == 0) {
                System.out.println("No instances of mesh " + mesh.id + " found");
                continue;
            }
            System.out.println("Rendering " + instances.size() + " instances of mesh " + mesh.id);

            int meshVBOIdx = mesh.VBOi;

            // Bind mesh vertex data buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx]);
            
            // Set vertex position data to shader
            gl.glEnableVertexAttribArray(positionLoc);
            gl.glVertexAttribPointer(positionLoc, 3, GL_FLOAT, false, 0, 0);

            // Set vertex texture coordinate data to shader
            gl.glEnableVertexAttribArray(texCoordLoc);
            gl.glVertexAttribPointer(texCoordLoc, 2, GL_FLOAT, false, 0, mesh.numVertices * 3 * Float.BYTES);
            
            // Set vertex normal data to shader
            gl.glEnableVertexAttribArray(normalLoc);
            gl.glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 0, mesh.numVertices * 5 * Float.BYTES);

            // Get model matrices, material data and texture indices for each instance
            FloatBuffer modelMxBuffer = Buffers.newDirectFloatBuffer(instances.size() * 16);
            FloatBuffer materialBuffer = Buffers.newDirectFloatBuffer(13 * instances.size());
            int[] textureIndices = new int[instances.size()];
            for (int j = 0; j < instances.size(); ++j) {
                Instance instance = instances.get(j);
                // Put model matrix into buffer
                instance.getTransform().getModelMatrix().get(j*16, modelMxBuffer);

                // Get material data
                Material material = instance.getMaterial();
                if (material == null) {
                    material = mesh.material;
                }
                material.ambient().get(j*13, materialBuffer);
                material.diffuse().get(j*13 + 4, materialBuffer);
                material.specular().get(j*13 + 8, materialBuffer);
                materialBuffer.put(j*13 + 12, material.shininess());

                // Get texture index
                textureIndices[j] = instance.getTextureIndex();
            }

            // Bind model matrix buffer and send data to GPU
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + MODEL_MATRIX_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, modelMxBuffer.limit() * Float.BYTES, modelMxBuffer, GL_STATIC_DRAW);

            // A model matrix is 4x4, so we need to set up 4 attribute pointers
            for (int j = 0; j < 4; ++j) {
                gl.glVertexAttribPointer(modelMxLoc + j, 4, GL_FLOAT, false, 16 * 4, 16 * j);
                gl.glEnableVertexAttribArray(modelMxLoc + j);
                gl.glVertexAttribDivisor(modelMxLoc + j, 1);
            }

            // Bind material buffer and send data to GPU
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + MATERIAL_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, materialBuffer.limit() * Float.BYTES, materialBuffer, GL_STATIC_DRAW);

            // Set material data to shader
            int ambientLoc = gl.glGetAttribLocation(shaderProgram, "materialAmbient");
            int diffuseLoc = gl.glGetAttribLocation(shaderProgram, "materialDiffuse");
            int specularLoc = gl.glGetAttribLocation(shaderProgram, "materialSpecular");
            int shininessLoc = gl.glGetAttribLocation(shaderProgram, "materialShininess");

            gl.glVertexAttribPointer(ambientLoc, 4, GL_FLOAT, false, 13 * Float.BYTES, 0);
            gl.glEnableVertexAttribArray(ambientLoc);
            gl.glVertexAttribDivisor(ambientLoc, 1);

            gl.glVertexAttribPointer(diffuseLoc, 4, GL_FLOAT, false, 13 * Float.BYTES, 4 * Float.BYTES);
            gl.glEnableVertexAttribArray(diffuseLoc);
            gl.glVertexAttribDivisor(diffuseLoc, 1);

            gl.glVertexAttribPointer(specularLoc, 4, GL_FLOAT, false, 13 * Float.BYTES, 8 * Float.BYTES);
            gl.glEnableVertexAttribArray(specularLoc);
            gl.glVertexAttribDivisor(specularLoc, 1);

            gl.glVertexAttribPointer(shininessLoc, 1, GL_FLOAT, false, 13 * Float.BYTES, 12 * Float.BYTES);
            gl.glEnableVertexAttribArray(shininessLoc);
            gl.glVertexAttribDivisor(shininessLoc, 1);

            // Bind texture index buffer and send data to GPU
            IntBuffer textureIndicesBuffer = Buffers.newDirectIntBuffer(textureIndices);
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + TEXTURE_INDEX_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, textureIndicesBuffer.limit() * Integer.BYTES, textureIndicesBuffer, GL_STATIC_DRAW);

            // Bind texture index information to shader
            int textureIndexLoc = gl.glGetAttribLocation(shaderProgram, "textureIdx");
            gl.glVertexAttribIPointer(textureIndexLoc, 1, GL_INT, 0, 0);
            gl.glEnableVertexAttribArray(textureIndexLoc);
            gl.glVertexAttribDivisor(textureIndexLoc, 1);

            // Bind the texture array
            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D_ARRAY, mesh.textureArrayID);
            gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "textureArray"), 0);
            
            // Bind mesh index buffer and draw
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIdx + INDEX_DATA_VBO]);
            gl.glDrawElementsInstanced(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0, instances.size());
        }

        // Unbind VAO
        gl.glBindVertexArray(0);
        int error = gl.glGetError();
        if (error != GL_NO_ERROR) {
            System.out.println("OpenGL error: " + error);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        clearVBOs(gl);
        gl.glDeleteVertexArrays(1, VAO, 0);
        gl.glDeleteProgram(shaderProgram);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        int viewportWidth = drawable.getSurfaceWidth();
        int viewportHeight = drawable.getSurfaceHeight();

        // Scale the viewport to match the actual window size, if the window has been
        // scaled. See issue #12.
        if (drawable instanceof Component) {
            Component comp = (Component) drawable;
            AffineTransform at = comp.getGraphicsConfiguration().getDefaultTransform();
            float sx = (float) at.getScaleX(), sy = (float) at.getScaleY();
            viewportWidth = (int) (width * sx);
            viewportHeight = (int) (height * sy);
        }
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
        updateProjectionMatrix((float) viewportWidth / viewportHeight);
    }

    public void updateProjectionMatrix(float aspectRatio) {
        projectionMatrix.setPerspective(
                (float) Math.toRadians(fov),
                aspectRatio,
                near,
                far);
    }

    private void loadMeshes(List<Mesh> meshes, GL4 gl) {
        clearVBOs(gl);
        gl.glBindVertexArray(VAO[0]);

        // Generate 4 VBOs for each mesh: vertex data; indices; instance model
        // matrices; 
        VBOs = new int[meshes.size() * VBO_AMOUNT];
        System.out.println("Generating " + VBOs.length + " VBOs");
        gl.glGenBuffers(VBOs.length, VBOs, 0);

        for (int i = 0; i < meshes.size(); ++i) {
            Mesh mesh = meshes.get(i);

            // Get mesh vertex data
            float[] vPositions = mesh.getPositions();
            float[] vTexCoords = mesh.getTextureCoords();
            float[] vNormals = mesh.getNormals();

            // Concatenate vertex data into a single array
            float[] meshVertexData = new float[vPositions.length + vTexCoords.length + vNormals.length];
            System.arraycopy(vPositions, 0, meshVertexData, 0, vPositions.length);
            System.arraycopy(vTexCoords, 0, meshVertexData, vPositions.length, vTexCoords.length);
            System.arraycopy(vNormals, 0, meshVertexData, vPositions.length + vTexCoords.length, vNormals.length);

            FloatBuffer vDataBuffer = Buffers.newDirectFloatBuffer(meshVertexData);
            IntBuffer indicesBuffer = Buffers.newDirectIntBuffer(mesh.getIndices());

            int meshArrayTextureId = loadMeshTextures(mesh, gl);

            // Save mesh data
            int meshVBOIdx = i * VBO_AMOUNT;
            meshData.add(new MeshData(mesh.getId(), meshVBOIdx, mesh.getNumVertices(), mesh.getNumIndices(), mesh.getMaterial(), meshArrayTextureId));

            // Send vertex position, texture coordinate and normal data to GPU.
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + VERTEX_DATA_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, vDataBuffer.limit() * Float.BYTES, vDataBuffer, GL_STATIC_DRAW);

            // Send vertex indices to GPU.
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIdx + INDEX_DATA_VBO]);
            gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.limit() * Integer.BYTES, indicesBuffer,
                    GL_STATIC_DRAW);
        }

        // Set the meshes to null to free up memory, because the data is now stored in
        // the VBOs on the GPU
        meshes = null;
    }

    private int loadMeshTextures(Mesh mesh, GL4 gl) {
        List<Texture> textures = mesh.getTextures();
        if (textures.size() == 0) {
            return -1;
        }

        // Create texture array
        int[] textureArrayID = new int[1];
        gl.glGenTextures(1, textureArrayID, 0);
        gl.glBindTexture(GL_TEXTURE_2D_ARRAY, textureArrayID[0]);
        gl.glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, textures.get(0).getWidth(), textures.get(0).getHeight(), textures.size());

        for (int i = 0; i < textures.size(); ++i) {
            Texture texture = textures.get(i);
            gl.glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, texture.getWidth(), texture.getHeight(), 1, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, Buffers.newDirectIntBuffer(texture.getPixels()));
        }

        for (var entry : mesh.getTextureParameters().entrySet()) {
            gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, entry.getKey(), entry.getValue());
        }

        return textureArrayID[0];
    }

    /**
     * Deletes all VBOs.
     * 
     * @param gl The OpenGL context.
     */
    private void clearVBOs(GL4 gl) {
        if (VBOs != null) {
            gl.glDeleteBuffers(VBOs.length, VBOs, 0);
        }
    }

    public void setInstances(List<Instance> meshInstances) {
        this.meshInstances = meshInstances.parallelStream()
                .collect(Collectors.groupingBy(Instance::getMeshID));
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setGlobalAmbient(float globalAmbient) {
        this.globalAmbient = globalAmbient;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public void setPositionalLights(List<PositionalLight> posLights) {
        this.posLights = posLights;
    }
}
