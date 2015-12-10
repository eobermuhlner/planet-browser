//
// Vertex shader for emissive texture depending on vertex normal and direction to camera.
// 
// Calculates the vector between the vertex position and the camera position.
// This vector and the texture coordinates are passed to the fragment shader.
//
// The goal of this shader is render a textured object independent of any lightsource.
// This is also called self-illumination. 
//

attribute vec3 a_position;
attribute vec2 a_texCoord0;
 
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoords0;

void main() {
	v_texCoords0 = a_texCoord0;

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
