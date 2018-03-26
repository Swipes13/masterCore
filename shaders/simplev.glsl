#version 400 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

layout (location = 0) in vec2 vpos;
layout (location = 1) in vec3 vcolor;

out vec3 color;

void main() {
	color = vcolor;
    gl_Position = projectionMatrix * viewMatrix * vec4(vpos, 0.0, 10.0);
}