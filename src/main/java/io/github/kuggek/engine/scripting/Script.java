package io.github.kuggek.engine.scripting;

import io.github.kuggek.engine.ecs.GameComponent;
import io.github.kuggek.engine.subsystems.EngineRuntimeSettings;

public abstract class Script extends GameComponent {
    public abstract void start(EngineRuntimeSettings settings);

    public abstract void update(KeyInput keyInput, float deltaTime, EngineRuntimeSettings settings);
}
