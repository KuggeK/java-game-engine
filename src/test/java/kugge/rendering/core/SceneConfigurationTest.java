package kugge.rendering.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import kugge.rendering.core.config.ProjectPaths;
import kugge.rendering.core.json.SceneStorageJSON;
import kugge.rendering.core.objects.Camera;
import kugge.rendering.core.objects.RenderScene;
import kugge.rendering.core.objects.Transform;
import kugge.rendering.core.objects.lights.DirectionalLight;
import kugge.rendering.core.objects.lights.PositionalLight;
import kugge.rendering.core.objects.materials.Material;
import kugge.rendering.core.objects.meshes.Mesh;

public class SceneConfigurationTest {
    
    @Test
    public void testLoadSceneConfiguration() {

        RenderScene scene = null;
        ProjectPaths.setScenesPath("src/test/resources/scenes");
        try {
            SceneStorageJSON storage = new SceneStorageJSON(true);
            scene = storage.loadScene(1);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertEquals(1, scene.getID());

        // -------------------- MESHES --------------------
        assertEquals(1, scene.getMeshes().size());
        Mesh mesh = scene.getMeshes().get(0);
        assertEquals(1, mesh.getID());
        assertEquals("Test Mesh", mesh.getFileName());
        assertArrayEquals(new Integer[] { 1, 2 }, mesh.getTextureIDs().toArray());

        // Texture parameters
        var texParams = mesh.getTextureParameters();
        assertEquals(2, texParams.size());
        assertTrue(texParams.containsKey(10242)); // GL_TEXTURE_WRAP_S
        assertTrue(texParams.containsKey(10243)); // GL_TEXTURE_WRAP_T
        assertEquals(10497, texParams.get(10242)); // GL_REPEAT
        assertEquals(33071, texParams.get(10243)); // GL_CLAMP_TO_EDGE

        // -------------------- TEXTURES --------------------
        assertEquals(2, scene.getTextures().size());
        assertEquals(1, scene.getTextures().get(0).getID());
        assertEquals(2, scene.getTextures().get(1).getID());
        
        // -------------------- MATERIALS --------------------
        assertEquals(2, scene.getMaterials().size());
    
        Material expectedMat1 = new Material(
            1,
            new Vector4f(0.11f, 0.12f, 0.13f, 0.14f),
            new Vector4f(0.21f, 0.22f, 0.23f, 0.24f),
            new Vector4f(0.31f, 0.32f, 0.33f, 0.34f),
            0.1f
        );
        assertMaterialsEqual(expectedMat1, scene.getMaterials().get(0));

        Material expectedMat2 = new Material(
            2,
            new Vector4f(0.41f, 0.42f, 0.43f, 0.44f),
            new Vector4f(0.51f, 0.52f, 0.53f, 0.54f),
            new Vector4f(0.61f, 0.62f, 0.63f, 0.64f),
            0.2f
        );
        assertMaterialsEqual(expectedMat2, scene.getMaterials().get(1));

        // -------------------- LIGHTS --------------------
        // Positional lights
        assertEquals(1, scene.getPositionalLights().size());
        PositionalLight posLight = scene.getPositionalLights().get(0);
        assertTrue(posLight.getAmbient().equals(0.1f, 0.2f, 0.3f, 0.4f));
        assertTrue(posLight.getDiffuse().equals(0.5f, 0.6f, 0.7f, 0.8f));
        assertTrue(posLight.getSpecular().equals(0.9f, 1.0f, 0.9f, 0.8f));
        assertTrue(posLight.getPosition().equals(1.0f, 2.0f, 3.0f));

        assertEquals(1.0f, posLight.getConstant());
        assertEquals(0.0f, posLight.getLinear());
        assertEquals(0.0f, posLight.getQuadratic());
        assertEquals(10, posLight.getRadius());

        // Directional light
        DirectionalLight dirLight = scene.getDirectionalLight();
        assertTrue(dirLight.getAmbient().equals(0.1f, 0.2f, 0.3f, 0.0f));
        assertTrue(dirLight.getDiffuse().equals(0.4f, 0.5f, 0.6f, 0.5f));
        assertTrue(dirLight.getSpecular().equals(0.7f, 0.8f, 0.9f, 1.0f));
        assertTrue(dirLight.getDirection().equals(1.0f, 2.0f, 3.0f));

        // Global ambient
        assertTrue(scene.getGlobalAmbient().equals(0.1f, 0.2f, 0.3f, 0.4f));

        // -------------------- CAMERA --------------------
        Camera camera = scene.getCamera();
        assertEquals(60f, camera.getFov());
        assertEquals(0.1f, camera.getNear());
        assertEquals(1000f, camera.getFar());
        
        // Transform
        assertTransformEquals(
            new Vector3f(1f, 2f, -10.0f),
            new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 1.0f),
            camera.getTransform()
        );

        // -------------------- MESH INSTANCES --------------------
        assertEquals(1, scene.getInstances().size());
        var instance = scene.getInstances().get(0);
        assertEquals(1, instance.getMeshID());
        assertEquals(1, instance.getMaterialID());
        assertEquals(0, instance.getTextureIndex());
        assertTransformEquals(
            new Vector3f(3, 2, 1),
            new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 1.0f),
            instance.getTransform()
        );
    }

    private void assertTransformEquals(Vector3f expectedPos, Quaternionf expectedRot, Vector3f expectedScale, Transform transform) {
        assertTrue(transform.getPosition().equals(expectedPos));
        assertTrue(transform.getRotation().equals(expectedRot));
        assertTrue(transform.getScale().equals(expectedScale));
    }

    private void assertMaterialsEqual(Material mat1, Material mat2) {
        assertEquals(mat1.ID(), mat2.ID());
        assertTrue(mat1.ambient().equals(mat2.ambient()));
        assertTrue(mat1.diffuse().equals(mat2.diffuse()));
        assertTrue(mat1.specular().equals(mat2.specular()));
        assertEquals(mat1.shininess(), mat2.shininess());
    }
}
