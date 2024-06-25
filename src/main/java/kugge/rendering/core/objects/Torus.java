package kugge.rendering.core.objects;

public class Torus extends Mesh {

    private Torus(int id, float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        super(id, vertices, textureCoords, normals, indices);
    }

    public static Torus generateTorus(int id, float majorRadius, float minorRadius, int majorSegments, int minorSegments) {
        int numVertices = (majorSegments + 1) * (minorSegments + 1);
        int numIndices = majorSegments * minorSegments * 6;

        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = new float[numVertices * 2];
        float[] normals = new float[numVertices * 3];
        int[] indices = new int[numIndices];

        int vertexIndex = 0;
        int texCoordIndex = 0;
        int normalIndex = 0;
        int indexIndex = 0;

        for (int i = 0; i <= majorSegments; i++) {
            float theta = (float) (i * 2.0 * Math.PI / majorSegments);
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);

            for (int j = 0; j <= minorSegments; j++) {
                float phi = (float) (j * 2.0 * Math.PI / minorSegments);
                float cosPhi = (float) Math.cos(phi);
                float sinPhi = (float) Math.sin(phi);

                float x = (majorRadius + minorRadius * cosPhi) * cosTheta;
                float y = (majorRadius + minorRadius * cosPhi) * sinTheta;
                float z = minorRadius * sinPhi;

                float nx = cosPhi * cosTheta;
                float ny = cosPhi * sinTheta;
                float nz = sinPhi;

                vertices[vertexIndex++] = x;
                vertices[vertexIndex++] = y;
                vertices[vertexIndex++] = z;

                textureCoords[texCoordIndex++] = (float) i / majorSegments;
                textureCoords[texCoordIndex++] = (float) j / minorSegments;

                normals[normalIndex++] = nx;
                normals[normalIndex++] = ny;
                normals[normalIndex++] = nz;

                if (i < majorSegments && j < minorSegments) {
                    int first = (i * (minorSegments + 1)) + j;
                    int second = first + minorSegments + 1;

                    indices[indexIndex++] = first;
                    indices[indexIndex++] = second;
                    indices[indexIndex++] = first + 1;

                    indices[indexIndex++] = second;
                    indices[indexIndex++] = second + 1;
                    indices[indexIndex++] = first + 1;
                }
            }
        }
        return new Torus(id, vertices, textureCoords, normals, indices);
    }
}
