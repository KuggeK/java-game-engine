package io.github.kuggek.engine.core.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

import io.github.kuggek.engine.rendering.WindowSettings;

public class EngineProjectConfiguration extends WindowSettings {

    private transient String projectAbsolutePath;
    private String projectName;
    private String projectDescription;
    private String projectVersion;
    private String projectAuthor;

    private List<String> scenes;
    private String initialSceneName;

    private static EngineProjectConfiguration instance;
    
    public EngineProjectConfiguration(int width, int height, String title, boolean fullscreen, boolean resizable, int targetFPS) {
        super(width, height, title, fullscreen, resizable, targetFPS);
    }

    public static EngineProjectConfiguration loadProjectConfiguration(String path) throws IOException, URISyntaxException {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory.");
        }

        String projectFilePath = ProjectPaths.concatenateAndFormat(path, "project.json");
        String json = Files.readString(Paths.get(projectFilePath));
        Gson gson = new Gson();
        instance = gson.fromJson(json, EngineProjectConfiguration.class);
        instance.projectAbsolutePath = file.getAbsolutePath();
        return instance;
    }

    public static void set(EngineProjectConfiguration config) {
        instance = config;
    }

    public static EngineProjectConfiguration get() {
        return instance;
    }

    public String getProjectAbsolutePath() {
        return projectAbsolutePath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getProjectAuthor() {
        return projectAuthor;
    }

    public void setProjectAuthor(String projectAuthor) {
        this.projectAuthor = projectAuthor;
    }

    public List<String> getScenes() {
        return scenes;
    }

    public void setSceneIDs(List<String> scenes) {
        this.scenes = scenes;
    }

    public String getInitialSceneName() {
        return initialSceneName;
    }

    public void setInitialSceneName(String initialSceneName) {
        this.initialSceneName = initialSceneName;
    }
}
