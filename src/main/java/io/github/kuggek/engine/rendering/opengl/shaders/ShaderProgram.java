package io.github.kuggek.engine.rendering.opengl.shaders;

import com.jogamp.opengl.GL4;

import io.github.kuggek.engine.rendering.RenderScene;
import io.github.kuggek.engine.rendering.opengl.GLLocations;
import io.github.kuggek.engine.rendering.opengl.RenderPassVariables;

public interface ShaderProgram {

    /**
     * Initializes the shader program. This method should be called before the shader program is used.
     * @param gl The OpenGL context
     * @param locations The locations of the uniforms in the shader program
     * @throws Exception If the shader program could not be initialized
     */
    void initialize(GL4 gl, GLLocations locations) throws Exception;

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
    void render(GL4 gl, RenderScene scene, GLLocations locations, RenderPassVariables renderVariables);

    /**
     * Clean up the shader program. This should be called when the shader program is no longer needed.
     * Deletes all resources associated with the shader program along with the program itself.
     * Do not attempt to use the shader program after calling this method.
     * @param gl
     */
    void cleanup(GL4 gl);
}
