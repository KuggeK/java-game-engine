package kugge.rendering;

import kugge.rendering.core.KeyInput;

public interface Updateable {
    void update(KeyInput keyInput, float deltaTime);
}
