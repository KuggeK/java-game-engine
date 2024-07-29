package kugge.engine.rendering.objects.lights;

import org.joml.Vector4f;

public interface Light {
    Vector4f getAmbient();
    Vector4f getDiffuse();
    Vector4f getSpecular();
}
