#version 430 core

layout (location = 0) in vec3 vPosition;

uniform mat4 viewMx;
uniform mat4 projectionMx;

out vec3 varyingTexCoord;

void main() {
    varyingTexCoord = vPosition;
    // Remove rotation from view matrix
    mat4 viewRotMx = mat4(mat3(viewMx));
    gl_Position = projectionMx * viewRotMx * vec4(vPosition, 1.0); 
}