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

uniform float u_normalStep;

uniform float u_time;

uniform float u_random0;
uniform float u_random1;
uniform float u_random2;
uniform float u_random3;
uniform float u_random4;
uniform float u_random5;
uniform float u_random6;
uniform float u_random7;
uniform float u_random8;
uniform float u_random9;

uniform vec4 u_planetColor0;
uniform vec4 u_planetColor1;
uniform vec4 u_planetColor2;

varying vec2 v_texCoords0;

//-------------------------------------------------------------------

//
// GLSL textureless classic 2D noise "cnoise",
// with an RSL-style periodic variant "pnoise".
// Author:  Stefan Gustavson (stefan.gustavson@liu.se)
// Version: 2011-08-22
//
// Many thanks to Ian McEwan of Ashima Arts for the
// ideas for permutation and gradient selection.
//
// Copyright (c) 2011 Stefan Gustavson. All rights reserved.
// Distributed under the MIT license. See LICENSE file.
// https://github.com/ashima/webgl-noise
//

vec4 mod289(vec4 x)
{
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 permute(vec4 x)
{
  return mod289(((x*34.0)+1.0)*x);
}

vec4 taylorInvSqrt(vec4 r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}

vec2 fade(vec2 t) {
  return t*t*t*(t*(t*6.0-15.0)+10.0);
}

// Classic Perlin noise
float cnoise(vec2 P)
{
  vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
  vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
  Pi = mod289(Pi); // To avoid truncation effects in permutation
  vec4 ix = Pi.xzxz;
  vec4 iy = Pi.yyww;
  vec4 fx = Pf.xzxz;
  vec4 fy = Pf.yyww;

  vec4 i = permute(permute(ix) + iy);

  vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
  vec4 gy = abs(gx) - 0.5 ;
  vec4 tx = floor(gx + 0.5);
  gx = gx - tx;

  vec2 g00 = vec2(gx.x,gy.x);
  vec2 g10 = vec2(gx.y,gy.y);
  vec2 g01 = vec2(gx.z,gy.z);
  vec2 g11 = vec2(gx.w,gy.w);

  vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
  g00 *= norm.x;  
  g01 *= norm.y;  
  g10 *= norm.z;  
  g11 *= norm.w;  

  float n00 = dot(g00, vec2(fx.x, fy.x));
  float n10 = dot(g10, vec2(fx.y, fy.y));
  float n01 = dot(g01, vec2(fx.z, fy.z));
  float n11 = dot(g11, vec2(fx.w, fy.w));

  vec2 fade_xy = fade(Pf.xy);
  vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
  float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
  return 2.3 * n_xy;
}

// Classic Perlin noise, periodic variant
float pnoise(vec2 P, vec2 rep)
{
  vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
  vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
  Pi = mod(Pi, rep.xyxy); // To create noise with explicit period
  Pi = mod289(Pi);        // To avoid truncation effects in permutation
  vec4 ix = Pi.xzxz;
  vec4 iy = Pi.yyww;
  vec4 fx = Pf.xzxz;
  vec4 fy = Pf.yyww;

  vec4 i = permute(permute(ix) + iy);

  vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
  vec4 gy = abs(gx) - 0.5 ;
  vec4 tx = floor(gx + 0.5);
  gx = gx - tx;

  vec2 g00 = vec2(gx.x,gy.x);
  vec2 g10 = vec2(gx.y,gy.y);
  vec2 g01 = vec2(gx.z,gy.z);
  vec2 g11 = vec2(gx.w,gy.w);

  vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));
  g00 *= norm.x;  
  g01 *= norm.y;  
  g10 *= norm.z;  
  g11 *= norm.w;  

  float n00 = dot(g00, vec2(fx.x, fy.x));
  float n10 = dot(g10, vec2(fx.y, fy.y));
  float n01 = dot(g01, vec2(fx.z, fy.z));
  float n11 = dot(g11, vec2(fx.w, fy.w));

  vec2 fade_xy = fade(Pf.xy);
  vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
  float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
  return 2.3 * n_xy;
}

//-------------------------------------------------------------------

float pnoise2(vec2 P, float period) {
	return pnoise(P*period, vec2(period, period));
}

float pnoise1(float x, float period) {
	return pnoise2(vec2(x, 0.0), period);
}

vec3 encode_rgb888(float value) {
	vec3 bitShift = vec3(256.0*256.0, 256.0, 1.0);
	vec3 bitMask = vec3(0.0, 1.0/256.0, 1.0/256.0);
	vec3 comp = fract(clamp(value, 0.0, 1.0) * bitShift);
	comp -= comp.xxy * bitMask;
	return comp;
}

vec3 toColor(float value) {
    float r = clamp(-value, 0.0, 1.0);
    float g = clamp(value, 0.0, 1.0);
    float b = 0.0;
    return vec3(r, g, b);
}

float planetNoise(vec2 P) {
	float noise = 0.0; 
	noise += pnoise2(P+vec2(u_random0, u_random1), 10.0) * (0.2 + (u_random0 + u_random9) * 0.4);
	noise += pnoise2(P+vec2(u_random2, u_random3), 50.0) * (0.2 + (u_random1 + u_random9) * 0.4);
	noise += pnoise2(P+vec2(u_random4, u_random5), 100.0) * (0.3 + (u_random2 + u_random9) * 0.2);
	noise += pnoise2(P+vec2(u_random6, u_random7), 200.0) * (0.05 + (u_random3 + u_random9) * 0.1);
	noise += pnoise2(P+vec2(u_random8, u_random9), 500.0) * (0.02 + (u_random4 + u_random9) * 0.15);

	return noise;
}

//	noise += pnoise2(P+vec2(u_random0+u_random4, u_random1+u_random6), baseFrequency * 1.0) * baseFactor / 1.0;


float jupiterNoise(vec2 texCoords) {
	float distEquator = abs(texCoords.t - 0.5) * 2.0;
	float noise = planetNoise(vec2(texCoords.x+distEquator*0.6, texCoords.y));
	
	float distPol = 1.0 - distEquator;
	float disturbance = 0.0;
	disturbance += pnoise1(distPol+(u_random0 + u_random8), 3.0+(u_random3 + u_random8)*3.0) * 1.0;
	disturbance += pnoise1(distPol+(u_random1 + u_random8), 9.0+(u_random4 + u_random8)*5.0) * 0.5;
	disturbance += pnoise1(distPol+(u_random2 + u_random8), 20.0+(u_random5 + u_random8)*10.0) * 0.1;
//	disturbance += pnoise1(distPol+(u_random3 + u_random8), 20.0+(u_random6 + u_random8)*20.0) * 0.05;
//	disturbance += pnoise1(distPol+(u_random4 + u_random8), 20.0+(u_random7 + u_random8)*40.0) * 0.025;
//	disturbance += pnoise1(distPol+(u_random5 + u_random8), 20.0+(u_random8 + u_random8)*80.0) * 0.0125;
//	disturbance = disturbance*disturbance*2.0;
	float noiseFactor = (u_random6 + u_random9) * 0.1;
	float noiseDistEquator = noise * noiseFactor * disturbance;
	return noiseDistEquator;
}

vec3 planetColor(float height, float distEquator) {
	vec3 color1 = u_planetColor0.rgb;
	vec3 color2 = u_planetColor1.rgb;
	vec3 color3 = u_planetColor2.rgb;
	vec3 color4 = mix(u_planetColor0, u_planetColor2, pnoise1(distEquator, 2.0+u_random9*10.0)).rgb;
	
	float v1 = pnoise1(distEquator+u_random1, 2.0+u_random2*10.0);
	float v2 = pnoise1(distEquator+u_random3, 2.0+u_random4*10.0);
	float v3 = height * 10.0;
	
	vec3 mix1 = mix(color1, color2, v1);
	vec3 mix2 = mix(color3, color4, v2);
	vec3 mix3 = mix(mix1, mix2, v3);
	return mix3;
}

void main() {
	float distEquator = abs(v_texCoords0.t - 0.5) * 2.0;
	float height = jupiterNoise(v_texCoords0);

	vec3 bumpColor = vec3(0.0, 0.0, 0.0);
	vec3 diffuseColor = vec3(0.0, 0.0, 0.0);
	vec3 normalColor = vec3(0.5, 0.5, 1.0);
	vec3 specularColor = vec3(0.0, 0.0, 0.0);
	vec3 emissiveColor = vec3(0.0, 0.0, 0.0);

	#if defined(createBumpFlag)
		bumpColor = encode_rgb888(height);	
	#endif
	
	#if defined(createDiffuseFlag)
		diffuseColor = planetColor(height, distEquator);
	#endif
	
	#if defined(createNormalFlag)
		vec3 normal;

		float offset = u_normalStep;
		float heightDeltaX = jupiterNoise(v_texCoords0 + vec2(offset, 0.0));
		float heightDeltaY = jupiterNoise(v_texCoords0 + vec2(0.0, offset));
		float deltaX = height - heightDeltaX;
		float deltaY = height - heightDeltaY;
		vec3 tangentX = vec3(offset, 0, deltaX / 200.0);
		vec3 tangentY = vec3(0, offset, deltaY / 200.0);
		normal = normalize(cross(tangentX, tangentY));

		normalColor = clamp((normal + 1.0) / 2.0, 0.0, 1.0);
	#endif

	#if defined(multiTextureRenderingFlag)
		#if defined(createBumpFlag)
			gl_FragData[createBumpOutput].rgb = bumpColor;
		#endif
		#if defined(createDiffuseFlag)
			gl_FragData[createDiffuseOutput].rgb = diffuseColor;
		#endif
		#if defined(createNormalFlag)
			gl_FragData[createNormalOutput].rgb = normalColor;
		#endif
		#if defined(createSpecularFlag)
			gl_FragData[createSpecularOutput].rgb = specularColor;
		#endif
		#if defined(createEmissiveFlag)
			gl_FragData[createEmissiveOutput].rgb = emissiveColor;
		#endif
	#else
		#if defined(createBumpFlag)
			gl_FragColor.rgb = bumpColor;
		#endif
		#if defined(createDiffuseFlag)
			gl_FragColor.rgb = diffuseColor;
		#endif
		#if defined(createNormalFlag)
			gl_FragColor.rgb = normalColor;
		#endif
		#if defined(createSpecularFlag)
			gl_FragColor.rgb = specularColor;
		#endif
		#if defined(createEmissiveFlag)
			gl_FragColor.rgb = emissiveColor;
		#endif
	#endif
}


