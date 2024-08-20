package io.github.kuggek.engine.rendering;

import java.util.EventListener;

import io.github.kuggek.engine.scripting.KeyInput;

public interface Window {
    
    void setWindowSettings(WindowSettings settings);

    void registerEventListener(EventListener listener);

    void removeEventListener(EventListener listener);

    void destroy();

    void toggleFullscreen();

    KeyInput getKeyInput();

    void display();
}
