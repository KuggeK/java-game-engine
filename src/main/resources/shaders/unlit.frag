#version 430 core

uniform vec4 color;

uniform sampler2D instanceTexture;
uniform bool textured;

in vec2 varyingTexCoord;

void main() {
    // Texture contribution
    vec4 textureColor;
    if (textured) {
        textureColor = texture(instanceTexture, varyingTexCoord);
    } else {
        textureColor = vec4(1.0);
    }

    gl_FragColor = color * textureColor;
}