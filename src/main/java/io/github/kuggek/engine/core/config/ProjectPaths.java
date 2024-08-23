package io.github.kuggek.engine.core.config;

/**
 * Class containing the paths to the different project folders. Paths are already formatted to 
 * the system format and can be used directly. See {@link ProjectPaths} for more information.
 */
public class ProjectPaths {
    public final static String PROJECT_CONFIG_PATH;
    public final static String SCENES_PATH;
    public final static String SCRIPTS_PATH;
    public final static String SCRIPT_DEPENDENCIES_PATH;
    public final static String DB_PATH;

    public final static String PATH_SEPARATOR = System.getProperty("path.separator");
    public final static String FILE_SEPARATOR = System.getProperty("file.separator");

    static {
        PROJECT_CONFIG_PATH = "project.json";
        SCENES_PATH = "scenes";
        SCRIPTS_PATH = "scripts";
        SCRIPT_DEPENDENCIES_PATH = concatenatePaths("scripts", "dependencies");
        DB_PATH = concatenatePaths(".assets", "assets.db");
    }

        /**
     * Formats a path to the system's path format. 
     * @param path The path to format
     * @return The formatted path
     */
    public static String formatToSystemPath(String path) {
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }

    /**
     * Concatenates paths, adding the system separator between them. 
     * Uses the system's file separator, and removes hanging separators from the beginning/end of 
     * the provided paths, but does not change other separators already present in the middle of paths.
     * For full formatting, use {@link #concatenateAndFormat(String...)}. Does not add a separator at the end.
     * @param paths The paths to concatenate
     * @return The concatenated path
     */
    public static String concatenatePaths(String... paths) {
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            if (path.startsWith("/") || path.startsWith("\\")) {
                path = path.substring(1);
            }
            builder.append(path);
            if (path.endsWith("/") || path.endsWith("\\")) {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(FILE_SEPARATOR);
        }
        // Remove the last separator
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * Concatenates and formats paths to the system's path format. See {@link #concatenatePaths(String...)} 
     * and {@link #formatToSystemPath(String)} for more information. 
     * @param paths The paths to concatenate and format
     * @return The concatenated and formatted path
     */
    public static String concatenateAndFormat(String... paths) {
        return formatToSystemPath(concatenatePaths(paths));
    }
}
