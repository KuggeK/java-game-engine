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
layout(location = 3) in vec3 vTangent; 

// Out values for fragment shader.
// Varying means these will be interpolated.
out Varying {
    vec2 texCoord;
    vec4 position;
    vec4 posLightSpace;
    vec3 normal;
    mat3 TBN;
    vec3 T;
    vec3 B;
} v_out;

void main() {
    mat3 normalMx = mat3(transpose(inverse(modelMx)));
    v_out.normal = normalize(normalMx * vNormal);

    v_out.T = normalize(vec3(modelMx * vec4(vTangent, 0.0)));
    vec3 N = normalize(vec3(modelMx * vec4(vNormal, 0.0)));
    v_out.B = cross(v_out.T,N);

    v_out.TBN = mat3(v_out.T, v_out.B, N);

    v_out.position = modelMx * vPosition;

    v_out.texCoord = vTextureCoord;

    v_out.posLightSpace = lightSpaceMx * v_out.position;

    // Apply transformations to position
    gl_Position = projectionMx * viewMx * v_out.position;
}