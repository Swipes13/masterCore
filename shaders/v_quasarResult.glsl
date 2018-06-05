#version 400 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

layout (location = 0) in vec3 vpos;
layout (location = 1) in vec3 vcolor;
layout (location = 2) in vec3 vresult;

out VS_OUT {
    vec3 color;
    vec3 result;
} vs_out;

void main() {
    vs_out.color = vcolor;
    vs_out.result = vresult;
    gl_Position = projectionMatrix * viewMatrix * vec4(vpos, 1.0);
}