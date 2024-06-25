package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL4.*;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    private List<Texture> textures;

    private Map<Integer, List<Instance>> meshInstances;

    private Map<Integer, Material> materials;

    // Buffer for transferring matrix data to the GPU
    private FloatBuffer matrixVals = Buffers.newDirectFloatBuffer(16);
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f inverseViewMatrix;
    private Vector3f viewPos;
    private Matrix4f lightSpaceMatrix;
    private Matrix4f lightViewMatrix;

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
        new Shader(GL_VERTEX_SHADER, "basic.vert"),
        new Shader(GL_FRAGMENT_SHADER, "basic.frag")
    };

    private int shadowShaderProgram;
    private Shader[] shadowShaders = new Shader[] {
        new Shader(GL_VERTEX_SHADER, "shadow.vert"),
        new Shader(GL_FRAGMENT_SHADER,"shadow.frag")
    };

    private final int SHADOW_WIDTH = 1024;
    private final int SHADOW_HEIGHT = 1024;
    private int depthMapFBO;
    private int depthMapTexture;

    // Camera settings
    private float fov = 60f;
    private float near = 0.01f;
    private float far = 1000f;

    // Lighting
    private Vector4f globalAmbient = new Vector4f(0.2f);
    private DirectionalLight directionalLight = new DirectionalLight();
    private List<PositionalLight> posLights = new ArrayList<>();

    public OpenGLBindings(List<Mesh> meshes, List<Material> materials) {
        meshData = new ArrayList<>();
        meshInstances = new HashMap<>();

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        viewPos = new Vector3f();
        lightSpaceMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();

        this.meshes = meshes;
        this.materials = materials.stream().collect(Collectors.toMap(Material::ID, m -> m));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.6f, 0.2f, 1.0f);

        VAO = new int[1];
        gl.glGenVertexArrays(VAO.length, VAO, 0);

        try {
            shaderProgram = Shaders.loadShaders(shaders, gl);
            shadowShaderProgram = Shaders.loadShaders(shadowShaders, gl);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle exception
            throw new RuntimeException();
        }

        gl.glBindVertexArray(VAO[0]);

        loadMeshes(meshes, gl);

        // Set up depth map FBO
        setupDepthBuffer(gl);

        // Configure depth test
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        
        // Configure culling
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);
        gl.glFrontFace(GL_CCW);

        updateProjectionMatrix((float) drawable.getSurfaceWidth() / drawable.getSurfaceHeight());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glBindVertexArray(VAO[0]);

        // Clear screen
        gl.glClear(GL_COLOR_BUFFER_BIT);

        // Setup normal shader program
        gl.glUseProgram(shaderProgram);

        // Get uniform locations
        int projectionMxLox = gl.glGetUniformLocation(shaderProgram, "projectionMx");
        int viewMxLoc = gl.glGetUniformLocation(shaderProgram, "viewMx");
        int lightSpaceMxLoc = gl.glGetUniformLocation(shaderProgram, "lightSpaceMx");

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

        // Calculate and set light space matrix
        lightSpaceMatrix.identity().ortho(-10, 10, -10, 10, near, far);
        lightViewMatrix.identity().lookAt(new Vector3f().sub(directionalLight.getDirection()).mul(10), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        lightSpaceMatrix.mul(lightViewMatrix);

        gl.glUniformMatrix4fv(lightSpaceMxLoc, 1, false, lightSpaceMatrix.get(matrixVals));

        // Set view position uniform
        inverseViewMatrix.set(viewMatrix).invert().getColumn(3, viewPos);
        gl.glUniform3fv(viewPosLoc, 1, viewPos.get(matrixVals));

        // Set global ambient uniform
        gl.glUniform3fv(globalAmbientLoc, 1, globalAmbient.get(matrixVals));

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

        // Setup shadow shader program
        gl.glUseProgram(shadowShaderProgram);

        // Get shadow shader uniform and attribute locations
        lightSpaceMxLoc = gl.glGetUniformLocation(shadowShaderProgram, "lightSpaceMx");
        int modelMxShadowLoc = gl.glGetAttribLocation(shadowShaderProgram, "modelMx");
        int shadowPositionLoc = gl.glGetAttribLocation(shadowShaderProgram, "vPosition");

        // Send light space matrix to shadow shader
        gl.glUniformMatrix4fv(lightSpaceMxLoc, 1, false, lightSpaceMatrix.get(matrixVals));


        // ----------------- SHADOW MAP RENDERING -----------------
        gl.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        gl.glCullFace(GL_FRONT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        // Render all meshes
        for (MeshData mesh : meshData) {
            List<Instance> instances = meshInstances.get(mesh.id);

            // Skip if there are no instances of this mesh
            if (instances == null || instances.size() == 0) {
                continue;
            }

            // Get model matrices for each instance
            FloatBuffer modelMxBuffer = Buffers.newDirectFloatBuffer(instances.size() * 16);
            for (int j = 0; j < instances.size(); ++j) {
                Instance instance = instances.get(j);
                // Put model matrix into buffer
                instance.getTransform().getModelMatrix().get(j*16, modelMxBuffer);
            }

            int meshVBOIdx = mesh.VBOi;

            // Bind model matrix buffer and send model matrices to GPU
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + MODEL_MATRIX_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, modelMxBuffer.limit() * Float.BYTES, modelMxBuffer, GL_STATIC_DRAW);

            // Setup model matrix attribute pointers to shadow shader
            setupInstancedMat4Attribute(gl, modelMxShadowLoc);

            // Set vertex position data to shadow shader
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + VERTEX_DATA_VBO]);
            gl.glEnableVertexAttribArray(shadowPositionLoc);
            gl.glVertexAttribPointer(shadowPositionLoc, 3, GL_FLOAT, false, 0, 0);

            // Bind mesh index buffer and draw
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIdx + INDEX_DATA_VBO]);
            gl.glDrawElementsInstanced(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0, instances.size());
        }
        
        // ----------------- NORMAL RENDERING -----------------
        
        gl.glCullFace(GL_BACK);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        // Reset viewport
        resizeViewPort(drawable);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        gl.glUseProgram(shaderProgram);
        for (MeshData mesh : meshData) {

            int meshVBOIdx = mesh.VBOi;

            List<Instance> instances = meshInstances.get(mesh.id);

            // Skip if there are no instances of this mesh
            if (instances == null || instances.size() == 0) {
                continue;
            }

            FloatBuffer modelMxBuffer = Buffers.newDirectFloatBuffer(instances.size() * 16);
            FloatBuffer materialBuffer = Buffers.newDirectFloatBuffer(13 * instances.size());
            int[] textureIndices = new int[instances.size()];
            for (int j = 0; j < instances.size(); ++j) {
                Instance instance = instances.get(j);
                // Put model matrix into buffer
                instance.getTransform().getModelMatrix().get(j*16, modelMxBuffer);

                // Get material data
                Material material = materials.get(instance.getMaterialID());
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


            // Bind model matrix buffer and send model matrices to GPU
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + MODEL_MATRIX_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, modelMxBuffer.limit() * Float.BYTES, modelMxBuffer, GL_STATIC_DRAW);

            // Bind mesh vertex data buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + VERTEX_DATA_VBO]);

            // Set vertex position data to shader
            gl.glEnableVertexAttribArray(positionLoc);
            gl.glVertexAttribPointer(positionLoc, 3, GL_FLOAT, false, 0, 0);

            // Set vertex texture coordinate data to shader
            gl.glEnableVertexAttribArray(texCoordLoc);
            gl.glVertexAttribPointer(texCoordLoc, 2, GL_FLOAT, false, 0, mesh.numVertices * 3 * Float.BYTES);
            
            // Set vertex normal data to shader
            gl.glEnableVertexAttribArray(normalLoc);
            gl.glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 0, mesh.numVertices * 5 * Float.BYTES);

            // Bind model matrix buffer. 
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + MODEL_MATRIX_VBO]);

            // Setup model matrix attribute pointers
            setupInstancedMat4Attribute(gl, modelMxLoc);

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


            if (mesh.textureArrayID != -1) {
                // Bind texture index buffer and send data to GPU
                IntBuffer textureIndicesBuffer = Buffers.newDirectIntBuffer(textureIndices);
                gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + TEXTURE_INDEX_VBO]);
                gl.glBufferData(GL_ARRAY_BUFFER, textureIndicesBuffer.limit() * Integer.BYTES, textureIndicesBuffer, GL_STATIC_DRAW);

                // Bind the texture array
                gl.glActiveTexture(GL_TEXTURE0);
                gl.glBindTexture(GL_TEXTURE_2D_ARRAY, mesh.textureArrayID);
                gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "textureArray"), 0);
            } else {
                int[] defaultIndices = new int[instances.size()];
                Arrays.fill(defaultIndices, -1);
                IntBuffer textureIndicesBuffer = Buffers.newDirectIntBuffer(defaultIndices);
                gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + TEXTURE_INDEX_VBO]);
                gl.glBufferData(GL_ARRAY_BUFFER, textureIndicesBuffer.limit() * Integer.BYTES, textureIndicesBuffer, GL_STATIC_DRAW);
            }
            
            // Bind texture index information to shader
            int textureIndexLoc = gl.glGetAttribLocation(shaderProgram, "textureIdx");
            gl.glVertexAttribIPointer(textureIndexLoc, 1, GL_INT, 0, 0);
            gl.glEnableVertexAttribArray(textureIndexLoc);
            gl.glVertexAttribDivisor(textureIndexLoc, 1);

            // Bind shadow map
            gl.glActiveTexture(GL_TEXTURE1);
            gl.glBindTexture(GL_TEXTURE_2D, depthMapTexture);
            gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "shadowMap"), 1);
            
            
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

    private void setupInstancedMat4Attribute(GL4 gl, int location) {
        for (int i = 0; i < 4; ++i) {
            gl.glVertexAttribPointer(location + i, 4, GL_FLOAT, false, 16 * 4, 16 * i);
            gl.glEnableVertexAttribArray(location + i);
            gl.glVertexAttribDivisor(location + i, 1);
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
        resizeViewPort(drawable);
    }

    private void resizeViewPort(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int viewportWidth = drawable.getSurfaceWidth();
        int viewportHeight = drawable.getSurfaceHeight();

        // Scale the viewport to match the actual window size, if the window has been
        // scaled. See issue #12.
        if (drawable instanceof Component) {
            Component comp = (Component) drawable;
            AffineTransform at = comp.getGraphicsConfiguration().getDefaultTransform();
            float sx = (float) at.getScaleX(), sy = (float) at.getScaleY();
            viewportWidth = (int) (viewportWidth * sx);
            viewportHeight = (int) (viewportHeight * sy);
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

        // Generate VBOs
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
            meshData.add(new MeshData(mesh.getID(), meshVBOIdx, mesh.getNumVertices(), mesh.getNumIndices(), mesh.getMaterial(), meshArrayTextureId));

            // Send vertex position, texture coordinate and normal data to GPU.
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIdx + VERTEX_DATA_VBO]);
            gl.glBufferData(GL_ARRAY_BUFFER, vDataBuffer.limit() * Float.BYTES, vDataBuffer, GL_STATIC_DRAW);

            // Send vertex indices to GPU.
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIdx + INDEX_DATA_VBO]);
            gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.limit() * Integer.BYTES, indicesBuffer,
                    GL_STATIC_DRAW);
        }

        // Set the meshes and textures to null to free up memory, because the data is now stored in
        // the VBOs on the GPU
        meshes = null;
        textures = null;
    }

    private int loadMeshTextures(Mesh mesh, GL4 gl) {
        List<Texture> meshTextures = textures.stream()
                .filter(t -> mesh.getTextureIDs().contains(t.getID()))
                .collect(Collectors.toList());

        if (meshTextures.size() == 0) {
            return -1;
        }

        // Create texture array
        int[] textureArrayID = new int[1];
        gl.glGenTextures(1, textureArrayID, 0);
        gl.glBindTexture(GL_TEXTURE_2D_ARRAY, textureArrayID[0]);
        gl.glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, meshTextures.get(0).getWidth(), meshTextures.get(0).getHeight(), meshTextures.size());

        for (int i = 0; i < meshTextures.size(); ++i) {
            Texture texture = meshTextures.get(i);
            gl.glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, texture.getWidth(), texture.getHeight(), 1, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, Buffers.newDirectIntBuffer(texture.getPixels()));
        }

        // Set texture parameters
        for (var entry : mesh.getTextureParameters().entrySet()) {
            gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, entry.getKey(), entry.getValue());
        }

        return textureArrayID[0];
    }

    private void setupDepthBuffer(GL4 gl) {
        int[] depthMap = new int[1];
        gl.glGenFramebuffers(1, depthMap, 0);   
        depthMapFBO = depthMap[0];

        int[] depthMapTextureArray = new int[1];
        gl.glGenTextures(1, depthMapTextureArray, 0);
        depthMapTexture = depthMapTextureArray[0];
        gl.glBindTexture(GL_TEXTURE_2D, depthMapTexture);

        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor, 0);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMapTexture, 0);
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);

        int error = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (error != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Framebuffer error: " + error);
        } else {
            System.out.println("Framebuffer complete");
        }
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
        this.globalAmbient.set(globalAmbient);
    }

    public void setGlobalAmbient(Vector4f globalAmbient) {
        this.globalAmbient.set(globalAmbient);
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public void setPositionalLights(List<PositionalLight> posLights) {
        this.posLights = posLights;
    }

    public void setTextures(List<Texture> textures) {
        this.textures = textures;
    }
}
