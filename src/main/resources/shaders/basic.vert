#version 430 core

// Uniforms
uniform mat4 viewMx;
uniform mat4 projectionMx;
uniform mat4 lightSpaceMx;
uniform mat4 modelMx;

// Per mesh attributes
layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTextureCoord;
layout(location = 2) in vec3 vNormal;

// Out values for fragment shader.
// Varying means these will be interpolated.
out Varying {
    out vec2 texCoord;
    out vec3 normal;
    out vec4 position;
    out vec4 posLightSpace;
} coords;

void main() {
    mat3 normalMx = mat3(transpose(inverse(modelMx)));
    coords.normal = normalize(normalMx * vNormal);

    coords.position = modelMx * vPosition;

    coords.texCoord = vTextureCoord;

    coords.posLightSpace = lightSpaceMx * coords.position;

    // Apply transformations to position
    gl_Position = projectionMx * viewMx * coords.position;
}