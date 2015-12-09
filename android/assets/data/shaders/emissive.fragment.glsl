#line 0 2

#ifdef GL_ES 
precision mediump float;
#endif
 
varying vec4 v_Color;
 
void main() {
	//gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    gl_FragColor = v_Color;
}
