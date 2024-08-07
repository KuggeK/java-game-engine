package io.github.kuggek.engine.rendering.opengl.shaders;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Vector4f;

import com.jogamp.opengl.GLException;

import io.github.kuggek.engine.rendering.RenderScene;
import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.RenderInstance;
import io.github.kuggek.engine.rendering.objects.Texture;
import io.github.kuggek.engine.rendering.opengl.GLLocations;
import io.github.kuggek.engine.rendering.opengl.RenderPassVariables;
import io.github.kuggek.engine.rendering.opengl.shaders.Shaders.Shader;

public class UnlitShaderProgram implements ShaderProgram {

    private int programID;

    private FloatBuffer matrixValueHelper = Buffers.newDirectFloatBuffer(16);

    public UnlitShaderProgram(GL4 gl, GLLocations locations, String vertexShaderFile, String fragmentShaderFile) {
        Shader[] shaders = new Shader[] {
            new Shader(GL4.GL_VERTEX_SHADER, vertexShaderFile),
            new Shader(GL4.GL_FRAGMENT_SHADER, fragmentShaderFile)
        };

        try {
            this.programID = Shaders.loadShaders(shaders, gl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GLException("Failed to load shaders", e);
        }
    }

    @Override
    public void cleanup(GL4 gl) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getProgramID() {
        return programID;
    }

    @Override
    public void render(GL4 gl, RenderScene scene, GLLocations locations, RenderPassVariables renderVariables) {
        List<RenderInstance> instancesToRender = renderVariables.getInstancesToRender().stream().filter(i -> passesCondition(i)).toList();
        if (instancesToRender.isEmpty()) {
            return;
        }

        gl.glUseProgram(programID);
        gl.glBindVertexArray(locations.getMeshVAO());
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Set uniforms that are static for all instances
        int projectionMatrixLocation = unif(gl, "projectionMx");
        gl.glUniformMatrix4fv(projectionMatrixLocation, 1, false, renderVariables.getProjectionMatrix().get(matrixValueHelper));

        int viewMatrixLocation = unif(gl, "viewMx");
        gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, renderVariables.getViewMatrix().get(matrixValueHelper));

        int instanceTextureUnit = 1;
        gl.glUniform1i(unif(gl, "instanceTexture"), instanceTextureUnit);

        // Render all instances
        int previousMaterialID = -1;
        int previousMeshID = -1;
        for (RenderInstance instance : instancesToRender) {
        
            if (previousMaterialID != instance.getMaterialID()) {
                Material mat = scene.getMaterial(instance.getMaterialID());

                // Calculate the average color of the material. 
                Vector4f color = new Vector4f(mat.getAmbient());
                color.add(mat.getDiffuse());
                color.add(mat.getSpecular());
                color.div(3.0f);

                gl.glUniform4fv(unif(gl, "color"), 1, color.get(matrixValueHelper));
            }

            Mesh mesh = scene.getMesh(instance.getMeshID());

            if (mesh == null) {
                continue;
            }

            if (previousMeshID != mesh.getID()) {
                if (locations.getMeshVertexLoc(mesh.getID()) == -1) {
                    locations.loadMesh(gl, mesh);
                }

                gl.glBindBuffer(GL_ARRAY_BUFFER, locations.getMeshVertexLoc(mesh.getID()));
                gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
                gl.glEnableVertexAttribArray(0);
                gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, mesh.getPositions().length * Float.BYTES);
                gl.glEnableVertexAttribArray(1);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, locations.getMeshIndexLoc(mesh.getID()));
                previousMeshID = mesh.getID();
            }

            Texture texture = null;
            if (instance.isTexturingEnabled()) {
                texture = scene.getTexture(instance.getTextureID());
            }

            if (texture != null) {
                if (locations.getTextureLocation(texture.getID()) == -1) {
                    locations.loadTexture(gl, texture);
                }

                boolean textureActive = locations.setupTextureUnit(gl, instance, instanceTextureUnit);

                if (!textureActive) {
                    gl.glUniform1i(unif(gl, "textured"), 0);
                } else {
                    gl.glUniform1i(unif(gl, "textured"), 1);
                }
            } else {
                gl.glUniform1i(unif(gl, "textured"), 0);
            }

            gl.glUniformMatrix4fv(unif(gl, "modelMx"), 1, false, instance.getModelMatrix().get(matrixValueHelper));

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

    public boolean passesCondition(RenderInstance instance) {
        return !instance.isLit();
    }
}
