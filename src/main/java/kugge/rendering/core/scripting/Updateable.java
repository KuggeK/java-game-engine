package kugge.rendering.core.scripting;

import kugge.rendering.core.KeyInput;

public interface Updateable {
    void update(KeyInput keyInput, float deltaTime);
}
