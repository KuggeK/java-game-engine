package kugge.rendering.core.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import kugge.rendering.core.config.ProjectPaths;

public class Meshes {

    public static final int CUBE_ID = 0;
    public static final int SPHERE_ID = 1;
    public static final int TORUS_ID = 2;

    public static final Map<Integer, Mesh> DEFAULT_MESHES = Map.of(
        CUBE_ID, new Cube(0),
        SPHERE_ID, Sphere.withId(1),
        TORUS_ID, Torus.generateTorus(2, 0.5f, 0.25f, 32, 32)
    );

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
}
