package kugge.rendering.core.objects.materials;

public class Materials {

    public static final Material DEFAULT = new Material(
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.8f, 0.8f, 0.8f, 1.0f},
        new float[] {0.1f, 0.1f, 0.1f, 1.0f},
        0.1f
    );

    public static final Material GOLD = new Material(
        new float[] {0.24725f, 0.1995f, 0.0745f, 1.0f},
        new float[] {0.75164f, 0.60648f, 0.22648f, 1.0f},
        new float[] {0.628281f, 0.555802f, 0.366065f, 1.0f},
        51.2f
    );

    public static final Material SILVER = new Material(
        new float[] {0.19225f, 0.19225f, 0.19225f, 1.0f},
        new float[] {0.50754f, 0.50754f, 0.50754f, 1.0f},
        new float[] {0.508273f, 0.508273f, 0.508273f, 1.0f},
        51.2f
    );

    public static final Material BRONZE = new Material(
        new float[] {0.2125f, 0.1275f, 0.054f, 1.0f},
        new float[] {0.714f, 0.4284f, 0.18144f, 1.0f},
        new float[] {0.393548f, 0.271906f, 0.166721f, 1.0f},
        25.6f
    );
}
