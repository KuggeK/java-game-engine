package kugge.engine.scripting;

import kugge.engine.ecs.GameComponent;

public abstract class Script extends GameComponent {
    public abstract void start();

    public abstract void update(KeyInput keyInput, float deltaTime);
}
