package io.github.kugge.engine.rendering.opengl.shaders;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.jogamp.opengl.GL4;

public class Shaders {
    
    public record Shader(int type, String filename) {}

    public static String[] readSourceFromFile(String fileName) throws IOException, URISyntaxException {
        List<String> linesList = Files.readAllLines(Paths.get(Shaders.class.getResource("/shaders/" + fileName).toURI()));
        linesList = linesList.stream().map(line -> line.concat("\n")).collect(Collectors.toList());
        String[] linesArr = new String[linesList.size()];
        linesList.toArray(linesArr);
        return linesArr;
    }

    public static int getShaderStatus(int shader, GL4 gl, int statusType) {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        intBuffer.put(0, 9);
        gl.glGetShaderiv(shader, statusType, intBuffer);
        return intBuffer.get(0);
    }

    public static int loadShaders(Shader[] shaders, GL4 gl) throws Exception {
        int program = gl.glCreateProgram();
        for (int i = 0; i < shaders.length; ++i) {
            Shader shader = shaders[i];
            int shaderId = gl.glCreateShader(shader.type);
            String[] source = readSourceFromFile(shader.filename);

            gl.glShaderSource(shaderId, source.length, source, null, 0);
            gl.glCompileShader(shaderId);
            gl.glAttachShader(program, shaderId);

            if (getShaderStatus(shaderId, gl, GL4.GL_COMPILE_STATUS) == 0) {
                int logLength = getShaderStatus(shaderId, gl, GL4.GL_INFO_LOG_LENGTH);
                if (logLength > 0) {
                    ByteBuffer log = ByteBuffer.allocate(logLength);                    
                    gl.glGetShaderInfoLog(shaderId, logLength, null, log);
                    System.err.println("Shader compilation failed: " + new String(log.array()));
                } else {
                    System.err.println("Shader compilation failed for an unknown reason");
                }
            } else {
                System.out.println("Shader compilation successful");
            }
        }

        gl.glLinkProgram(program);
        return program;
    }

}
