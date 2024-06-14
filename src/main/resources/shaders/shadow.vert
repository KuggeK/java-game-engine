#version 430 core

layout(location = 0) in vec4 vPosition;
layout(location = 3) in mat4 modelMx; // This actually occupies locations 3, 4, 5 and 6.

uniform mat4 lightSpaceMx;

void main() {
    gl_Position = lightSpaceMx * modelMx * vPosition;
}