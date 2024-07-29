package kugge.engine.rendering;

import java.util.EventListener;

import kugge.engine.scripting.KeyInput;

public interface Window {
    
    void setWindowSettings(WindowSettings settings);

    void registerEventListener(EventListener listener);

    void destroy();

    void toggleFullscreen();

    KeyInput getKeyInput();
}
