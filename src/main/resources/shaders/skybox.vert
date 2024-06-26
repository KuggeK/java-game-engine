#version 430 core

layout (location = 0) in vec3 vPosition;

uniform mat4 viewMx;
uniform mat4 projectionMx;

out vec3 varyingTexCoord;

void main() {
    varyingTexCoord = vPosition;
    vec4 pos = projectionMx * viewMx * vec4(vPosition, 1.0);
    gl_Position = pos.xyww;
}