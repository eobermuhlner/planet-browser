#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision highp float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform float u_time;
uniform vec4 u_cameraPosition;

varying vec2 v_texCoords0;

varying vec3 v_normal;
varying float v_lambertFactor;
varying vec3 v_debug_vec3;

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif


void main() {
	vec3 normal = v_normal;

	float factor = 1.0 - v_lambertFactor;
	factor = factor * factor * factor;

	vec4 centerColor = vec4(0.5, 0.5, 1.0, 0.1);
	vec4 horizonColor = vec4(0.8, 0.8, 1.0, 1.0);
	vec4 spaceColor = vec4(0.8, 0.8, 0.1, 0.0);

	float atmosphereEnd = 0.3;
	vec4 color = mix(centerColor, horizonColor, factor / atmosphereEnd);

	if (factor > atmosphereEnd) {
		//color = vec4(1.0, 0.0, 0.0, 1.0);
		float fadeout = (factor - atmosphereEnd) / (1.0 - atmosphereEnd);
		fadeout = sqrt(fadeout);
		color = mix(horizonColor, spaceColor, fadeout);
	}
	
	//vec4 color = vec4(vec3(factor), 1.0);
	gl_FragColor.rgba = color;
}


