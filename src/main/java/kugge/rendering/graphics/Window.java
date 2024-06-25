package kugge.rendering.graphics;

import java.util.EventListener;

public interface Window {
    
    public void setWindowSettings(WindowSettings settings);

    public void registerEventListener(EventListener listener);

    public void destroy();
}
