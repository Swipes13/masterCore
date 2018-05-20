#version 400 core
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float red;

uniform vec3 lightDir;

layout (location = 0) in vec3 vpos;
layout (location = 1) in vec3 vnorm;
layout (location = 2) in vec2 vtutv;

out vec3 norm;
out vec2 tutv;
out vec3 light;

void main() {
    tutv = vtutv;
    light = lightDir;
    norm = vnorm;
    gl_Position = projectionMatrix * viewMatrix * vec4(vpos.x, vpos.y, vpos.z, 1.0);
}