package io.github.kuggek.engine.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;

public class JarPackager {
    

    /**
     * Package all Java .class files in the given directory and subdirectories into a jar file.
     * @param jarName The name of the jar file to create.
     * @param dirPath The directory to search for .class files in.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void packageDirectoryClasses(String jarName, String dirPath) throws FileNotFoundException, IOException {
        File directory = new File(dirPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory " + directory.getName());
            return;
        }

        System.out.println("Packaging classes in " + dirPath + " to " + jarName);

        File jarOutputFile = new File(jarName);
        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarOutputFile));
        jarOut.setLevel(Deflater.DEFAULT_COMPRESSION);

        writeManifest(jarOut);
        packageDirectoryTo(jarOut, "", directory);
        jarOut.close();
    }

    private static void packageDirectoryTo(JarOutputStream jarOut, String path, File directory) throws IOException {

        if (!directory.exists() || !directory.canRead()) {
            System.out.println("Invalid directory " + directory.getName());
            return;
        }

        File[] entries = directory.listFiles();
        path = buildPath(path, directory.getName());

        for (File entry : entries) {
            if (entry.getName().endsWith(".class")) {
                packageFileTo(jarOut, path, entry);
            } else if (entry.isDirectory()) {
                packageDirectoryTo(jarOut, path, entry);
            }
        }
    }

    public static void packageFileTo(JarOutputStream jarOut, String path, File file) throws IOException {
        if (!file.exists() || !file.canRead()) {
            System.out.println("Invalid file " + file.getName());
            return;
        }
        System.out.println("Adding " + file.getName() + " to " + path);

        FileInputStream in = new FileInputStream(file);

        jarOut.putNextEntry(new JarEntry(buildPath(path, file.getName())));

        // Transfer bytes from the file to the ZIP file
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            jarOut.write(buffer, 0, bytesRead);
        }
        in.close();
        jarOut.closeEntry();        
    }

    private static String buildPath(String path, String fileName) {
        if (path.isEmpty()) {
            return fileName;
        }
        return path + (path.endsWith("/") ? "" : "/") + fileName;
    }

    private static void writeManifest(JarOutputStream jarOut) throws IOException {
        jarOut.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
        jarOut.write("Manifest-Version: 1.0\n".getBytes());
        jarOut.closeEntry();
    }

}
