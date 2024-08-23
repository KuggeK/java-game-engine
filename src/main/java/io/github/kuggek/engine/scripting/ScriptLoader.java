package io.github.kuggek.engine.scripting;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import io.github.kuggek.engine.core.config.ProjectPaths;

public class ScriptLoader {

    private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private static Instrumentation instrumentation;
    private static JarClassLoader jarClassLoader = new JarClassLoader(new URLClassLoader(new URL[0], ScriptLoader.class.getClassLoader()));
    private static Set<String> loadedScriptsNames = new HashSet<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
        System.out.println("Agent loaded.");
    }

    public static Script loadScript(String scriptName) {
        try {
            System.out.println(scriptName);
            Class<?> scriptClass = null;
            // If we are running this as a java agent, we can load the class directly with the default class loader.
            // Otherwise, we need to use our custom class loader.
            if (instrumentation == null) {
                scriptClass = jarClassLoader.loadClass(scriptName);
            } else  {
                scriptClass = Class.forName(scriptName);
            }
            return (Script) scriptClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compile user script with the given name.
     */
    public static boolean compileScript(String scriptFileName, String directoryPath, String... dependencies) {
        String pathSep = ProjectPaths.PATH_SEPARATOR;
        String fileSep = ProjectPaths.FILE_SEPARATOR;
        directoryPath = directoryPath.replace("/", fileSep) + fileSep;

        StringBuilder classpath = new StringBuilder();
        for (String dependency : dependencies) {
            classpath.append(dependency).append(pathSep);
        }
        classpath.deleteCharAt(classpath.length() - 1);
        
        int result = javac.run(System.in, System.out, System.err,
            "-classpath", classpath.toString(), 
            scriptFileName);

        if (result != 0) {
            System.out.println("Compilation failed.");
            return false;
        }

        loadedScriptsNames.add(determineClassName(scriptFileName));
        return true;
    }

    // The root package of all scripts should be scripts. 
    private static String determineClassName(String scriptFileName) {
        scriptFileName = ProjectPaths.formatToSystemPath(scriptFileName);
        scriptFileName = scriptFileName.split("scripts")[1];
        if (System.getProperty("file.separator").equals("\\")) {
            scriptFileName = scriptFileName.replace("\\", ".");
        } else {
            scriptFileName = scriptFileName.replace("/", ".");
        }
        scriptFileName = scriptFileName.endsWith(".java") ? scriptFileName.substring(0, scriptFileName.length() - 5) : scriptFileName;
        return "scripts" + (!scriptFileName.startsWith(".") ? "." : "") + scriptFileName;
    }

    /**
     * Recursively compile all scripts in the given directory.
     * @param directory The directory to search for scripts in.
     */
    public static void compileAllScripts(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory.");
            return;
        }

        String[] dependencies = new String[] {
            ProjectPaths.concatenateAndFormat(ProjectPaths.SCRIPT_DEPENDENCIES_PATH, "engine-1.0.jar"),
            ProjectPaths.concatenateAndFormat(ProjectPaths.SCRIPT_DEPENDENCIES_PATH, "joml-1.10.5.jar") 
        };

        File[] entries = directory.listFiles();
        for (File entry : entries) {
            if (entry.getName().endsWith(".java")) {
                System.out.println("Java file: " + entry.getName());
                compileScript(entry.getAbsolutePath(), directory.getAbsolutePath(), dependencies);
            }

            if (entry.isDirectory()) {
                System.out.println("Directory: " + entry.getName());
                compileAllScripts(entry);
            } 
        }
    }

    public static void compileAllScripts(String directory) {
        compileAllScripts(new File(directory));
    }

    public static void compileAndPackageScripts(String jarName, String dirPath) {
        try {
            compileAllScripts(dirPath);
            JarPackager.packageDirectoryClasses(jarName, dirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addJarToClasspath(String jarName) {
        try {
            if (instrumentation == null) {
                jarClassLoader.addURL(new File(jarName).toURI().toURL());
                System.out.println("Added jar to classpath.");
            } else {
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(jarName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getLoadedScriptsNames() {
        return Set.copyOf(loadedScriptsNames);
    }

    public static JarClassLoader getJarClassLoader() {
        return jarClassLoader;
    }
}
