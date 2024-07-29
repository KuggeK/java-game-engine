package kugge.engine.rendering.objects.defaults;

import java.util.List;

import org.joml.Vector4f;

import kugge.engine.rendering.objects.Material;

public class Materials {

    public static final Material DEFAULT = new Material(
        0,
        new Vector4f(0.8f, 0.8f, 0.8f, 1.0f),
        new Vector4f(0.1f, 0.1f, 0.1f, 1.0f),
        new Vector4f(0.2f, 0.2f, 0.2f, 1.0f),
        0.1f
    );

    public static final Material GOLD = new Material(
        1,
        new Vector4f(0.24725f, 0.1995f, 0.0745f, 1.0f),
        new Vector4f(0.75164f, 0.60648f, 0.22648f, 1.0f),
        new Vector4f(0.628281f, 0.555802f, 0.366065f, 1.0f),
        51.2f
    );

    public static final Material SILVER = new Material(
        2,
        new Vector4f(0.19225f, 0.19225f, 0.19225f, 1.0f),
        new Vector4f(0.50754f, 0.50754f, 0.50754f, 1.0f),
        new Vector4f(0.508273f, 0.508273f, 0.508273f, 1.0f),
        51.2f
    );

    public static final Material BRONZE = new Material(
        3,
        new Vector4f(0.2125f, 0.1275f, 0.054f, 1.0f),
        new Vector4f(0.714f, 0.4284f, 0.18144f, 1.0f),
        new Vector4f(0.393548f, 0.271906f, 0.166721f, 1.0f),
        25.6f
    );

    public static final Material RED = new Material(
        4,
        new Vector4f(0.5f, 0.0f, 0.0f, 1.0f),
        new Vector4f(0.7f, 0.6f, 0.6f, 1.0f),
        new Vector4f(0.7f, 0.6f, 0.6f, 1.0f),
        32.0f
    );

    public static final Material EMERALD = new Material(
        5, 
        new Vector4f(0.0215f, 0.1745f, 0.0215f, 0.55f),
        new Vector4f(0.07568f, 0.61424f, 0.07568f, 0.55f),
        new Vector4f(0.633f, 0.727811f, 0.633f, 0.55f),
        76.8f
    );

    public static final List<Material> MATERIALS = List.of(
        DEFAULT,
        GOLD,
        SILVER,
        BRONZE,
        RED,
        EMERALD
    );
}
