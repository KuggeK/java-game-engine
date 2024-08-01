package io.github.kuggek.engine.rendering.objects.defaults.meshes;

import static java.lang.Math.*;

import io.github.kuggek.engine.rendering.objects.Mesh;

public class Sphere extends Mesh {
    
    private Sphere(int id, float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        super(id, positions, textureCoords, normals, indices);
    }

    public static Sphere withId(int id) {
        
        int slices = 20;
        int stacks = 20;
        float radius = 1;
        
        float[] positions = new float[(slices + 1) * (stacks + 1) * 3];
        float[] textureCoords = new float[(slices + 1) * (stacks + 1) * 2];
        int[] indices = new int[slices * stacks * 6];
        
        int vertexPointer = 0;
        int texturePointer = 0;
        for (int i = 0; i <= stacks; i++) {
            float stackAngle = (float) (PI / 2 - PI * i / stacks);
            float xy = (float) cos(stackAngle);
            float z = (float) sin(stackAngle);
            
            for (int j = 0; j <= slices; j++) {
                float sectorAngle = (float) (2 * PI * j / slices);
                float x = xy * (float) cos(sectorAngle);
                float y = xy * (float) sin(sectorAngle);
                
                positions[vertexPointer * 3] = x * radius;
                positions[vertexPointer * 3 + 1] = y * radius;
                positions[vertexPointer * 3 + 2] = z * radius;
                
                textureCoords[texturePointer * 2] = (float) j / slices;
                textureCoords[texturePointer * 2 + 1] = (float) i / stacks;
                
                vertexPointer++;
                texturePointer++;
            }
        }
        
        int indexPointer = 0;
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int first = i * (slices + 1) + j;
                int second = first + slices + 1;
                
                indices[indexPointer++] = first;
                indices[indexPointer++] = second;
                indices[indexPointer++] = first + 1;
                
                indices[indexPointer++] = second;
                indices[indexPointer++] = second + 1;
                indices[indexPointer++] = first + 1;
            }
        }
        
        return new Sphere(id, positions, textureCoords, positions, indices);
    }

    // Vertices are the same as normals in a sphere. This means we don't need to keep track of normal separately
    @Override
    public float[] getNormals() {
        return getPositions();
    }
}
