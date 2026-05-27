#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;
uniform float flMaxHeight;

out vec4 fColor;
out vec2 fTexCoords;
out vec3 fPos;
out float fMaxHeight;

void main() {
    fColor = aColor;
    fTexCoords = aTexCoords;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
    fPos = aPos;
    fMaxHeight = flMaxHeight;
}

#type fragment
#version 330 core

uniform sampler2D TEX_SAMPLER;

in vec4 fColor;
in vec2 fTexCoords;
in vec3 fPos;
in float fMaxHeight;

out vec4 color;

void main() {
//    color = texture(TEX_SAMPLER, fTexCoords);
    // Hardcoded height factor value
    float factor = abs(fPos.z) / fMaxHeight;
    if (factor <= 0.1) {
        color = vec4(0.2, 0.2, 0.8, 1.0);
    }
    else if (0.1 < factor && factor <= 0.2) {
        color = vec4(0.8, 0.8, 0.0, factor);
    }
    else {
        color = vec4(0.0, 0.8, 0.0, 1.0);
    }
}