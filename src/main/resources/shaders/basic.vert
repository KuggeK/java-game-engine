#version 430 core

layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTextureCoord;
layout(location = 2) in vec3 vNormal;
layout(location = 3) in mat4 modelMx; // mat4 consumes 4 locations, so this lies in locations 3, 4, 5 and 6.

uniform mat4 viewMx;
uniform mat4 projectionMx;

out vec4 varyingColor;

void main() {
    varyingColor = vec4(0.34, 0.15, 0.43, 1.0);

    gl_Position = ((projectionMx * viewMx) * modelMx) * vPosition;
}