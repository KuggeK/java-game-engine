package io.github.kuggek.engine.core.config;

/**
 * Class containing the paths to the different project folders.
 */
public class ProjectPaths {
    private String scenesPath = "scenes/";
    private String scriptsPath = "assets/scripts/";
    private String scriptDependenciesPath = "assets/scripts/.dependencies/";

    public ProjectPaths(String absoluteProjectPath) {
        scenesPath = concatenatePath(absoluteProjectPath, scenesPath);
        scriptsPath = concatenatePath(absoluteProjectPath, scriptsPath);
        scriptDependenciesPath = concatenatePath(absoluteProjectPath, scriptDependenciesPath);
    }

    public ProjectPaths() {
        
    }

    public String getScenesPath() {
        return scenesPath;
    }
    
    public String getScenePath(int sceneID) {
        return concatenatePath(scenesPath, "scene" + sceneID + ".json");
    }

    public String getScriptsPath() {
        return scriptsPath;
    }

    public String getScriptDependenciesPath() {
        return scriptDependenciesPath;
    }

    public void setScenesPath(String scenesPath) {
        this.scenesPath = scenesPath;
    }

    public void setScriptsPath(String scriptsPath) {
        this.scriptsPath = scriptsPath;
    }

    public void setScriptDependenciesPath(String scriptDependenciesPath) {
        this.scriptDependenciesPath = scriptDependenciesPath;
    }
    
    private String concatenatePath(String path, String fileName) {
        return path + (path.endsWith("/") ? "" : "/") + fileName;
    }
}
