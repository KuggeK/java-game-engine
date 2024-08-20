package io.github.kuggek.engine.core.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;

/**
 * A class that manages the project's internal resources.
 */
public class ResourceManager {
    
    /**
     * Checks if the program is running from a JAR file
     * @return True if the program is running from a JAR file, false otherwise
     */
    public static boolean inJar() {
        return ResourceManager.class.getResource("ResourceManager.class").toString().startsWith("jar:");
    }

    public static String readFile(String path) throws IOException {

        if (inJar()) {
            return readJarFile(path);
        } else {
            return readRegularFile(path);
        }
    }

    /**
     * Lists all files in a directory 
     * @param path The path to the directory
     * @return An array of file names
     * @throws IOException 
     */
    public static String[] listFilesIn(String path) throws IOException {
        if (inJar()) {
            return listJarFilesIn(path);
        } else {
            return listRegularFilesIn(path);
        }
    }

    private static String readRegularFile(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(ResourceManager.class.getResource(path).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IOException("Failed to read file: " + path, e);
        }
        return String.join("\n", lines);
    }

    private static String readJarFile(String path) {
        StringBuilder content = new StringBuilder();

        try {
            JarFile jarFile = new JarFile(new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            jarFile.stream().parallel().filter(entry -> entry.getName().equals(path)).forEach(entry -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            jarFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }   

        return content.toString();
    }

    private static String[] listRegularFilesIn(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        File folder;
        try {
            folder = new File(ResourceManager.class.getResource(path).toURI());
        } catch (URISyntaxException e) {
            throw new IOException("Failed to list files in directory: " + path, e);
        }

        if (!folder.exists()) {
            throw new IOException("Directory does not exist: " + path);
        }
        return folder.list();
    }

    private static String[] listJarFilesIn(String path) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Filter out the entries that are not in the specified path and the path itself
        return jarFile.stream().parallel().map(entry -> entry.getName()).filter(name -> name.startsWith(path) && !name.equals(path)).toArray(String[]::new);
    }

}
