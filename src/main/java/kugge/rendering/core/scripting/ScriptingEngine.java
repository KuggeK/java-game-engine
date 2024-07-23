package kugge.rendering.core.scripting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.objects.GameComponent;
import kugge.rendering.core.objects.Subsystem;

public class ScriptingEngine implements Subsystem {

    private Set<Script> scripts;
    private Set<Script> newScripts;

    private final Consumer<GameComponent> removeScriptListener;
    private final Consumer<GameComponent> addScriptListener;

    private KeyInput keyInput;

    
    public ScriptingEngine(KeyInput keyInput) {
        scripts = new HashSet<>();
        newScripts = new HashSet<>();

        removeScriptListener = script -> removeScript(script);
        addScriptListener = script -> addScript(script);

        this.keyInput = keyInput;
    }

    @Override
    public void update(float dt) {
        // Start new scripts
        for (Script script : newScripts) {
            script.start();
        }
        newScripts.clear();

        // Update all scripts
        for (Script script : scripts) {
            script.update(keyInput, dt);
        }

        keyInput.clear();
    }

    private void addScript(GameComponent component) {
        if (component instanceof Script) {
            scripts.add((Script) component);
            newScripts.add((Script) component);
        }
    }

    private void removeScript(GameComponent component) {
        if (component instanceof Script) {
            scripts.remove((Script) component);
        }
    }

    @Override
    public Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentDestroyListeners() {
        return Map.of(Script.class, removeScriptListener);
    }

    @Override
    public Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentInitListeners() {
        return Map.of(Script.class, addScriptListener);
    }
    
}
