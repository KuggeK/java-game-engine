package io.github.kuggek.engine.core.assets;

import java.util.Set;

import io.github.kuggek.engine.rendering.objects.Material;
import io.github.kuggek.engine.rendering.objects.Mesh;
import io.github.kuggek.engine.rendering.objects.defaults.Materials;
import io.github.kuggek.engine.rendering.objects.defaults.meshes.Cube;
import io.github.kuggek.engine.rendering.objects.defaults.meshes.Sphere;
import io.github.kuggek.engine.rendering.objects.defaults.meshes.Torus;

public class DefaultAssets {
    
    public static final Set<Mesh> MESHES = Set.of(
        new Cube(1),
        Sphere.withId(2),
        Torus.generateTorus(3, 0.5f, 0.25f, 32, 32)
    );

    public static void loadDefaultAssets(AssetManager assetManager) {
        for (Mesh mesh : MESHES) {
            try {
                assetManager.saveMeshChecked(mesh);
            } catch (Exception e) {
                System.out.println("Failed to save default mesh: " + mesh.getName());
            }
        }

        for (Material material : Materials.MATERIALS) {
            try {
                assetManager.saveMaterialChecked(material);
            } catch (Exception e) {
                System.out.println("Failed to save default material: " + material.getName());
            }
        }
    }
}
