package kugge.rendering.core;

import kugge.rendering.core.objects.RenderScene;

public interface SceneStorage {

    RenderScene loadScene(int sceneID) throws Exception;

    void saveScene(RenderScene scene) throws Exception;
}
