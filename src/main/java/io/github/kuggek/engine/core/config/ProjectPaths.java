package io.github.kuggek.engine.core.config;

/**
 * Static class containing the paths to the different project folders.
 */
public class ProjectPaths {
    
    private static String scenesPath = "scenes/";
    private static String meshesPath = "meshes/";
    private static String texturesPath = "textures/";
    private static String shadersPath = "shaders/";
    private static String scriptsPath = "assets/scripts/";
    private static String classesPath = "classes/";

    public static String getScenesPath() {
        return scenesPath;
    }
    public static void setScenesPath(String scenesPath) {
        ProjectPaths.scenesPath = scenesPath;
    }
    public static String getMeshesPath() {
        return meshesPath;
    }
    public static void setMeshesPath(String meshesPath) {
        ProjectPaths.meshesPath = meshesPath;
    }
    public static String getTexturesPath() {
        return texturesPath;
    }
    public static void setTexturesPath(String texturesPath) {
        ProjectPaths.texturesPath = texturesPath;
    }
    public static String getShadersPath() {
        return shadersPath;
    }
    public static void setShadersPath(String shadersPath) {
        ProjectPaths.shadersPath = shadersPath;
    }
    public static String getScriptsPath() {
        return scriptsPath;
    }
    public static void setScriptsPath(String scriptsPath) {
        ProjectPaths.scriptsPath = scriptsPath;
    }

    private static String concatenatePath(String path, String fileName) {
        return path + (path.endsWith("/") ? "" : "/") + fileName;
    }

    public static String getScenePath(int sceneID) {
        return concatenatePath(scenesPath, "scene" + sceneID + ".json");
    }

    public static String getMeshPath(String fileName) {
        return concatenatePath(meshesPath, fileName);
    }

    public static String getTexturePath(String fileName) {
        return concatenatePath(texturesPath, fileName);
    }

    public static String getShaderPath(String fileName) {
        return concatenatePath(shadersPath, fileName);
    }

    public static String getScriptPath(String fileName) {
        return concatenatePath(scriptsPath, fileName);
    }
    public static String getClassesPath() {
        return classesPath;
    }
}
