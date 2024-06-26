package kugge.rendering.graphics;

import java.util.EventListener;

public interface Window {
    
    void setWindowSettings(WindowSettings settings);

    void registerEventListener(EventListener listener);

    void destroy();

    void toggleFullscreen();
}
