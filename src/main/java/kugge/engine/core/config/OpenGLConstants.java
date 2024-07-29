package kugge.engine.core.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenGLConstants {
    
    private static Map<Integer, String> valueToConstantMap = new HashMap<>();
    private static Map<String, Integer> constantToValueMap = new HashMap<>();
    private static boolean initialized = false;

    public static String getConstant(Integer value) {
        if (!initialized) {
            initialize();
        }
        return (String) valueToConstantMap.get(value);
    }

    public static Integer getValue(String constant) {
        if (!initialized) {
            initialize();
        }
        return (Integer) constantToValueMap.get(constant);
    }

    private static void initialize() {
        try {
            List<String> constants = Files.readAllLines(Path.of(OpenGLConstants.class.getResource("/GL-constants.csv").toURI()));
            constants.remove(0);
            for (String line : constants) {
                String[] parts = line.split(",");
                String constant = parts[0];
                Integer value = Integer.valueOf(parts[1]);
                valueToConstantMap.put(value, constant);
                constantToValueMap.put(constant, value);
            }
            initialized = true;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
