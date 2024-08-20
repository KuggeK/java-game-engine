package io.github.kuggek.engine.rendering.opengl;

import static com.jogamp.opengl.GL4.*;

import java.awt.Frame;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;

import io.github.kuggek.engine.rendering.RenderScene;
import io.github.kuggek.engine.rendering.opengl.shaders.BlinnPhongShaderProgram;
import io.github.kuggek.engine.rendering.opengl.shaders.NormalMapShaderProgram;
import io.github.kuggek.engine.rendering.opengl.shaders.ShaderProgram;
import io.github.kuggek.engine.rendering.opengl.shaders.ShadowMapShaderProgram;
import io.github.kuggek.engine.rendering.opengl.shaders.UnlitShaderProgram;

public class OpenGLBindings implements GLEventListener {
    
    private RenderScene scene;

    private List<ShaderProgram> shaderPrograms;

    // Shader programs that have been added since the last frame. 
    // The integer key is the index at which the program is to be added.
    private List<Entry<Integer, ShaderProgram>> newShaderPrograms;

    private GLLocations locations;
    private RenderPassVariables renderVariables;

    public OpenGLBindings(RenderScene scene) {
        this.scene = scene;
        locations = new GLLocations(16);

        shaderPrograms = new ArrayList<>();
        newShaderPrograms = new ArrayList<>();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        renderVariables = new RenderPassVariables();
        locations.init(gl);

        // Load shaders
        loadNativeShaders(gl);

        // Set up OpenGL state
        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);
        gl.glDepthFunc(GL_LEQUAL);
    }

    private void loadNativeShaders(GL4 gl) {
        try {
            shaderPrograms.add(new ShadowMapShaderProgram(1024, 1024));
            shaderPrograms.add(new BlinnPhongShaderProgram());
            shaderPrograms.add(new UnlitShaderProgram());
            shaderPrograms.add(new NormalMapShaderProgram());

            for (ShaderProgram program : shaderPrograms) {
                program.initialize(gl, locations);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GLException("Failed to load shaders");
        }
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.1f, 0.4f, 0.6f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        locations.update(gl);

        // Add new shader programs
        for (Entry<Integer, ShaderProgram> entry : newShaderPrograms) {
            try {
                entry.getValue().initialize(gl, locations);
                shaderPrograms.add(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                System.out.println("Failed to load shader program: " + entry.getValue().getClass().getName());
            }
        }
        newShaderPrograms.clear();

        // Check if the view matrix is set
        if (scene.getViewMatrix() == null) {
            return;
        }

        // Set render variables
        renderVariables.setViewMatrix(scene.getViewMatrix());
        renderVariables.setProjectionMatrix(scene.getProjectionMatrix());
        renderVariables.setLightSpaceMatrix(scene.getLightSpaceMatrix());
        // TODO pass render instances through culling
        renderVariables.setInstancesToRender(scene.getRenderInstances());


        // RENDER PASSES
        for (ShaderProgram program : shaderPrograms) {
            program.render(gl, scene, locations, renderVariables);
            resizeViewPort(drawable);
        }

        // Clean up
        gl.glUseProgram(0);
        renderVariables.reset();
    }

    /**
     * Add a shader program to the list of programs to be rendered. The program will be
     * rendered in the order it was added.
     * @param program The shader program to add
     * @param index The index to add the program at. The index value is clamped to the
     * within the bounds of the list.
     */
    public void addShaderProgram(ShaderProgram program, int index) {
        if (index < 0) {
            index = 0;
        } else if (index > shaderPrograms.size()) {
            index = shaderPrograms.size();
        }
        newShaderPrograms.add(Map.entry(index, program));
    }

    /**
     * Add a shader program to the end of the list of programs to be rendered.
     * @param program The shader program to add
     */
    public void addShaderProgram(ShaderProgram program) {
        addShaderProgram(program, shaderPrograms.size());
    }

    public List<ShaderProgram> getShaderPrograms() {
        return shaderPrograms;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
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
        if (drawable instanceof Frame) {
            Frame frame = (Frame) drawable;
            AffineTransform at = frame.getGraphicsConfiguration().getDefaultTransform();
            float sx = (float) at.getScaleX(), sy = (float) at.getScaleY();
            System.out.println("sx: " + sx + ", sy: " + sy);
            viewportWidth = (int) (viewportWidth * sx);
            viewportHeight = (int) (viewportHeight * sy);
        }
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
        scene.updateProjectionMatrix((float) viewportWidth / viewportHeight);
    }

    public RenderScene getRenderScene() {
        return scene;
    }

    public GLLocations getLocations() {
        return locations;
    }

    public void setScene(RenderScene scene) {
        this.scene = scene;
    }
}
