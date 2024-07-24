package kugge.rendering.graphics.opengl;

import static com.jogamp.opengl.GL4.*;

import java.awt.Component;
import java.awt.geom.AffineTransform;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;

import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.shaders.BlinnPhongShaderProgram;
import kugge.rendering.graphics.opengl.shaders.NormalMapShaderProgram;
import kugge.rendering.graphics.opengl.shaders.ShaderProgram;
import kugge.rendering.graphics.opengl.shaders.ShadowMapShaderProgram;
import kugge.rendering.graphics.opengl.shaders.SkyBoxShaderProgram;
import kugge.rendering.graphics.opengl.shaders.UnlitShaderProgram;

public class OpenGLBindings implements GLEventListener {
    
    private RenderScene scene;

    private ShaderProgram blinnPhongProgram;
    private ShaderProgram shadowMapProgram;
    private ShaderProgram skyboxProgram;
    private ShaderProgram unlitProgram;
    private ShaderProgram normalMapProgram;

    private GLLocations locations;
    private RenderPassVariables renderVariables;

    public OpenGLBindings(RenderScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        locations = new GLLocations(gl, 16);
        renderVariables = new RenderPassVariables();

        // Load shaders
        loadShaders(gl);

        // Set up OpenGL state
        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);
        gl.glDepthFunc(GL_LEQUAL);
    }

    private void loadShaders(GL4 gl) {
        try {
            blinnPhongProgram = new BlinnPhongShaderProgram(gl, "basic.vert", "basic.frag");
            shadowMapProgram = new ShadowMapShaderProgram(gl, locations, "shadow.vert", "shadow.frag");
            skyboxProgram = new SkyBoxShaderProgram(gl, "skybox.vert", "skybox.frag", scene.getSkyBox());
            unlitProgram = new UnlitShaderProgram(gl, locations, "unlit.vert", "unlit.frag");
            normalMapProgram = new NormalMapShaderProgram(gl, "normal.vert", "normal.frag");
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

        // Set render variables
        renderVariables.setViewMatrix(scene.getViewMatrix());
        renderVariables.setProjectionMatrix(scene.getProjectionMatrix());
        renderVariables.setLightSpaceMatrix(scene.getLightSpaceMatrix());
        // TODO pass render instances through culling
        renderVariables.setInstancesToRender(scene.getRenderInstances());

        // RENDER PASSES

        // 1. Shadow map pass
        shadowMapProgram.render(gl, scene, locations, renderVariables);

        // Reset the viewport to the window size after the shadow map pass
        resizeViewPort(drawable);
        
        // 2. Main render pass
        blinnPhongProgram.render(gl, scene, locations, renderVariables);

        // 3. Unlit pass
        unlitProgram.render(gl, scene, locations, renderVariables);

        // 4. Normal map pass
        normalMapProgram.render(gl, scene, locations, renderVariables);

        // 5. Skybox pass
        skyboxProgram.render(gl, scene, locations, renderVariables);

        // Clean up
        gl.glUseProgram(0);
        renderVariables.reset();
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
        if (drawable instanceof Component) {
            Component comp = (Component) drawable;
            AffineTransform at = comp.getGraphicsConfiguration().getDefaultTransform();
            float sx = (float) at.getScaleX(), sy = (float) at.getScaleY();
            viewportWidth = (int) (viewportWidth * sx);
            viewportHeight = (int) (viewportHeight * sy);
        }
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
        scene.updateProjectionMatrix((float) viewportWidth / viewportHeight);
    }

    public RenderScene getRenderScene() {
        return scene;
    }


}
