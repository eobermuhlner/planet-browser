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

uniform float u_heightMin;
uniform float u_heightMax;
uniform float u_heightFrequency;

#ifdef mountainsFlag
uniform float u_heightMountains;
#endif

uniform float u_iceLevel;
uniform float u_heightWater;

#ifdef colorNoiseFlag
uniform float u_colorNoise;
uniform float u_colorFrequency;
#endif

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

varying vec2 v_texCoords0;

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef planetColorsFlag
uniform vec3 u_planetColor0;
uniform vec3 u_planetColor1;
uniform vec3 u_planetColor2;
#endif

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

float pnoise2(vec2 P, float period) {
	return pnoise(P*period, vec2(period, period));
}

float pnoise1(float x, float period) {
	return pnoise2(vec2(x, 0.0), period);
}

// based on http://theorangeduck.com/page/avoiding-shader-conditionals
float when_not(float condition) {
  return 1.0 - condition;
}

float when_neq(float x, float y) {
  return abs(sign(x - y));
}

float when_eq(float x, float y) {
  return 1.0 - when_neq(x, y);
}

float when_gt(float x, float y) {
  return max(sign(x - y), 0.0);
}

float when_lt(float x, float y) {
  return min(1.0 - sign(x - y), 1.0);
}

float when_ge(float x, float y) {
  return 1.0 - when_lt(x, y);
}

float when_le(float x, float y) {
  return 1.0 - when_gt(x, y);
}

vec4 if_then_else(float condition, vec4 trueValue, vec4 falseValue) {
	vec4 result = trueValue * condition;
	result += falseValue * when_not(condition);
	return result;
}

float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	vec2 r1 = vec2(u_random0+u_random4, u_random1+u_random6);
	vec2 r2 = vec2(u_random2+u_random4, u_random3+u_random6);
	vec2 r3 = vec2(u_random4+u_random4, u_random5+u_random6);
	vec2 r4 = vec2(u_random6+u_random4, u_random7+u_random6);
	vec2 r5 = vec2(u_random8+u_random4, u_random9+u_random6);
	vec2 r6 = vec2(u_random0+u_random5, u_random7+u_random7);
	vec2 r7 = vec2(u_random2+u_random5, u_random5+u_random7);
	vec2 r8 = vec2(u_random4+u_random5, u_random3+u_random7);
	vec2 r9 = vec2(u_random6+u_random5, u_random1+u_random7);

	float noise = 0.0;
	noise += pnoise2(P+r1, baseFrequency * 1.0) * baseFactor / 1.0;
	noise += pnoise2(P+r2, baseFrequency * 2.0) * baseFactor / 2.0;
	noise += pnoise2(P+r3, baseFrequency * 4.0) * baseFactor / 4.0;
	noise += pnoise2(P+r4, baseFrequency * 8.0) * baseFactor / 8.0;
	noise += pnoise2(P+r5, baseFrequency * 16.0) * baseFactor / 16.0;
	noise += pnoise2(P+r6, baseFrequency * 32.0) * baseFactor / 32.0;
	noise += pnoise2(P+r7, baseFrequency * 64.0) * baseFactor / 64.0;
	noise += pnoise2(P+r8, baseFrequency * 128.0) * baseFactor / 128.0;
	return noise;
}

float ridge(float x) {
	return exp(-8.0 * (x * x));
}

float heightTransform(float h) {
	// replaced before compiling
	$HEIGHT_FUNCTION
	return h;
}

float calculateHeight(vec2 P) {
	vec2 r1 = vec2(u_random0+u_random1, u_random1+u_random0);
	vec2 r2 = vec2(u_random2+u_random1, u_random3+u_random0);
	vec2 r3 = vec2(u_random4+u_random1, u_random5+u_random0);
	vec2 r4 = vec2(u_random6+u_random1, u_random7+u_random0);
	vec2 r5 = vec2(u_random8+u_random1, u_random9+u_random0);
	vec2 r6 = vec2(u_random0+u_random3, u_random7+u_random2);
	vec2 r7 = vec2(u_random2+u_random3, u_random5+u_random2);
	vec2 r8 = vec2(u_random4+u_random3, u_random3+u_random2);
	vec2 r9 = vec2(u_random6+u_random3, u_random1+u_random2);

	float baseHeight = u_heightMin + (u_heightMax - u_heightMin) / 2.0; 
	float base = u_heightFrequency;
	float range = u_heightMax - u_heightMin;

	float height = baseHeight;
	height += fractalNoise(P, base, range);

	#ifdef mountainsFlag
		float mountainFrequency = u_heightFrequency;
		float mountainHeight = ridge(2.0 * fractalNoise(P, 2.0, 1.0) - 1.0) * u_heightMountains;
		float mountainFactor = smoothstep(0.0, 0.6, ((pnoise2(P+r9, mountainFrequency) + 1.0) * 0.5));
		height = max(height, mountainHeight * mountainFactor);
	#endif
	
	height = heightTransform(height);
	
	return height;
}

float dummyHeight(vec2 P) {
	return sin(P.x * 100.0) * 0.5 + 0.5;
}

#ifdef planetColorsFlag
vec3 planetColor(vec2 P, float height, float distEquator) {
	float r1 = u_random0 + u_random7;
	float r2 = u_random1 + u_random7;
	float r3 = u_random2 + u_random7;
	float r4 = u_random3 + u_random7;
	float r5 = u_random4 + u_random7;
	float r6 = u_random5 + u_random7;
	float r7 = u_random6 + u_random7;
	float r8 = u_random7 + u_random7;

	vec3 color1 = u_planetColor0; 
	vec3 color2 = u_planetColor1; 
	vec3 color3 = u_planetColor2; 

	float v1 = clamp(height, u_heightMin, u_heightMax);
	float v2 = pnoise2(P+r2, 8.0) * 0.5 + 0.5;

	vec3 mix1 = mix(color1, color2, v1);
	vec3 mix2 = mix(mix1, color3, v2);
	return mix2;
}
#endif

void main() {
	vec2 r1 = vec2(u_random0+u_random5, u_random1+u_random4);
	vec2 r2 = vec2(u_random0+u_random6, u_random1+u_random3);
	vec2 r3 = vec2(u_random0+u_random7, u_random1+u_random2);
	
	#if defined(createNormalFlag)
		vec3 normal;
		float height = calculateHeight(v_texCoords0);
		if (height > u_heightWater) {
			float offset = 0.000001;
			float heightDeltaX = calculateHeight(v_texCoords0 + vec2(offset, 0.0));
			float heightDeltaY = calculateHeight(v_texCoords0 + vec2(0.0, offset));
			float deltaX = height - heightDeltaX;
			float deltaY = height - heightDeltaY;
			vec3 tangentX = vec3(offset, 0, deltaX / 100.0);
			vec3 tangentY = vec3(0, offset, deltaY / 100.0);
			normal = normalize(cross(tangentX, tangentY));
		} else {
			normal = vec3(0.0, 0.0, 1.0);
		}
		gl_FragColor.rgb = clamp((normal + 1.0) / 2.0, 0.0, 1.0);
	#elif defined(createEmissiveFlag)
		// TODO untested and probably wrong!
		float height = calculateHeight(v_texCoords0);

		vec3 color = vec3(0.0, 0.0, 0.0);
		#ifdef diffuseTextureFlag
			color = texture2D(u_diffuseTexture, vec2(clamp(height, 0.0, 1.0), distEquator)).rgb;
		#endif
		#ifdef planetColorsFlag
			color = planetColor(v_texCoords0, height, distEquator);
		#endif

		gl_FragColor.rgb = color;
	#elif defined(createSpecularFlag)
		float height = calculateHeight(v_texCoords0);

		gl_FragColor.rgba = if_then_else(when_gt(height, u_heightWater), vec4(0.2, 0.2, 0.2, 1.0), vec4(0.8, 0.8, 0.8, 1.0));
	#else
		float height = calculateHeight(v_texCoords0);

		float distEquator = abs(v_texCoords0.t - 0.5) * 2.0;
		distEquator += pnoise2(v_texCoords0,  8.0) * 0.04;
		distEquator += pnoise2(v_texCoords0, 16.0) * 0.02;
		distEquator += pnoise2(v_texCoords0, 32.0) * 0.01;
		
		float absIceLevel = abs(u_iceLevel);
		distEquator = (1.0 - absIceLevel) * distEquator;
		if (u_iceLevel >= 0.0) {
			distEquator = distEquator + absIceLevel;
		} else {
			distEquator = distEquator + absIceLevel * 0.15;
		}
	
		vec3 color = vec3(0.0, 0.0, 1.0);
		#ifdef diffuseTextureFlag
			color = texture2D(u_diffuseTexture, vec2(clamp(height, 0.0, 1.0), distEquator)).rgb;
		#endif
		#ifdef planetColorsFlag
			color = planetColor(v_texCoords0, height, distEquator);
		#endif
		#ifdef debugColorFlag
			color = vec3(height, distEquator, 1.0);
		#endif
		
		#ifdef colorNoiseFlag
			if (height > u_heightWater) {
				// make noise on land, not on water
				float colorNoise = fractalNoise(v_texCoords0+r1, u_colorFrequency, u_colorNoise);
				color = color * (1.0 + colorNoise);
//				float colorNoiseR = 1.0 + fractalNoise(v_texCoords0+r1, u_colorFrequency, u_colorNoise);
//				float colorNoiseG = 1.0 + fractalNoise(v_texCoords0+r2, u_colorFrequency, u_colorNoise);
//				float colorNoiseB = 1.0 + fractalNoise(v_texCoords0+r3, u_colorFrequency, u_colorNoise);
//				color = vec3(color.r * colorNoiseR, color.g * colorNoiseG, color.b * colorNoiseB);
			}
		#endif
				
		gl_FragColor.rgb = color;
	#endif
}


