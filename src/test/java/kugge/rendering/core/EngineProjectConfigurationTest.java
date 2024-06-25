package kugge.rendering.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import kugge.rendering.core.config.EngineProjectConfiguration;

public class EngineProjectConfigurationTest {


    private final String testJSONPath = "src/test/resources/testProjectConfig.json";

    @Test
    public void testLoadEngineProjectConfiguration() {
        File testFile = new File(testJSONPath);
        EngineProjectConfiguration config = null;
        try {
            config = EngineProjectConfiguration.loadProjectConfiguration(testFile.getAbsolutePath());
        } catch (IOException | URISyntaxException e) {
            fail("Failed to load project configuration file");
        }
        
        assertEquals(0, config.getInitialSceneID());
        assertEquals(1, config.getSceneIDs().size());
        assertEquals(0, config.getSceneIDs().get(0));
        assertEquals("Test Project", config.getProjectName());
        assertEquals("Test Description", config.getProjectDescription());
        assertEquals("1.0", config.getProjectVersion());
        assertEquals("Test Author", config.getProjectAuthor());
        assertEquals(60, config.getTargetFPS());
        assertEquals(800, config.getWidth());
        assertEquals(600, config.getHeight());
        assertEquals("Test Title", config.getTitle());
        assertFalse(config.isFullscreen());
        assertTrue(config.isResizable());
        assertEquals("scenes", config.getScenesPath());
        assertEquals("meshes", config.getMeshesPath());
        assertEquals("textures", config.getTexturesPath());
        assertEquals("shaders", config.getShadersPath());
        assertEquals("scripts", config.getScriptsPath());
    }

}
