package kugge.rendering.core.objects;

import kugge.rendering.core.objects.materials.Material;

public class Cube extends Mesh {

    public Cube(int id, float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        super(id, positions, textureCoords, normals, indices);
    }

    public Cube(int id, float[] positions, float[] textureCoords, float[] normals, int[] indices, Material material) {
        super(id, positions, textureCoords, normals, indices, material);
    }

    public static Cube withSize(int id, float size) {
        float[] positions = new float[] {
            -size, -size, -size,
            -size, size, -size,
            size, -size, -size,
            size, size, -size,
            -size, -size, size,
            -size, size, size,
            size, -size, size,
            size, size, size
        };
        float[] textureCoords = new float[] {
            0, 0,
            0, 1,
            1, 0,
            1, 1,
            0, 0,
            0, 1,
            1, 0,
            1, 1
        };

        float[] normals = positions;

        int[] indices = new int[] {
            0, 1, 2,
            1, 3, 2,
            4, 6, 5,
            5, 6, 7,
            0, 4, 1,
            1, 4, 5,
            2, 3, 6,
            3, 7, 6,
            0, 2, 4,
            2, 6, 4,
            1, 5, 3,
            3, 5, 7
        };
        return new Cube(id, positions, textureCoords, normals, indices);
    }
}