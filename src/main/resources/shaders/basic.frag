#version 430 core

in vec4 varyingColor;
out vec4 FragColor;

void main() {
    FragColor = varyingColor;
}