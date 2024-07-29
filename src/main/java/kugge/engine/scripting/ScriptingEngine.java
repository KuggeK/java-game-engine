package kugge.engine.scripting;

import java.util.HashSet;
import java.util.Set;
public class ScriptingEngine {

    private Set<Script> scripts;
    private Set<Script> newScripts;

    private KeyInput keyInput;

    public ScriptingEngine(KeyInput keyInput) {
        scripts = new HashSet<>();
        newScripts = new HashSet<>();

        this.keyInput = keyInput;
    }

    public void updateScripts(float dt) {
        // Start new scripts
        for (Script script : newScripts) {
            script.start();
        }
        newScripts.clear();

        // Update all scripts
        for (Script script : scripts) {
            script.update(keyInput, dt);
        }
    }

    public void addScript(Script script) {
        scripts.add(script);
        newScripts.add(script);
    }

    public void removeScript(Script script) {
        scripts.remove(script);
    }
}
