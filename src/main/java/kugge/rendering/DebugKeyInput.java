package kugge.rendering;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import kugge.rendering.core.objects.Camera;
import kugge.rendering.graphics.Renderer;

public class DebugKeyInput implements KeyListener {

    private Camera camera;
    private Renderer renderer;

    public DebugKeyInput(Camera camera, Renderer renderer) {
        this.camera = camera;
        this.renderer = renderer;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                camera.getTransform().translate(camera.getTransform().getForward().mul(0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_S:
                camera.getTransform().translate(camera.getTransform().getForward().mul(-0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_A:
                camera.getTransform().translate(camera.getTransform().getRight().mul(-0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_D:
                camera.getTransform().translate(camera.getTransform().getRight().mul(0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_SPACE:
                camera.getTransform().translate(camera.getTransform().getUp().mul(0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_SHIFT:
                camera.getTransform().translate(camera.getTransform().getUp().mul(-0.1f));
                renderer.render();
                break;
            case KeyEvent.VK_LEFT:
                camera.getTransform().rotate(0, 0.1f, 0);
                renderer.render();
                break;
            case KeyEvent.VK_RIGHT:
                camera.getTransform().rotate(0, -0.1f, 0);
                renderer.render();
                break;
            case KeyEvent.VK_UP:
                camera.getTransform().rotate(0.1f, 0, 0);
                renderer.render();
                break;
            case KeyEvent.VK_DOWN:
                camera.getTransform().rotate(-0.1f, 0, 0);
                renderer.render();
                break;
            case KeyEvent.VK_Q:
                camera.getTransform().rotate(0, 0, 0.1f);
                renderer.render();
                break;
            case KeyEvent.VK_E:
                camera.getTransform().rotate(0, 0, -0.1f);
                renderer.render();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    

}
