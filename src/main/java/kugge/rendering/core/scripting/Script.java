package kugge.rendering.core.scripting;

import kugge.rendering.core.KeyInput;
import kugge.rendering.core.objects.GameComponent;

public abstract class Script extends GameComponent implements Updateable {
    public abstract void start();

    public abstract void update(KeyInput keyInput, float deltaTime);
}
