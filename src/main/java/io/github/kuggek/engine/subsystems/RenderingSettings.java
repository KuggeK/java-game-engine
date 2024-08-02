package io.github.kuggek.engine.subsystems;

import org.joml.Vector4f;

import io.github.kuggek.engine.rendering.objects.Camera;
import io.github.kuggek.engine.rendering.objects.SkyBox;
import io.github.kuggek.engine.rendering.objects.lights.DirectionalLight;

public interface RenderingSettings {
    
    void setActiveCamera(Camera camera);
    Camera getActiveCamera();

    void setSkyBox(SkyBox skyBox);

    void setGlobalAmbient(float r, float g, float b, float a);
    void setGlobalAmbient(Vector4f color);

    void setDirectionalLight(DirectionalLight light);
    DirectionalLight getDirectionalLight();
}
