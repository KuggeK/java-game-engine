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

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

// Uniforms used by fragment shader.
uniform vec3 viewPos;
uniform sampler2D instanceTexture;

uniform sampler2D shadowMap;
uniform sampler2D normalMap;

uniform bool normalMapped;

#define MAX_LIGHTS 50
uniform int nLights;
uniform PositionalLight posLights[MAX_LIGHTS];

uniform vec4 globalAmbient;
uniform DirectionalLight dirLight;

uniform Material material;
uniform bool textured;

// Varying means these will be interpolated.
in Varying {
    vec2 texCoord;
    vec4 position;
    vec4 posLightSpace;
    vec3 normal;
    mat3 TBN;
    vec3 T;
    vec3 B;
} f_in;

Light CalculateDirLight(DirectionalLight dirLight, vec3 N, vec3 V);
Light CalculatePosLight(PositionalLight light, vec4 fragPos, vec3 N, vec3 V);
Light BlinnPhongShading(vec3 L, vec3 N, vec3 V);
float CalculateShadow();

void main() {
    // The N vector needs to be normalized after interpolation.
    vec3 N;
    if (normalMapped) {
        N = texture(normalMap, f_in.texCoord).rgb;
        N = N * 2.0 - 1.0;
        N = normalize(f_in.TBN * N);
    } else {
        N = normalize(f_in.normal);
    }

    vec3 V = normalize(viewPos - f_in.position.xyz);

    // Texture contribution
    vec4 textureColor;
    if (textured) {
        textureColor = texture(instanceTexture, f_in.texCoord);
    } else {
        textureColor = vec4(1.0);
    }

    // Calculate the value of the contribution of all lights
    vec4 lightColor = vec4(0.0, 0.0, 0.0, 0.0);

    // Global ambient contribution
    lightColor += globalAmbient * material.ambient;

    float shadow = CalculateShadow();

    // Directional light contribution
    Light dirContribution = CalculateDirLight(dirLight, N, V);
    lightColor += dirContribution.ambient + (1.0 - shadow) * (dirContribution.diffuse + dirContribution.specular);

    // Positional light contribution
    Light posContribution;
    for (int i = 0; i < nLights && i < MAX_LIGHTS; ++i) {
        posContribution = CalculatePosLight(posLights[i], f_in.position, N, V);
        lightColor += posContribution.ambient + posContribution.diffuse + posContribution.specular;
    }
    
    gl_FragColor = lightColor * textureColor;
}

float CalculateShadow() {
    vec3 projCoords = f_in.posLightSpace.xyz / f_in.posLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;

    float currentDepth = projCoords.z;

    float bias = 0.0001;
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    int range = 2;
    for (int x = -range; x <= range; ++x) {
        for (int y = -range; y <= range; ++y) {
            float texDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += (currentDepth - bias) > texDepth ? 1.0 : 0.0;
        }
    }

    shadow /= (pow(range + range + 1, 2));

    if (projCoords.z > 1.0) {
        shadow = 0.0;
    }

    return shadow;
}

Light BlinnPhongShading(vec3 L, vec3 N, vec3 V) {
    vec3 H = normalize(L + V);

    // Ambient.
    vec4 ambient = material.ambient;

    // Diffuse. Angle between the light direction and the fragment normal.
    float diffAmount = max(dot(N, L), 0.0);
    vec4 diffuse = material.diffuse * diffAmount;

    // Specular (Phong). Angle between the direction to the view and the reflection vector to the power of the material shininess.
    float specAmount = pow(max(dot(N, H), 0.0), material.shininess);
    vec4 specular = material.specular * specAmount;

    return Light(ambient, diffuse, specular);
}

Light CalculateDirLight(DirectionalLight light, vec3 N, vec3 V) {
    vec3 L = normalize(-light.direction);
    Light res = BlinnPhongShading(L, N, V);
    Light final = Light(
        res.ambient * light.ambient,
        res.diffuse * light.diffuse,
        res.specular * light.specular
    );
    return final;
}

Light CalculatePosLight(PositionalLight light, vec4 fragPos, vec3 N, vec3 V) {
    vec4 fragToLight = light.position - fragPos;
    
    float dist = length(fragToLight);
    // If the frag is not inside the cutoff, the light has no effect
    if (light.attenuation.radius < dist) {
        vec4 nothing = vec4(0.0);
        return Light(nothing, nothing, nothing);
    }

    vec3 L = normalize(fragToLight.xyz);
    Light res = BlinnPhongShading(L, N, V);

    float attenuation = 1.0 / (
        light.attenuation.constant + 
        light.attenuation.linear * dist + 
        light.attenuation.quadratic * pow(dist, 2)
    );

    return Light(
        light.ambient * res.ambient * attenuation, 
        light.diffuse * res.diffuse * attenuation,
        light.specular * res.specular * attenuation
    );
}
