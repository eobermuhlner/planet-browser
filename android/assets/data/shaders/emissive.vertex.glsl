attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
 
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

uniform vec4 u_emissiveColor;
varying vec4 v_emissiveColor;
uniform vec4 u_cameraPosition;
 
varying vec4 v_Color;

void main() {
	vec3 pos = vec3(u_worldTrans * vec4(a_position, 1.0));
	vec3 normal = vec3(u_worldTrans * vec4(a_normal, 0.0));
	
	vec3 viewVecNorm = normalize(u_cameraPosition.xyz - pos.xyz);
	float NdotL = clamp(dot(normal, viewVecNorm), 0.0, 1.0);
	vec4 emissiveColor = u_emissiveColor + a_color; 
	v_Color = emissiveColor * NdotL * 0.75 + emissiveColor * 0.25;

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
