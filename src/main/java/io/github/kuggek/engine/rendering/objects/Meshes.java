package io.github.kuggek.engine.rendering.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import io.github.kuggek.engine.core.config.ProjectPaths;

public class Meshes {

    /**
     * Loads a mesh from a file in the resources/models directory. The file must be in the .obj format.
     * The returned mesh only contains the vertex attributes and indices. The id of the mesh
     * is set to -1 and needs to be set to a unique value by the caller before use.
     * 
     * @param fileName The name of the file to load the mesh from
     * @return The mesh
     * @throws IOException If an IO error occurs either while opening or reading the file
     * @throws URISyntaxException If a URI error occurs, likely the file name is invalid
     */
    public static Mesh loadMesh(String fileName) throws IOException, URISyntaxException {
        FileInputStream is = new FileInputStream(new File(ProjectPaths.getMeshPath(fileName)));
        Obj obj = ObjReader.read(is);

        obj = ObjUtils.convertToRenderable(obj);

        float[] positions = ObjData.getVerticesArray(obj);
        float[] texCoords = ObjData.getTexCoordsArray(obj, 2);
        float[] normals = ObjData.getNormalsArray(obj);
        int[] indices = ObjData.getFaceVertexIndicesArray(obj);

        Mesh mesh = new Mesh(-1, positions, texCoords, normals, indices);
        mesh.setFileName(fileName);
        return mesh;
    }

    public static float[] calculateTangents(Mesh mesh) {
        int[] indices = mesh.getIndices();
        float[] positions = mesh.getPositions();
        float[] texCoords = mesh.getTextureCoords();

        float[] tangents = new float[positions.length];

        for (int i = 0; i < indices.length; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];

            float[] p1 = {positions[i0 * 3], positions[i0 * 3 + 1], positions[i0 * 3 + 2]};
            float[] p2 = {positions[i1 * 3], positions[i1 * 3 + 1], positions[i1 * 3 + 2]};
            float[] p3 = {positions[i2 * 3], positions[i2 * 3 + 1], positions[i2 * 3 + 2]};

            float[] uv1 = {texCoords[i0 * 2], texCoords[i0 * 2 + 1]};
            float[] uv2 = {texCoords[i1 * 2], texCoords[i1 * 2 + 1]};
            float[] uv3 = {texCoords[i2 * 2], texCoords[i2 * 2 + 1]};

            // E1 = p2 - p1
            float[] E1 = {p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
            // E2 = p3 - p1
            float[] E2 = {p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2]};

            // deltaUV1 = uv2 - uv1
            float[] deltaUV1 = {uv2[0] - uv1[0], uv2[1] - uv1[1]};
            // deltaUV2 = uv3 - uv1
            float[] deltaUV2 = {uv3[0] - uv1[0], uv3[1] - uv1[1]};

            float f = 1.0f / (deltaUV1[0] * deltaUV2[1] - deltaUV2[0] * deltaUV1[1]);

            float[] tangent = {
                f * (deltaUV2[1] * E1[0] - deltaUV1[1] * E2[0]),
                f * (deltaUV2[1] * E1[1] - deltaUV1[1] * E2[1]),
                f * (deltaUV2[1] * E1[2] - deltaUV1[1] * E2[2])
            };

            for (int j = 0; j < 3; j++) {
                tangents[i0 * 3 + j] += tangent[j];
                tangents[i1 * 3 + j] += tangent[j];
                tangents[i2 * 3 + j] += tangent[j];
            }
        }

        // Normalize tangents
        for (int i = 0; i < tangents.length; i += 3) {
            float[] t = {tangents[i], tangents[i + 1], tangents[i + 2]};
            float length = (float) Math.sqrt(t[0] * t[0] + t[1] * t[1] + t[2] * t[2]);
            t[0] /= length;
            t[1] /= length;
            t[2] /= length;
        }

        return tangents;
    }
}
