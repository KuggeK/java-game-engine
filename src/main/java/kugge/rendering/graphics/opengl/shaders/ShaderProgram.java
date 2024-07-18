package kugge.rendering.graphics.opengl.shaders;

import com.jogamp.opengl.GL4;

import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.GLLocations;

public interface ShaderProgram {

    /**
     * Get the program ID of the shader program. This corresponds to the actual OpenGL ID or name 
     * of the shader program.
     * @return
     */
    int getProgramID();

    /**
     * Binds the shader program to the OpenGL context, sets the uniforms and renders the scene
     * using this shader program.
     * @param gl
     * @param scene
     */
    void render(GL4 gl, RenderScene scene, GLLocations locations);

    /**
     * Clean up the shader program. This should be called when the shader program is no longer needed.
     * Deletes all resources associated with the shader program along with the program itself.
     * Do not attempt to use the shader program after calling this method.
     * @param gl
     */
    void cleanup(GL4 gl);
}
