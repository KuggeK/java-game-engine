#version 430 core

in vec2 texCoord;
flat in int flatTextureIdx;
out vec4 FragColor;

uniform sampler2DArray textureArray;

void main() {
    vec4 color;
    if (flatTextureIdx == -1) {
        color = vec4(0.4, 0.1, 0.2, 1.0);
    } else {
        color = texture(textureArray, vec3(texCoord, flatTextureIdx));
    }
    FragColor = color;
}