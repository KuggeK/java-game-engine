package kugge.rendering.core.objects.meshes;

import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.materials.Materials;

public class Cube extends Mesh {

    private static final float[] POSITIONS = new float[] {
        -0.5f, -0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f
    };

    private static final float[] TEXTURE_COORDINATES = new float[] {
        0, 0,
        0, 1,
        1, 0,
        1, 1,
        0, 0,
        0, 1,
        1, 0,
        1, 1
    };

    private static final float[] NORMALS = new float[] {
        -0.5f, -0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f
    };

    private static final int[] INDICES = new int[] {
        0, 1, 2, 2, 1, 3,
        2, 3, 6, 6, 3, 7,
        6, 7, 4, 4, 7, 5,
        4, 5, 0, 0, 5, 1,
        1, 5, 3, 3, 5, 7,
        4, 0, 6, 6, 0, 2
    };

    public Cube(int id, Material material) {
        super(id, POSITIONS, TEXTURE_COORDINATES, NORMALS, INDICES, material);
    }

    public Cube(int id) {
        this(id, Materials.DEFAULT);
    }
}