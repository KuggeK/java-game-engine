package io.github.kugge.engine.rendering;

import java.util.EventListener;

import io.github.kugge.engine.scripting.KeyInput;

public interface Window {
    
    void setWindowSettings(WindowSettings settings);

    void registerEventListener(EventListener listener);

    void destroy();

    void toggleFullscreen();

    KeyInput getKeyInput();
}
