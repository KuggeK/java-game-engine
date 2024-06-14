#version 430 core

// Uniforms
uniform mat4 viewMx;
uniform mat4 projectionMx;
uniform mat4 lightSpaceMx;

// Per mesh attributes
layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTextureCoord;
layout(location = 2) in vec3 vNormal;

// The rest of these in values are per instance attributes.
layout(location = 3) in mat4 modelMx; // mat4 consumes 4 locations, so this lies in locations 3, 4, 5 and 6.
layout(location = 7) in int textureIdx;

// Material in values.
layout(location = 8) in vec4 materialAmbient;
layout(location = 9) in vec4 materialDiffuse;
layout(location = 10) in vec4 materialSpecular;
layout(location = 11) in float materialShininess;

// Out values for fragment shader.
// Material values are flat for the object.
flat out vec4 matAmbient;
flat out vec4 matDiffuse;
flat out vec4 matSpecular;
flat out float matShininess;

// Varying means these will be interpolated.
out vec2 varyingTexCoord;
out vec3 worldNormal;
out vec4 worldPosition;
out vec4 fragPosLightSpace;

flat out int flatTextureIdx;

void main() {
    // Send attributes to fragment shader
    flatTextureIdx = textureIdx;
    matAmbient = materialAmbient;
    matDiffuse = materialDiffuse;
    matSpecular = materialSpecular;
    matShininess = materialShininess;

    mat3 normalMx = mat3(transpose(inverse(modelMx)));
    worldNormal = normalize(normalMx * vNormal);

    worldPosition = modelMx * vPosition;

    varyingTexCoord = vTextureCoord;

    fragPosLightSpace = lightSpaceMx * worldPosition;

    // Apply transformations to position
    gl_Position = projectionMx * viewMx * worldPosition;
}