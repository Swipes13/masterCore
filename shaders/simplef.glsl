#version 400 core

uniform sampler2D texture_diffuse;

in vec3 norm;
in vec2 tutv;
in vec3 light;

out vec4 out_color;

void main() {
    out_color = vec4(1, 1, 1, 1);// texture(texture_diffuse, tutv);
    out_color.rgb *= dot(norm, light);
}
