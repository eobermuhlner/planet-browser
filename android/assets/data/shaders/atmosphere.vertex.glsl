attribute vec3 a_position;
attribute vec2 a_texCoord0;
 
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoords0;

uniform vec4 u_cameraPosition;

attribute vec3 a_normal;
attribute vec3 a_tangent;
uniform mat3 u_normalMatrix;
varying vec3 v_normal;

varying float v_lambertFactor;

varying vec3 v_debug_vec3;

void main() {
	v_texCoords0 = a_texCoord0;

	// vertex position in world space
	vec3 position = vec3(u_worldTrans * vec4(a_position, 1.0));
	// vertex normal in world space
	vec3 normal = vec3(u_worldTrans * vec4(a_normal, 0.0));

	//vec3 normal = normalize(u_normalMatrix * a_normal);
	v_normal = normal;

	// matrix to convert eye space into tangent space
	//vec3 n = normalize (a_normal);
	//vec3 t = normalize (a_tangent);
	//vec3 b = cross (n, t);
		
	// normalized vector from vertex position to light position in tangent space - passed to fragment shader
	//v_lightVecTangent = normalize (vec3(dot (strongestLightDir, t), dot (strongestLightDir, b), dot (strongestLightDir, n)));

	// normalized vector from vertex position to camera position
	vec3 viewVecNorm = normalize(u_cameraPosition.xyz - position.xyz);
	// lambert factor represents angle between vertex normal and vector to camera 
	v_lambertFactor = clamp(dot(normal, viewVecNorm), 0.0, 1.0);

	gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
