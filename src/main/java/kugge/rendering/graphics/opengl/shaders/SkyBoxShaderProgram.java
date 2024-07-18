package kugge.rendering.graphics.opengl.shaders;

import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

import kugge.rendering.core.objects.SkyBox;
import kugge.rendering.core.objects.rendering.RenderScene;
import kugge.rendering.graphics.opengl.GLLocations;
import kugge.rendering.graphics.opengl.shaders.Shaders.Shader;

public class SkyBoxShaderProgram implements ShaderProgram {

    // VBO for the skyboxes vertices
    private int VBO;
    private int VAO;
    private int cubeMapID;
    private int programID;

    private FloatBuffer mxValueBuffer = Buffers.newDirectFloatBuffer(16);
    private Matrix4f viewMxHelper = new Matrix4f();

    /**
     * Create a new skybox shader program.
     * @param gl The OpenGL context
     * @param vertexShaderFile The vertex shader file name. 
     * @param fragmentShaderFile The fragment shader file. 
     * @param skyBoxVBO The VBO for the skybox vertices
     * @throws Exception If the shader program could not be created.
     */
    public SkyBoxShaderProgram(GL4 gl, String vertexShaderFile, String fragmentShaderFile, SkyBox skybox) throws Exception {
        Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER, vertexShaderFile),
            new Shader(GL_FRAGMENT_SHADER, fragmentShaderFile)
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

    @Override
    public int getProgramID() {
        return programID;
    }

    @Override
    public void render(GL4 gl, RenderScene scene, GLLocations locations) {
        gl.glDepthMask(false);  

        gl.glUseProgram(programID);
        gl.glBindVertexArray(VAO);

        int viewMxLoc = gl.glGetUniformLocation(programID, "viewMx");
        int projectionMxLoc = gl.glGetUniformLocation(programID, "projectionMx");

        // View matrix without rotation, so the skybox does not rotate with the camera
        viewMxHelper.identity().set3x3(scene.getViewMatrix());
        gl.glUniformMatrix4fv(viewMxLoc, 1, false, viewMxHelper.get(mxValueBuffer));
        gl.glUniformMatrix4fv(projectionMxLoc, 1, false, scene.getProjectionMatrix().get(mxValueBuffer));

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
