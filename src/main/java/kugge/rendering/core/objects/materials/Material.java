package kugge.rendering.core.objects.materials;

public record Material(
    float[] ambient,
    float[] diffuse,
    float[] specular,
    float shininess
) {}