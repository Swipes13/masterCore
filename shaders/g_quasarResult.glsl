#version 400 core
layout (points) in;
layout (triangle_strip, max_vertices = 5) out;

in VS_OUT {
    vec3 color;
    vec3 result;
} gs_in[];

out vec3 fColor;

const int arrowStripSize = 5;
const vec3 cube_strip[14] = vec3[](
    vec3(-1.f, 1.f, 1.f),     // Front-top-left
    vec3(1.f, 1.f, 1.f),      // Front-top-right
    vec3(-1.f, -1.f, 1.f),    // Front-bottom-left
    vec3(1.f, -1.f, 1.f),     // Front-bottom-right
    vec3(1.f, -1.f, -1.f),    // Back-bottom-right
    vec3(1.f, 1.f, 1.f),      // Front-top-right
    vec3(1.f, 1.f, -1.f),     // Back-top-right
    vec3(-1.f, 1.f, 1.f),     // Front-top-left
    vec3(-1.f, 1.f, -1.f),    // Back-top-left
    vec3(-1.f, -1.f, 1.f),    // Front-bottom-left
    vec3(-1.f, -1.f, -1.f),   // Back-bottom-left
    vec3(1.f, -1.f, -1.f),    // Back-bottom-right
    vec3(-1.f, 1.f, -1.f),    // Back-top-left
    vec3(1.f, 1.f, -1.)      // Back-top-right
);

const vec2 arrowStrip[arrowStripSize] = vec2[](
    vec2(-0.03, -0.03),
    vec2(0.03, -0.03),
    vec2(-0.03,  0.03),
    vec2(0.03,  0.03),
    vec2(0.0,  0.06)
);

void createPoint(vec4 position) {
    fColor = gs_in[0].color;
    for (int i = 0; i < arrowStripSize; i++) {
        gl_Position = position + vec4(arrowStrip[i], 0.0, 0.0);
        EmitVertex();
    }
    EndPrimitive();
}

void main() {
    createPoint(gl_in[0].gl_Position);
}
