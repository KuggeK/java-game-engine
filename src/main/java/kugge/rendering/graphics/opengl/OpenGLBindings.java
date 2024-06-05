package kugge.rendering.graphics.opengl;

import com.jogamp.opengl.GL4;

import static com.jogamp.opengl.GL4.*;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import kugge.rendering.core.objects.Instance;
import kugge.rendering.core.objects.Mesh;
import kugge.rendering.graphics.opengl.Shaders.Shader;

public class OpenGLBindings implements GLEventListener {

    record MeshData(int id, int VBOi, int numVertices, int numIndices) {
    }

    private List<Mesh> meshes;
    private List<MeshData> meshData;

    private Map<Integer, List<Instance>> meshInstances;

    // Buffer for transferring matrix data to the GPU
    private FloatBuffer matrixVals = Buffers.newDirectFloatBuffer(16);
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private int[] VAO;
    private int[] VBOs;
    private int shaderProgram;
    private Shader[] shaders = new Shader[] {
            new Shader(GL_VERTEX_SHADER,
                    "C:/Users/Kilian/Documents/Programming/Java/rendering-engine/src/main/resources/shaders/basic.vert"),
            new Shader(GL_FRAGMENT_SHADER,
                    "C:/Users/Kilian/Documents/Programming/Java/rendering-engine/src/main/resources/shaders/basic.frag")
    };

    private float fov = 60f;
    private float near = 0.01f;
    private float far = 1000f;

    public OpenGLBindings(List<Mesh> meshes) {
        meshData = new ArrayList<>();
        meshInstances = new HashMap<>();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        this.meshes = meshes;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.6f, 0.2f, 1.0f);

        VAO = new int[1];
        gl.glGenVertexArrays(VAO.length, VAO, 0);

        try {
            shaderProgram = Shaders.loadShaders(shaders, gl);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle exception
            throw new RuntimeException();
        }

        loadMeshes(meshes, gl);

        // Configure depth test
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        updateProjectionMatrix((float) drawable.getSurfaceWidth() / drawable.getSurfaceHeight());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        // Clear screen
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);

        // Setup view and projection matrices
        int projectionMxLox = gl.glGetUniformLocation(shaderProgram, "projectionMx");
        int viewMxLoc = gl.glGetUniformLocation(shaderProgram, "viewMx");
        gl.glUniformMatrix4fv(projectionMxLox, 1, false, projectionMatrix.get(matrixVals));
        gl.glUniformMatrix4fv(viewMxLoc, 1, false, viewMatrix.get(matrixVals));

        // Get attribute locations
        int positionLoc = gl.glGetAttribLocation(shaderProgram, "vPosition");
        int texCoordLoc = gl.glGetAttribLocation(shaderProgram, "vTextureCoord");
        int normalLoc = gl.glGetAttribLocation(shaderProgram, "vNormal");
        int modelMxLoc = gl.glGetAttribLocation(shaderProgram, "modelMx");

        gl.glBindVertexArray(VAO[0]);

        // Render all meshes
        for (MeshData mesh : meshData) {
            List<Instance> instances = meshInstances.get(mesh.id);

            // Skip if there are no instances of this mesh
            if (instances == null || instances.size() == 0) {
                continue;
            }

            int meshVBOIndex = mesh.VBOi;

            // Bind mesh vertex data buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIndex]);
            // Set vertex position data to shader
            gl.glEnableVertexAttribArray(positionLoc);
            gl.glVertexAttribPointer(positionLoc, 3, GL_FLOAT, false, 0, 0);

            // Set vertex texture coordinate data to shader
            gl.glEnableVertexAttribArray(texCoordLoc);
            gl.glVertexAttribPointer(texCoordLoc, 2, GL_FLOAT, false, 0, mesh.numVertices * 3 * Float.BYTES);
            // Set vertex normal data to shader
            gl.glEnableVertexAttribArray(normalLoc);
            gl.glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 0, mesh.numVertices * 5 * Float.BYTES);

            // Get model matrices for each instance
            Matrix4f[] mMatrices = instances.parallelStream()
                    .map(instance -> instance.getTransform().getModelMatrix())
                    .toArray(Matrix4f[]::new);

            // Put the model matrices into a buffer
            FloatBuffer mMatBuffer = Buffers.newDirectFloatBuffer(mMatrices.length * 16);
            for (int j = 0; j < mMatrices.length; ++j) {
                mMatrices[j].get(j * 16, mMatBuffer);
            }

            // Bind model matrix buffer and send data to GPU
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIndex + 2]);
            gl.glBufferData(GL_ARRAY_BUFFER, mMatBuffer.limit() * Float.BYTES, mMatBuffer, GL_STATIC_DRAW);

            // A model matrix is 4x4, so we need to set up 4 attribute pointers for each
            // matrix
            for (int j = 0; j < 4; ++j) {
                gl.glVertexAttribPointer(modelMxLoc + j, 4, GL_FLOAT, false, 16 * 4, 16 * j);
                gl.glEnableVertexAttribArray(modelMxLoc + j);
                gl.glVertexAttribDivisor(modelMxLoc + j, 1);
            }

            // Bind mesh index buffer and draw
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIndex + 1]);
            gl.glDrawElementsInstanced(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0, mMatrices.length);
        }

        // Unbind VAO
        gl.glBindVertexArray(0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        clearVBOs(gl);
        gl.glDeleteVertexArrays(1, VAO, 0);
        gl.glDeleteProgram(shaderProgram);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl = drawable.getGL().getGL4();
        int viewportWidth = drawable.getSurfaceWidth();
        int viewportHeight = drawable.getSurfaceHeight();

        // Scale the viewport to match the actual window size, if the window has been
        // scaled. See issue #12.
        if (drawable instanceof Component) {
            Component comp = (Component) drawable;
            AffineTransform at = comp.getGraphicsConfiguration().getDefaultTransform();
            float sx = (float) at.getScaleX(), sy = (float) at.getScaleY();
            viewportWidth = (int) (width * sx);
            viewportHeight = (int) (height * sy);
        }
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
        updateProjectionMatrix((float) viewportWidth / viewportHeight);
    }

    public void updateProjectionMatrix(float aspectRatio) {
        projectionMatrix.setPerspective(
                (float) Math.toRadians(fov),
                aspectRatio,
                near,
                far);
    }

    private void loadMeshes(List<Mesh> meshes, GL4 gl) {
        clearVBOs(gl);
        gl.glBindVertexArray(VAO[0]);

        // Generate 3 VBOs for each mesh: vertex data, indices and instance model
        // matrices
        VBOs = new int[meshes.size() * 3];
        gl.glGenBuffers(VBOs.length, VBOs, 0);

        for (int i = 0; i < meshes.size(); ++i) {
            Mesh mesh = meshes.get(i);

            // Get mesh vertex data
            float[] vPositions = mesh.getPositions();
            float[] vTexCoords = mesh.getTextureCoords();
            float[] vNormals = mesh.getNormals();

            // Concatenate vertex data into a single array
            float[] meshVertexData = new float[vPositions.length + vTexCoords.length + vNormals.length];
            System.arraycopy(vPositions, 0, meshVertexData, 0, vPositions.length);
            System.arraycopy(vTexCoords, 0, meshVertexData, vPositions.length, vTexCoords.length);
            System.arraycopy(vNormals, 0, meshVertexData, vPositions.length + vTexCoords.length, vNormals.length);

            FloatBuffer vDataBuffer = Buffers.newDirectFloatBuffer(meshVertexData);
            IntBuffer indicesBuffer = Buffers.newDirectIntBuffer(mesh.getIndices());

            // Save mesh data
            int meshVBOIndex = i * 3;
            meshData.add(new MeshData(mesh.getId(), meshVBOIndex, mesh.getNumVertices(), mesh.getNumIndices()));

            // Send vertex position, texture coordinate and normal data to GPU.
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[meshVBOIndex]);
            gl.glBufferData(GL_ARRAY_BUFFER, vDataBuffer.limit() * Float.BYTES, vDataBuffer, GL_STATIC_DRAW);

            // Send vertex indices to GPU.
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VBOs[meshVBOIndex + 1]);
            gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.limit() * Integer.BYTES, indicesBuffer,
                    GL_STATIC_DRAW);
        }
    }

    /**
     * Deletes all VBOs.
     * 
     * @param gl The OpenGL context.
     */
    private void clearVBOs(GL4 gl) {
        if (VBOs != null) {
            gl.glDeleteBuffers(VBOs.length, VBOs, 0);
        }
    }

    public void setInstances(List<Instance> meshInstances) {
        this.meshInstances = meshInstances.parallelStream()
                .collect(Collectors.groupingBy(Instance::getMeshID));
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

}
