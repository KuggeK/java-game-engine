package kugge.rendering.core.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

import kugge.rendering.graphics.WindowSettings;

public class EngineProjectConfiguration extends WindowSettings {

    private String projectName;
    private String projectDescription;
    private String projectVersion;
    private String projectAuthor;

    private List<Integer> sceneIDs;
    private int initialSceneID;

    private String scenesPath;
    private String meshesPath;
    private String texturesPath;
    private String shadersPath;
    private String scriptsPath;

    
    public EngineProjectConfiguration(int width, int height, String title, boolean fullscreen, boolean resizable, int targetFPS) {
        super(width, height, title, fullscreen, resizable, targetFPS);
    }

    public static EngineProjectConfiguration loadProjectConfiguration(String path) throws IOException, URISyntaxException {
        String json = Files.readString(Paths.get(path));
        Gson gson =new Gson();
        return gson.fromJson(json, EngineProjectConfiguration.class);
    }

    public static void populateGlobalPaths(EngineProjectConfiguration config) {
        ProjectPaths.setScenesPath(config.getScenesPath());
        ProjectPaths.setMeshesPath(config.getMeshesPath());
        ProjectPaths.setTexturesPath(config.getTexturesPath());
        ProjectPaths.setShadersPath(config.getShadersPath());
        ProjectPaths.setScriptsPath(config.getScriptsPath());
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

    public List<Integer> getSceneIDs() {
        return sceneIDs;
    }

    public void setSceneIDs(List<Integer> sceneIDs) {
        this.sceneIDs = sceneIDs;
    }

    public int getInitialSceneID() {
        return initialSceneID;
    }

    public void setInitialSceneID(int initialSceneID) {
        this.initialSceneID = initialSceneID;
    }

    public String getScenesPath() {
        return scenesPath;
    }

    public void setScenesPath(String scenesPath) {
        this.scenesPath = scenesPath;
    }

    public String getMeshesPath() {
        return meshesPath;
    }

    public void setMeshesPath(String meshesPath) {
        this.meshesPath = meshesPath;
    }

    public String getTexturesPath() {
        return texturesPath;
    }

    public void setTexturesPath(String texturesPath) {
        this.texturesPath = texturesPath;
    }

    public String getShadersPath() {
        return shadersPath;
    }

    public void setShadersPath(String shadersPath) {
        this.shadersPath = shadersPath;
    }

    public String getScriptsPath() {
        return scriptsPath;
    }

    public void setScriptsPath(String scriptsPath) {
        this.scriptsPath = scriptsPath;
    }
}
