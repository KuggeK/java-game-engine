package kugge.rendering.graphics.opengl.shaders;

import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import kugge.rendering.core.objects.meshes.Mesh;
import kugge.rendering.core.objects.rendering.RenderInstance;
import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.GLLocations;
import kugge.rendering.graphics.opengl.shaders.Shaders.Shader;

public class ShadowMapShaderProgram implements ShaderProgram {

    private int programID;
    private int shadowFBO;
    private int shadowTexture;

    private final int SHADOW_WIDTH;
    private final int SHADOW_HEIGHT;

    private FloatBuffer matrixValueHelper = Buffers.newDirectFloatBuffer(16);

    /**
     * Create a shadow map shader program with a custom shadow map size.
     * @param gl The OpenGL context
     * @param vertexShaderFile The vertex shader file name
     * @param fragmentShaderFile The fragment shader file name
     * @param shadowWidth The width of the shadow map
     * @param shadowHeight The height of the shadow map
     * @throws Exception If the shader program could not be created
     */
    public ShadowMapShaderProgram(GL4 gl, GLLocations locations, String vertexShaderFile, String fragmentShaderFile, int shadowWidth, int shadowHeight) throws Exception {
        Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER, vertexShaderFile),
            new Shader(GL_FRAGMENT_SHADER, fragmentShaderFile)
        };

        this.programID = Shaders.loadShaders(shaders, gl);
        SHADOW_WIDTH = shadowWidth;
        SHADOW_HEIGHT = shadowHeight;

        // Create the shadow FBO
        int[] FBOs = new int[1];
        gl.glGenFramebuffers(1, FBOs, 0);
        this.shadowFBO = FBOs[0];

        // Create the shadow texture
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        this.shadowTexture = textures[0];   
        gl.glBindTexture(GL_TEXTURE_2D, shadowTexture);
        locations.setShadowMapTexture(shadowTexture);

        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor, 0);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowFBO);
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowTexture, 0);
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);

        int error = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (error != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Framebuffer error: " + error);
        } else {
            System.out.println("Framebuffer complete");
        }

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Create a shadow map shader program with a default shadow map size of 1024x1024.
     * @param gl The OpenGL context
     * @param vertexShaderFile The vertex shader file name
     * @param fragmentShaderFile The fragment shader file name
     * @throws Exception If the shader program could not be created
     */
    public ShadowMapShaderProgram(GL4 gl, GLLocations locations, String vertexShaderFile, String fragmentShaderFile) throws Exception {
        this(gl, locations, vertexShaderFile, fragmentShaderFile, 1024, 1024);
    }

    @Override
    public int getProgramID() {
        return programID;
    }

    @Override
    /**
     * Renders the scene depth to a shadow map texture, from the perspective of the directional light.
     * Viewport needs to be set to the proper size after calling this method.
     * @param gl The OpenGL context
     * @param scene The scene to render
     * @param locations The OpenGL locations
     */
    public void render(GL4 gl, RenderScene scene, GLLocations locations) {
        gl.glUseProgram(programID);
        gl.glBindVertexArray(locations.getMeshVAO());

        if (scene.getDirectionalLight() == null) {
            clearTexture(gl);
            return;
        }

        int modelMxLoc = gl.glGetUniformLocation(programID, "modelMx");
        int positionLoc = gl.glGetAttribLocation(programID, "vPosition");

        int previousMeshID = -1;

        // Set the viewport to the shadow map size and bind the shadow map
        gl.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowFBO);
        gl.glClear(GL_DEPTH_BUFFER_BIT);



        // Set light space matrix since it is the same for all instances
        gl.glUniformMatrix4fv(unif(gl, "lightSpaceMx"), 1, false, scene.getLightSpaceMatrix().get(matrixValueHelper));

        for (RenderInstance instance : scene.getRenderInstances().stream().filter(i -> passesCondition(i)).toList()) {
            Mesh mesh = scene.getMesh(instance.getMeshID());

            if (mesh == null) {
                continue;
            }

            // If this instance has the same mesh as the previous one, we don't need to bind the VBO again
            if (previousMeshID != mesh.getID()) {

                if (locations.getMeshVertexLoc(mesh.getID()) == -1) {
                    locations.loadMesh(gl, mesh);
                }

                gl.glBindBuffer(GL_ARRAY_BUFFER, locations.getMeshVertexLoc(previousMeshID));
                gl.glVertexAttribPointer(positionLoc, 3, GL_FLOAT, false, 0, 0);
                gl.glEnableVertexAttribArray(positionLoc);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, locations.getMeshIndexLoc(previousMeshID));
                previousMeshID = mesh.getID();
            } 

            // Set the model matrix
            instance.getModelMatrix().get(matrixValueHelper);
            gl.glUniformMatrix4fv(modelMxLoc, 1, false, matrixValueHelper);
            
            // Draw the mesh
            gl.glDrawElements(GL_TRIANGLES, mesh.getNumIndices(), GL_UNSIGNED_INT, 0);
        }

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    /**
     * Clear the shadow map texture to a white color so no shadows are cast.
     * @param gl The OpenGL context
     * @param locations The OpenGL locations
     */
    private void clearTexture(GL4 gl) {
        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowFBO);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Helper method to get the location of a uniform in the shader program. 
     * Used to make the code less bloated.
     * @param gl The OpenGL context
     * @param name The name of the uniform
     * @return The location of the uniform
     */
    private int unif(GL4 gl, String name) {
        return gl.glGetUniformLocation(programID, name);
    }
    
    @Override
    public void cleanup(GL4 gl) {
        gl.glDeleteProgram(programID);
        gl.glDeleteFramebuffers(1, new int[] {shadowFBO}, 0);
        gl.glDeleteTextures(1, new int[] {shadowTexture}, 0);
    }

    public boolean passesCondition(RenderInstance instance) {
        return instance.castsShadows();
    }
}
