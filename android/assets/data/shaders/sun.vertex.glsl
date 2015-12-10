//
// Vertex shader for emissive color depending on vertex normal and direction to camera, mixing with constant emissive color.
// 
// The vertex color is strongest when the vertex normal is in the direction of the camera.
// This corrected color is mixed with the constant emissive color.
//
// The goal of this shader is to create an emissive light source that looks like
// the sun (through sunglasses to prevent glare) or a not too bright lamp. 
//

#ifdef GL_ES 
precision highp float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
 
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

uniform vec4 u_cameraPosition;

varying vec2 v_texCoords0;
varying float v_lambertFactor;

void main() {
	v_texCoords0 = a_texCoord0;

	// vertex position in world space
	vec3 position = vec3(u_worldTrans * vec4(a_position, 1.0));
	// vertex normal in world space
	vec3 normal = vec3(u_worldTrans * vec4(a_normal, 0.0));
	
	// normalized vector from vertex position to camera position
	vec3 viewVecNorm = normalize(u_cameraPosition.xyz - position.xyz);
	// lambert factor represents angle between vertex normal and vector to camera 
	v_lambertFactor = clamp(dot(normal, viewVecNorm), 0.0, 1.0);

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
