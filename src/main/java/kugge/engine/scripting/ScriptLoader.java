package kugge.engine.scripting;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import kugge.engine.core.config.ProjectPaths;

public class ScriptLoader {
    
    public static Script loadScript(String scriptName) {
        try {
            Class<?> scriptClass = Class.forName(scriptName);
            return (Script) scriptClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compile user scripts into the game from the scripts directory.
     */
    public static boolean compileScript(String scriptName) {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        int result = javac.run(System.in, System.out, System.err,
            "-d", "target/classes/", 
            "-classpath", "target/rendering-engine-1.0.jar", 
            ProjectPaths.getScriptsPath() + scriptName + ".java");

        if (result != 0) {
            System.out.println("Compilation failed.");
            return false;
        }
        return true;
    }

    /**
     * Compile all scripts in the scripts directory.
     */
    public static void compileAllScripts() {
        // Run through all Java files in the scripts directory and compile them
        File scriptsDir = new File(ProjectPaths.getScriptsPath());
        compileAllScripts(scriptsDir);
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

        File[] entries = directory.listFiles();
        for (File entry : entries) {
            if (entry.getName().endsWith(".java")) {
                System.out.println("Java file: " + entry.getName());
                String scriptName = entry.getName().replace(".java", "");
                compileScript(scriptName);
            }

            if (entry.isDirectory()) {
                System.out.println("Directory: " + entry.getName());
                compileAllScripts(entry);
            } 
        }
    }
}
