package kugge.rendering.graphics.opengl.shaders;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GL4.*;

import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.meshes.Mesh;
import kugge.rendering.core.objects.rendering.RenderInstance;
import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.GLLocations;
import kugge.rendering.graphics.opengl.shaders.Shaders.Shader;

public class BlinnPhongShaderProgram implements ShaderProgram {

    private int programID;

    private FloatBuffer matrixValueHelper = Buffers.newDirectFloatBuffer(16);

    private final int MAX_N_LIGHTS = 20;

    public BlinnPhongShaderProgram(GL4 gl, String vertexShaderFile, String fragmentShaderFile) throws Exception {
        Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER, vertexShaderFile),
            new Shader(GL_FRAGMENT_SHADER, fragmentShaderFile)
        };
        this.programID = Shaders.loadShaders(shaders, gl);
    }

    @Override
    public int getProgramID() {
        return programID;
    }

    @Override
    public void render(GL4 gl, RenderScene scene, GLLocations locations) {
        gl.glUseProgram(programID);
        gl.glBindVertexArray(locations.getMeshVAO());
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Set uniforms that are static for all instances
        int projectionMatrixLocation = unif(gl, "projectionMx");
        gl.glUniformMatrix4fv(projectionMatrixLocation, 1, false, scene.getProjectionMatrix().get(matrixValueHelper));

        int viewMatrixLocation = unif(gl, "viewMx");
        gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, scene.getViewMatrix().get(matrixValueHelper));

        // Set the light uniforms
        DirectionalLight dirLight = scene.getDirectionalLight();

        if (dirLight == null) {
            dirLight = DirectionalLight.EMPTY;
        }

        gl.glUniform4fv(unif(gl, "dirLight.ambient"), 1, dirLight.getAmbient().get(matrixValueHelper));
        gl.glUniform4fv(unif(gl, "dirLight.diffuse"), 1, dirLight.getDiffuse().get(matrixValueHelper));
        gl.glUniform4fv(unif(gl, "dirLight.specular"), 1, dirLight.getSpecular().get(matrixValueHelper));
        gl.glUniform3fv(unif(gl, "dirLight.direction"), 1, dirLight.getDirection().get(matrixValueHelper));

        gl.glUniform4fv(unif(gl, "globalAmbient"), 1, scene.getGlobalAmbient().get(matrixValueHelper));

        Vector4f viewPos = new Vector4f();
        new Matrix4f(scene.getViewMatrix()).invert().getColumn(3, viewPos);
        gl.glUniform3fv(unif(gl, "viewPos"), 1, viewPos.get(matrixValueHelper));

        // Set positional light unifforms
        List<PositionalLight> posLights = scene.getPositionalLights();
        int nLightsLoc = unif(gl, "nLights");
        gl.glUniform1i(nLightsLoc, Math.min(MAX_N_LIGHTS, posLights.size()));
        for (int i = 0; i < posLights.size(); ++i) {
            PositionalLight light = posLights.get(i);
            gl.glUniform4fv(unif(gl, "posLights[" + i + "].ambient"), 1, light.getAmbient().get(matrixValueHelper));
            gl.glUniform4fv(unif(gl, "posLights[" + i + "].diffuse"), 1, light.getDiffuse().get(matrixValueHelper));
            gl.glUniform4fv(unif(gl, "posLights[" + i + "].specular"), 1, light.getSpecular().get(matrixValueHelper));
            gl.glUniform4fv(unif(gl, "posLights[" + i + "].position"), 1, light.getPosition().get(matrixValueHelper));
            gl.glUniform1f(unif(gl, "posLights[" + i + "].attenuation.constant"), light.getConstant());
            gl.glUniform1f(unif(gl, "posLights[" + i + "].attenuation.linear"), light.getLinear());
            gl.glUniform1f(unif(gl, "posLights[" + i + "].attenuation.quadratic"), light.getQuadratic());
            gl.glUniform1f(unif(gl, "posLights[" + i + "].attenuation.radius"), light.getRadius());
        }

        // Bind the shadow map
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, locations.getShadowMapTexture());
        gl.glUniform1i(unif(gl, "shadowMap"), 1);

        // Render all instances
        int previousMaterialID = -1;
        int previousMeshID = -1;
        int previousTextureID = -1;
        for (RenderInstance instance : scene.getRenderInstances().parallelStream().filter(i -> passesCondition(i)).toList()) {
                    
            if (previousMaterialID != instance.getMaterialID()) {
                Material mat = scene.getMaterial(instance.getMaterialID());
                gl.glUniform4fv(unif(gl, "material.ambient"), 1, mat.getAmbient().get(matrixValueHelper));
                gl.glUniform4fv(unif(gl, "material.diffuse"), 1, mat.getDiffuse().get(matrixValueHelper));
                gl.glUniform4fv(unif(gl, "material.specular"), 1, mat.getSpecular().get(matrixValueHelper));
                gl.glUniform1f(unif(gl, "material.shininess"), mat.getShininess());
                previousMaterialID = instance.getMaterialID();
            }

            // If the mesh has changed, bind the new mesh and set the vertex attribute pointers        
            Mesh mesh = scene.getMesh(instance.getMeshID());

            // If mesh doesn't exist, skip this instance
            if (mesh == null) {
                continue;
            }

            if (previousMeshID != instance.getMeshID()) {
                // Load the mesh into the GPU if it hasn't been loaded yet
                if (locations.getMeshVertexLoc(mesh.getID()) == -1) {
                    locations.loadMesh(gl, mesh);
                }

                gl.glBindBuffer(GL_ARRAY_BUFFER, locations.getMeshVertexLoc(mesh.getID()));
                gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
                gl.glEnableVertexAttribArray(0);
                gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, mesh.getPositions().length * Float.BYTES);
                gl.glEnableVertexAttribArray(1);
                gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, (mesh.getPositions().length + mesh.getTextureCoords().length) * Float.BYTES);
                gl.glEnableVertexAttribArray(2);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, locations.getMeshIndexLoc(mesh.getID()));
                previousMeshID = mesh.getID();
            }

            Texture texture = null;
            if (instance.isTexturingEnabled()){
                texture = scene.getTexture(instance.getTextureID());
            }

            if (texture != null) {
                gl.glUniform1i(unif(gl, "textured"), 1);

                // If the texture has changed, bind the new texture
                if (previousTextureID != texture.getID()) {
                    // Load the texture into the GPU if it hasn't been loaded yet
                    if (locations.getTextureLocation(texture.getID()) == -1) {
                        locations.loadTexture(gl, texture);
                    }

                    gl.glActiveTexture(GL_TEXTURE0);
                    gl.glBindTexture(GL_TEXTURE_2D, locations.getTextureLocation(texture.getID()));
                    gl.glUniform1i(unif(gl, "instanceTexture"), 0);
                    previousTextureID = instance.getTextureID();
                }
            } else {
                gl.glUniform1i(unif(gl, "textured"), 0);
            }

            // Set the model matrix
            gl.glUniformMatrix4fv(unif(gl, "modelMx"), 1, false, instance.getModelMatrix().get(matrixValueHelper));

            // Draw the mesh
            gl.glDrawElements(GL_TRIANGLES, mesh.getNumIndices(), GL_UNSIGNED_INT, 0);
        }
    }


    /**
     * Helper method to get the location of a uniform in the shader program. 
     * Used to make the code less bloated.
     * @param gl The OpenGL context
     * @param name The name of the uniform
     * @return The location of the uniform
     */
    private int unif(GL4 gl, String name) {
        int loc = gl.glGetUniformLocation(programID, name);
        if (loc == -1) {
            System.err.println("Uniform \"" + name + "\" not found in shader program " + programID);
        }
        return loc;
    }
    
    @Override
    public void cleanup(GL4 gl) {
        gl.glDeleteProgram(programID);
    }

    public boolean passesCondition(RenderInstance instance) {
        return instance.isLit();
    }
}
