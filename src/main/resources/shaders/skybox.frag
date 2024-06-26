#version 430 core

in vec3 varyingTexCoord;

layout (binding = 0) uniform samplerCube cubeSampler;

void main() {
    gl_FragColor = texture(cubeSampler, varyingTexCoord);
}