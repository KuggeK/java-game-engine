package kugge.rendering.core.objects;

import java.util.Map;
import java.util.function.Consumer;

public interface Subsystem {

    Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentDestroyListeners();
    Map<Class<? extends GameComponent>, Consumer<GameComponent>> getComponentInitListeners();
    void update(float dt);

}
