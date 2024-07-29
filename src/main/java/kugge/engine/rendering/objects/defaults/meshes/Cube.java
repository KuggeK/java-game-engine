package kugge.engine.rendering.objects.defaults.meshes;

import kugge.engine.rendering.objects.Material;
import kugge.engine.rendering.objects.defaults.Materials;
import kugge.engine.rendering.objects.Mesh;

public class Cube extends Mesh {

    /**
     * The positions of the vertices of the cube. 24 vertices, 3 floats each. Basically
     * each vertex is repeated 3 times, once for each face it is part of because each face
     * has its own normal.
     */
    private static final float[] POSITIONS = new float[] {
        // Back 
        -0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,

        // Right
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, -0.5f,

        // Front
        0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,

        // Left
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,

        // Top
        -0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,

        // Bottom
        0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f
    };

    private static final float[] TEXTURE_COORDINATES = new float[] {
        // Back 
        1, 0,
        0, 1,
        0, 0,
        1, 1,

        // Right
        1, 0,
        0, 1,
        0, 0,
        1, 1,

        // Front
        1, 0,
        0, 1,
        0, 0,
        1, 1,

        // Left
        1, 0,
        0, 1,
        0, 0,
        1, 1,

        // Top
        1, 0,
        0, 1,
        0, 0,
        1, 1,

        // Bottom
        1, 0,
        0, 1,
        0, 0,
        1, 1,
    };

    private static final float[] NORMALS = new float[] {
        // Back
        0, 0, -1,
        0, 0, -1,
        0, 0, -1,
        0, 0, -1,

        // Right
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,

        // Front
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,

        // Left
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,

        // Top
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,

        // Bottom
        0, -1, 0,
        0, -1, 0,
        0, -1, 0,
        0, -1, 0,
    };

    private static final float[] TANGENTS = new float[] {
        // Back
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,
        -1, 0, 0,

        // Right
        0, 0, -1,
        0, 0, -1,
        0, 0, -1,
        0, 0, -1,

        // Front
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,

        // Left
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,

        // Top
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,

        // Bottom
        1, 0, 0,
        1, 0, 0,
        1, 0, 0,
        1, 0, 0
    };

    private static final int[] INDICES = new int[] {
        // Back
        0, 1, 2, 1, 0, 3,

        // Right
        4, 5, 6, 5, 4, 7,

        // Front
        8, 9, 10, 9, 8, 11,

        // Left
        12, 13, 14, 13, 12, 15,

        // Top
        16, 17, 18, 17, 16, 19,

        // Bottom
        20, 21, 22, 21, 20, 23
    };

    public Cube(int id, Material material) {
        super(id, POSITIONS, TEXTURE_COORDINATES, NORMALS, INDICES, material);
        setTangents(TANGENTS);
    }

    public Cube(int id) {
        this(id, Materials.DEFAULT);
    }
}