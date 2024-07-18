#version 430 core

layout(location = 0) in vec4 vPosition;

uniform mat4 modelMx;
uniform mat4 lightSpaceMx;

void main() {
    gl_Position = lightSpaceMx * modelMx * vPosition;
}