package kugge.rendering.core.objects.materials;

import org.joml.Vector4f;

public record Material(
    int ID,
    Vector4f ambient,
    Vector4f diffuse,
    Vector4f specular,
    float shininess
) {
    public Material(int ID, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        this(
            ID,
            new Vector4f(ambient[0], ambient[1], ambient[2], ambient[3]),
            new Vector4f(diffuse[0], diffuse[1], diffuse[2], diffuse[3]),
            new Vector4f(specular[0], specular[1], specular[2], specular[3]),
            shininess
        );
    }
}