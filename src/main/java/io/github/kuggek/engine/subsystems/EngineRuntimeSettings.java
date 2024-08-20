package io.github.kuggek.engine.subsystems;

import io.github.kuggek.engine.physics.PhysicsSettings;
import io.github.kuggek.engine.rendering.RenderingSettings;

public interface EngineRuntimeSettings {
    PhysicsSettings getPhysicsSettings();
    RenderingSettings getRenderingSettings();
    GameSceneSettings getGameSceneSettings();
}
