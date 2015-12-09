#line 0 1

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
 
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoords0;
varying vec3 v_normal;

void main() {
	v_texCoords0 = a_texCoord0;

	gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
