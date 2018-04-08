#version 400 core
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float red;

layout (location = 0) in vec2 vpos;
layout (location = 1) in vec3 vcolor;

out vec3 color;

void main() {
    color = vec3(red, red, red);
    gl_Position = projectionMatrix * viewMatrix * vec4(vpos.x, vpos.y, 2.0, 1.0);
}