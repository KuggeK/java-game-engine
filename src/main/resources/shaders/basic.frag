#version 430 core

// Structs for lighting
struct Light {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
};

struct Attenuation {
    float constant;
    float linear;
    float quadratic;
    float radius;
};

struct PositionalLight {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec4 position;
    Attenuation attenuation;
};

struct DirectionalLight {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 direction;
};

// Uniforms used by fragment shader.
uniform vec3 viewPos;
uniform sampler2DArray textureArray;

#define MAX_LIGHTS 50
uniform int nLights;
uniform PositionalLight posLights[MAX_LIGHTS];

uniform vec4 globalAmbient;
uniform DirectionalLight dirLight;

// Attributes passed by vertex shader
// Material
flat in vec4 matAmbient;
flat in vec4 matDiffuse;
flat in vec4 matSpecular;
flat in float matShininess;

// Interpolated attributes
in vec2 varyingTexCoord;
in vec3 worldNormal;
in vec4 worldPosition;

// Index into the texture array for instance's texture.
flat in int flatTextureIdx;

vec4 CalculateDirLight(DirectionalLight dirLight, vec3 N, vec3 V);
vec4 CalculatePosLight(PositionalLight light, vec4 fragPos, vec3 N, vec3 V);
Light BlinnPhongShading(vec3 L, vec3 N, vec3 V);

void main() {
    // The N vector needs to be normalized after interpolation.
    vec3 N = normalize(worldNormal);
    vec3 V = normalize(viewPos - worldPosition.xyz);

    // Texture contribution
    vec4 textureColor;
    if (flatTextureIdx == -1) {
        textureColor = vec4(1.0);
    } else {
        textureColor = texture(textureArray, vec3(varyingTexCoord, flatTextureIdx));
    }

    // Calculate the value of the contribution of all lights
    vec4 lightColor = vec4(0.0, 0.0, 0.0, 0.0);

    // Global ambient contribution
    lightColor += globalAmbient * matAmbient;

    // Directional light contribution
    lightColor += CalculateDirLight(dirLight, N, V);

    // Positional light contribution
    for (int i = 0; i < nLights; ++i) {
        lightColor += CalculatePosLight(posLights[i], worldPosition, N, V);
    }
    
    gl_FragColor = textureColor * lightColor;
}

Light BlinnPhongShading(vec3 L, vec3 N, vec3 V) {
    vec3 H = normalize(L + V);

    // Ambient.
    vec4 ambient = matAmbient;

    // Diffuse. Angle between the light direction and the fragment normal.
    float diffAmount = max(dot(N, L), 0.0);
    vec4 diffuse = matDiffuse * diffAmount;

    // Specular (Phong). Angle between the direction to the view and the reflection vector to the power of the material shininess.
    float specAmount = pow(max(dot(N, H), 0.0), matShininess);
    vec4 specular = matSpecular * specAmount;

    return Light(ambient, diffuse, specular);
}

vec4 CalculateDirLight(DirectionalLight light, vec3 N, vec3 V) {
    vec3 L = normalize(-light.direction);
    Light res = BlinnPhongShading(L, N, V);
    return (
        light.ambient * res.ambient + 
        light.diffuse * res.diffuse +
        light.specular * res.specular
    );
}

vec4 CalculatePosLight(PositionalLight light, vec4 fragPos, vec3 N, vec3 V) {
    vec4 fragToLight = light.position - fragPos;
    
    float dist = length(fragToLight);
    // If the frag is not inside the cutoff, the light has no effect
    if (light.attenuation.radius < dist) {
        return vec4(0.0);
    }

    vec3 L = normalize(fragToLight.xyz);
    Light res = BlinnPhongShading(L, N, V);

    float attenuation = 1.0 / (
        light.attenuation.constant + 
        light.attenuation.linear * dist + 
        light.attenuation.quadratic * pow(dist, 2)
    );

    return (
        light.ambient * res.ambient * attenuation + 
        light.diffuse * res.diffuse * attenuation +
        light.specular * res.specular * attenuation
    );
}
