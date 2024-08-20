package io.github.kuggek.engine.scripting;

import java.util.HashSet;
import java.util.Set;

import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;
public class ScriptingEngine {

    private Set<Script> scripts;
    private Set<Script> newScripts;

    private KeyInput keyInput;

    private Set<Script> toRemove;
    private Set<Script> toAdd;

    public ScriptingEngine(KeyInput keyInput) {
        scripts = new HashSet<>();
        newScripts = new HashSet<>();

        this.keyInput = keyInput;
        
        toRemove = new HashSet<>();
        toAdd = new HashSet<>();
    }

    public void updateScripts(float dt, EngineRuntimeSettings settings) {
        // Start new scripts (excluding disabled ones)
        for (Script script : newScripts) {
            if (!script.isDisabled()) {
                script.start(settings);
            }
        }
        // Don't remove disabled scripts because they haven't been started yet
        newScripts.removeIf(script -> !script.isDisabled());

        // Update scripts (excluding disabled ones)
        for (Script script : scripts) {
            if (!script.isDisabled()) {
                script.update(keyInput, dt, settings);
            }
        }

        // Remove scripts
        for (Script script : toRemove) {
            scripts.remove(script);
        }
        toRemove.clear();

        // Add scripts
        for (Script script : toAdd) {
            scripts.add(script);
            newScripts.add(script);
        }
        toAdd.clear();
    }

    /**
     * Sets a script to be added to the engine after the next update cycle
     * @param script The script to add
     */
    public void setToBeAdded(Script script) {
        toAdd.add(script);
    }

    /**
     * Sets a script to be removed from the engine after the next update cycle
     * @param script The script to remove
     */
    public void setForRemoval(Script script) {
        toRemove.add(script);
    }

    public void clear() {
        scripts.clear();
        newScripts.clear();
        toRemove.clear();
        toAdd.clear();
    }
}
