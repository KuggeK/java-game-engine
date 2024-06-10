package kugge.rendering.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyInput {

    private Map<Short, Boolean> keysPressed;
    private Map<Short, Boolean> keysHeld;
    
    public KeyInput() {
        keysPressed = new HashMap<>();
        keysHeld = new HashMap<>();
    }

    public void keyPressed(KeyEvent e) {
        keysPressed.put((short) e.getKeyCode(), true);
        keysHeld.put((short) e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        keysHeld.put((short) e.getKeyCode(), false);
    }

    public boolean isKeyPressed(short keyCode) {
        return keysPressed.getOrDefault(keyCode, false);
    }

    public boolean isKeyHeld(short keyCode) {
        return keysHeld.getOrDefault(keyCode, false);
    }

    public void clear() {
        keysPressed.clear();
    }

    public void clearHeld() {
        keysHeld.clear();
    }

    public class DefaultKeyRegisterer implements KeyListener {

        public DefaultKeyRegisterer() {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            KeyInput.this.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            KeyInput.this.keyReleased(e);
        }
    }
}
