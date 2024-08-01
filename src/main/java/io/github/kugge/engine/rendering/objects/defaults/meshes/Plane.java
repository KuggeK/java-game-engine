package io.github.kugge.engine.rendering.objects.defaults.meshes;

import io.github.kugge.engine.rendering.objects.Mesh;

/**
 * Represents a one sided plane mesh consisting of two triangles.
 */
public class Plane extends Mesh {
    private final static float[] POSITIONS = new float[] {
        -0.5f, 0, -0.5f,
        -0.5f, 0, 0.5f,
        0.5f, 0, -0.5f,
        0.5f, 0, 0.5f
    };

    private final static float[] TEXTURE_COORDINATES = new float[] {
        0, 0,
        0, 1,
        1, 0,
        1, 1
    };

    private final static float[] NORMALS = new float[] {
        0, 1, 0,
        0, 1, 0,
        0, 1, 0,
        0, 1, 0
    };

    private final static int[] INDICES = new int[] {
        0, 1, 2,
        2, 1, 3
    };

    public Plane(int id) {
        super(id, POSITIONS, TEXTURE_COORDINATES, NORMALS, INDICES);
    }
}
