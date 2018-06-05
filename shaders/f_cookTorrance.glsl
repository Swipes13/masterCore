#version 400 core

uniform sampler2D texture_diffuse;
uniform samplerCube cubemap;

in vec3 norm;
in vec3 texNormal;
in vec3 light;
in vec3 view;
in vec3 wpos;
in vec3 center;

out vec4 out_color;

float cookTorrance(vec3 _normal, vec3 _light, vec3 _view, float roughness_val) {
    if (roughness_val <= 0.0) return 0.0;

    // вычислим средний вектор между положением источника света и вектором взгляда
    vec3  half_vec = normalize( _view + _light );
    // найдем разнообразные скалярные произведения :)
    float NdotL    = max( dot( _normal, _light ), 0.0 );
    float NdotV    = max( dot( _normal, _view ), 0.0 );
    float NdotH    = max( dot( _normal, half_vec ), 1.0e-7 );
    float VdotH    = max( dot( _view,   half_vec ), 0.0 );
    // NdotH не может быть равным нулю, так как в последствии на него надо будет делить

    // вычислим геометрическую составляющую
    float geometric = 2.0 * NdotH / VdotH;
    geometric = min( 1.0, geometric * min(NdotV, NdotL) );

    // вычислим компонент шероховатости поверхности
    float r_sq          = roughness_val * roughness_val;
    float NdotH_sq      = NdotH * NdotH;
    float NdotH_sq_r    = 1.0 / (NdotH_sq * r_sq);
    float roughness_exp = (NdotH_sq - 1.0) * ( NdotH_sq_r );
    float roughness     = exp(roughness_exp) * NdotH_sq_r / (4.0 * NdotH_sq );

    // вычислим коэффициент Френеля, не вводя дополнительный параметр
    float fresnel       = 1.0 / (1.0 + NdotV);

    return min(1.0, (fresnel * geometric * roughness) / (NdotV * NdotL + 1.0e-7));
}

void main() {
    float r = 0.1;
    float rs = cookTorrance(norm, light, view, r);
    vec3 vDiffuse = texture(cubemap, texNormal).xyz;
    vec3 vSpecular = vec3(1, 1, 1);

    out_color = /*vAmbient + */ vec4(dot(norm, view) * (vDiffuse + vSpecular * rs), 1);
}