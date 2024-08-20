package io.github.kuggek.engine.core;

public class PathUtils {
    /**
     * Formats a path to the system's path format. 
     * @param path The path to format
     * @return The formatted path
     */
    public static String formatToSystemPath(String path) {
        String separator = System.getProperty("file.separator");
        return path.replace("/", separator).replace("\\", separator);
    }

    /**
     * Concatenates paths and adds a separator between them if necessary.
     * @param paths
     * @return
     */
    public static String concatenatePaths(String... paths) {
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            builder.append(path);
            if (!path.endsWith("/") && !path.endsWith("\\")) {
                builder.append("/");
            }
        }
        return builder.toString();
    }

    public static String concatenateAndFormat(String... paths) {
        return formatToSystemPath(concatenatePaths(paths));
    }
}
