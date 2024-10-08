package io.github.kuggek.engine.rendering.opengl.shaders;

import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import io.github.kuggek.engine.rendering.RenderScene;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.opengl.GLLocations;
import io.github.kuggek.engine.rendering.opengl.RenderPassVariables;
import io.github.kuggek.engine.rendering.opengl.shaders.Shaders.Shader;

public class SkyBoxShaderProgram implements ShaderProgram {

    private SkyBox skybox;
    private boolean skyboxChanged = false;

    // VBO for the skyboxes vertices
    private int VBO;
    private int VAO;
    private int cubeMapID;
    private int programID;

    private FloatBuffer mxValueBuffer = Buffers.newDirectFloatBuffer(16);
    private Matrix4f viewMxHelper = new Matrix4f();

    private final String VERTEX_SHADER_FILE = "skybox.vert";
    private final String FRAGMENT_SHADER_FILE = "skybox.frag";

    /**
     * Create a new skybox shader program.
     * @param skybox The skybox to render
     */
    public SkyBoxShaderProgram(SkyBox skybox) {
        this.skybox = skybox;
    }

    @Override
    public void initialize(GL4 gl, GLLocations locations) throws Exception {
        Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER, VERTEX_SHADER_FILE),
            new Shader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_FILE)
        };
        this.programID = Shaders.loadShaders(shaders, gl);

        // Create the VAO for the skybox
        int[] VAOs = new int[1];
        gl.glGenVertexArrays(1, VAOs, 0);
        this.VAO = VAOs[0];

        // Bind the VAO
        gl.glBindVertexArray(VAO);

        // Create the VBO for the skybox vertices
        int[] VBOs = new int[1];
        gl.glGenBuffers(1, VBOs, 0);
        this.VBO = VBOs[0];

        // Bind the VBO
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(SkyBox.VERTICES);
        gl.glBufferData(GL_ARRAY_BUFFER, SkyBox.VERTICES.length * Float.BYTES, vertices, GL_STATIC_DRAW);

        // Set the vertex attribute pointers
        int vPositionLocation = gl.glGetAttribLocation(programID, "vPosition");
        gl.glEnableVertexAttribArray(vPositionLocation);
        gl.glVertexAttribPointer(vPositionLocation, 3, GL_FLOAT, false, 0, 0);

        // Setup the texture cube map
        int[] textureIDs = new int[1];
        gl.glGenTextures(1, textureIDs, 0);
        this.cubeMapID = textureIDs[0];
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapID);

        gl.glTexStorage2D(GL_TEXTURE_CUBE_MAP, 1, GL_RGBA8, skybox.getTextureSize(), skybox.getTextureSize());

        // Load the skybox textures
        for (int i = 0; i < 6; ++i) {
            gl.glTexSubImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 
                0, 0, 0, 
                skybox.getTextureSize(), skybox.getTextureSize(), 
                GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 
                Buffers.newDirectIntBuffer(skybox.getTexture(i).getPixels()));
        }

        // Set the texture parameters
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
    }

    public void setSkybox(SkyBox skybox) {
        this.skybox = skybox;
        skyboxChanged = true;
    }

    @Override
    public int getProgramID() {
        return programID;
    }

    @Override
    public void render(GL4 gl, RenderScene scene, GLLocations locations, RenderPassVariables renderVariables) {
        if (skyboxChanged) {
            // Load the skybox textures
            for (int i = 0; i < 6; ++i) {
                gl.glTexSubImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 
                    0, 0, 0, 
                    skybox.getTextureSize(), skybox.getTextureSize(), 
                    GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 
                    Buffers.newDirectIntBuffer(skybox.getTexture(i).getPixels()));
            }
            skyboxChanged = false;
        }
        
        gl.glDepthMask(false);  

        gl.glUseProgram(programID);
        gl.glBindVertexArray(VAO);

        int viewMxLoc = gl.glGetUniformLocation(programID, "viewMx");
        int projectionMxLoc = gl.glGetUniformLocation(programID, "projectionMx");

        // View matrix without rotation, so the skybox does not rotate with the camera
        viewMxHelper.identity().set3x3(renderVariables.getViewMatrix());
        gl.glUniformMatrix4fv(viewMxLoc, 1, false, viewMxHelper.get(mxValueBuffer));
        gl.glUniformMatrix4fv(projectionMxLoc, 1, false, renderVariables.getProjectionMatrix().get(mxValueBuffer));

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapID);

        gl.glDrawArrays(GL_TRIANGLES, 0, 36);

        gl.glDepthMask(true);
    }

    public void cleanup(GL4 gl) {
        gl.glDeleteBuffers(1, new int[] { VBO }, 0);
        gl.glDeleteVertexArrays(1, new int[] { VAO }, 0);
        gl.glDeleteTextures(1, new int[] { cubeMapID }, 0);
        gl.glDeleteProgram(programID);
    }
}
