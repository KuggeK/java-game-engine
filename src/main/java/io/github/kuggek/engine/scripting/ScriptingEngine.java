package io.github.kuggek.engine.scripting;

import java.util.HashSet;
import java.util.Set;
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
}
