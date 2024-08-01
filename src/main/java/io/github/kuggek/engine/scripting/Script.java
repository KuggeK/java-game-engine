package io.github.kuggek.engine.scripting;

import io.github.kuggek.engine.ecs.GameComponent;

public abstract class Script extends GameComponent {
    public abstract void start();

    public abstract void update(KeyInput keyInput, float deltaTime);
}
