#version 430 core

layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTextureCoord;
layout(location = 2) in vec3 vNormal;
layout(location = 3) in mat4 modelMx; // mat4 consumes 4 locations, so this lies in locations 3, 4, 5 and 6.
layout(location = 7) in int textureIdx;

uniform mat4 viewMx;
uniform mat4 projectionMx;
uniform sampler2DArray textureArray; // used in fragment shader

out vec2 texCoord;
flat out int flatTextureIdx;

void main() {
    texCoord = vTextureCoord;
    flatTextureIdx = textureIdx;
    gl_Position = ((projectionMx * viewMx) * modelMx) * vPosition;
}