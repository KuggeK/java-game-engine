#version 430 core

// Uniforms
uniform mat4 viewMx;
uniform mat4 projectionMx;
uniform mat4 modelMx;

// Per mesh attributes
layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTextureCoord;

out vec2 varyingTexCoord;

void main() {
    varyingTexCoord = vTextureCoord;
    gl_Position = projectionMx * viewMx * modelMx * vPosition;
}