package kugge.rendering.graphics.opengl.shaders;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import org.joml.Vector4f;

import com.jogamp.opengl.GLException;

import kugge.rendering.core.objects.Texture;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.meshes.Mesh;
import kugge.rendering.core.objects.rendering.RenderInstance;
import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.GLLocations;
import kugge.rendering.graphics.opengl.shaders.Shaders.Shader;

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
    public void render(GL4 gl, RenderScene scene, GLLocations locations) {
        gl.glUseProgram(programID);
        gl.glBindVertexArray(locations.getMeshVAO());
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Set uniforms that are static for all instances
        int projectionMatrixLocation = unif(gl, "projectionMx");
        gl.glUniformMatrix4fv(projectionMatrixLocation, 1, false, scene.getProjectionMatrix().get(matrixValueHelper));

        int viewMatrixLocation = unif(gl, "viewMx");
        gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, scene.getViewMatrix().get(matrixValueHelper));

        gl.glActiveTexture(GL_TEXTURE1);
        gl.glUniform1i(unif(gl, "instanceTexture"), 1);

        // Render all instances
        int previousMaterialID = -1;
        int previousMeshID = -1;
        int previousTextureID = -1;
        for (RenderInstance instance : scene.getRenderInstances().stream().filter(i -> passesCondition(i)).toList()) {
        
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
                gl.glUniform1i(unif(gl, "textured"), 1);

                if (previousTextureID != texture.getID()) {
                    if (locations.getTextureLocation(texture.getID()) == -1) {
                        locations.loadTexture(gl, texture);
                    }

                    gl.glBindTexture(GL_TEXTURE_2D, locations.getTextureLocation(texture.getID()));
                    previousTextureID = texture.getID();
                }

                for (var param : instance.getTextureParameters().entrySet()) {
                    System.out.println(param.getKey() + " " + param.getValue());
                    gl.glTexParameteri(GL_TEXTURE_2D, param.getKey(), param.getValue());
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
