#version 400 core
layout (points) in;
layout (triangle_strip, max_vertices = 14) out;

in VS_OUT {
    vec3 color;
    vec3 result;
    float rtScale;
    mat4 projectionMatrix;
    mat4 viewMatrix;
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
    vec2(-0.025, -0.025),
    vec2(0.025, -0.025),
    vec2(-0.025,  0.025),
    vec2(0.025,  0.025),
    vec2(0.0,  0.05)
);

const int tethraedronSize = 6;
const vec3 tethraedron[tethraedronSize] = vec3[](
    vec3(0, 0, 0), // A
    vec3(0, 1, 0), // B
    vec3(0.4, 0, 0), // C

    vec3(0, 0, 0.4), // D
    vec3(0, 0, 0), // A
    vec3(0, 1, 0) // B
);

float PI = 3.1415926535897932384626433832795;
vec4 setAxisAngle(vec3 axis, float rad) {
  rad = rad * 0.5;
  float s = sin(rad);
  return vec4(s * axis[0], s * axis[1], s * axis[2], cos(rad));
}
vec3 xUnitVec3 = vec3(1.0, 0.0, 0.0);
vec3 yUnitVec3 = vec3(0.0, 1.0, 0.0);

vec4 rotationTo(vec3 a, vec3 b) {
  float vecDot = dot(a, b);
  vec3 tmpvec3 = vec3(0);
  if (vecDot < -0.999999) {
    tmpvec3 = cross(xUnitVec3, a);
    if (length(tmpvec3) < 0.000001) {
      tmpvec3 = cross(yUnitVec3, a);
    }
    tmpvec3 = normalize(tmpvec3);
    return setAxisAngle(tmpvec3, PI);
  } else if (vecDot > 0.999999) {
    return vec4(0,0,0,1);
  } else {
    tmpvec3 = cross(a, b);
    vec4 _out = vec4(tmpvec3[0], tmpvec3[1], tmpvec3[2], 1.0 + vecDot);
    return normalize(_out);
  }
}

vec4 multQuat(vec4 q1, vec4 q2) {
  return vec4(
    q1.w * q2.x + q1.x * q2.w + q1.z * q2.y - q1.y * q2.z,
    q1.w * q2.y + q1.y * q2.w + q1.x * q2.z - q1.z * q2.x,
    q1.w * q2.z + q1.z * q2.w + q1.y * q2.x - q1.x * q2.y,
    q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z
  );
}

vec3 rotateVector(vec4 quat, vec3 vec) {
  // https://twistedpairdevelopment.wordpress.com/2013/02/11/rotating-a-vector-by-a-quaternion-in-glsl/
  vec4 qv = multQuat( quat, vec4(vec, 0.0) );
  return multQuat( qv, vec4(-quat.x, -quat.y, -quat.z, quat.w) ).xyz;
}


void createPoint(vec4 position) {
    fColor = gs_in[0].color;

    vec3 forward = vec3(0.0, 1.0, 0.0);
    vec4 quaternion1 = rotationTo(gs_in[0].result, forward);

    for (int i = 0; i < tethraedronSize; i++) {
        vec3 arrow = vec3(tethraedron[i] * gs_in[0].rtScale / 20);
        // rotate by gs_in[0].result
        vec4 positionRotated = vec4(rotateVector(quaternion1, arrow), 0.0);
        gl_Position = gs_in[0].projectionMatrix * gs_in[0].viewMatrix * (position + positionRotated);
        EmitVertex();
    }
//    for (int i = 0; i < 14; i++) {
//        gl_Position = position + vec4(cube_strip[i] / 45 * gs_in[0].rtScale, 0.0);
//        EmitVertex();
//    }

    EndPrimitive();
}

void main() {
    createPoint(gl_in[0].gl_Position);
}
