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
out Material {
    flat out vec4 ambient;
    flat out vec4 diffuse;
    flat out vec4 specular;
    flat out float shininess;
} material;

// Varying means these will be interpolated.
out Varying {
    out vec2 texCoord;
    out vec3 normal;
    out vec4 position;
    out vec4 posLightSpace;
} coords;

flat out int flatTextureIdx;

void main() {
    // Send attributes to fragment shader
    flatTextureIdx = textureIdx;
    material.ambient = materialAmbient;
    material.diffuse = materialDiffuse;
    material.specular = materialSpecular;
    material.shininess = materialShininess;

    mat3 normalMx = mat3(transpose(inverse(modelMx)));
    coords.normal = normalize(normalMx * vNormal);

    coords.position = modelMx * vPosition;

    coords.texCoord = vTextureCoord;

    coords.posLightSpace = lightSpaceMx * coords.position;

    // Apply transformations to position
    gl_Position = projectionMx * viewMx * coords.position;
}