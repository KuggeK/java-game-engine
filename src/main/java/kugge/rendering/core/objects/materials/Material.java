package kugge.rendering.core.objects.materials;

import org.joml.Vector4f;

public record Material(
    Vector4f ambient,
    Vector4f diffuse,
    Vector4f specular,
    float shininess
) {}