package io.github.kuggek.engine.rendering.opengl.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import io.github.kuggek.engine.core.assets.ResourceManager;

public class Shaders {
    
    public record Shader(int type, String filename) {}

    public static int getShaderStatus(int shader, GL4 gl, int statusType) {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        intBuffer.put(0, 9);
        gl.glGetShaderiv(shader, statusType, intBuffer);
        return intBuffer.get(0);
    }

    public static int loadShaders(Shader[] shaders, GL4 gl) throws Exception {
        int program = gl.glCreateProgram();
        String shaderFolder = "shaders/";
        for (int i = 0; i < shaders.length; ++i) {
            Shader shader = shaders[i];
            int shaderId = gl.glCreateShader(shader.type);
            String[] source = ResourceManager.readFile(shaderFolder + shader.filename).split("\n");
            
            for (int j = 0; j < source.length; ++j) {
                source[j] = source[j] + "\n";
            }

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
