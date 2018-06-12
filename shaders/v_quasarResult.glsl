#version 400 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float orthoZoomCf;

layout (location = 0) in vec3 vpos;
layout (location = 1) in vec3 vcolor;
layout (location = 2) in vec3 vresult;

out VS_OUT {
    vec3 color;
    vec3 result;
    float rtScale;
    mat4 projectionMatrix;
    mat4 viewMatrix;
} vs_out;



void main() {
    vs_out.color = vcolor;
    vs_out.result = vresult;
    vs_out.rtScale = 1 / max(orthoZoomCf == 1 ? orthoZoomCf : orthoZoomCf * 100, 1.0e-7);
    vs_out.projectionMatrix = projectionMatrix;
    vs_out.viewMatrix = viewMatrix;
    gl_Position = vec4(vpos, 1.0);
}