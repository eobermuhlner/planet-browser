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

//layout(location = 0)out vec4 output1; 
//layout(location = 1)out vec4 output2; 

uniform float u_normalStep;

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

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef planetColorsFlag
uniform vec4 u_planetColor0;
uniform vec4 u_planetColor1;
uniform vec4 u_planetColor2;
uniform vec4 u_planetColor3;
uniform vec4 u_planetColor4;
uniform vec4 u_planetColor5;

uniform float u_planetColorFrequency0;
uniform float u_planetColorFrequency1;
uniform float u_planetColorFrequency2;
uniform float u_planetColorFrequency3;
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

//based in gamedev.net topic 442138
vec3 encode_rgb888(float value) {
	vec3 bitShift = vec3(256.0*256.0, 256.0, 1.0);
	vec3 bitMask = vec3(0.0, 1.0/256.0, 1.0/256.0);
	vec3 comp = fract(clamp(value, 0.0, 1.0) * bitShift);
	comp -= comp.xxy * bitMask;
	return comp;
}

/*
float decode_rgb888(vec3 vec) {
	vec3 bitShift = vec3(1.0/(256.0*256.0), 1.0/256.0, 1.0);
	return dot(vec, bitShift);
}

vec4 encode_rgba8888(float value) {
	vec4 bitShift = vec4(256.0*256.0*256.0, 256.0*256.0, 256.0, 1.0);
	vec4 bitMask = vec4(0.0, 1.0/256.0, 1.0/256.0, 1.0/256.0);
	vec4 comp = fract(clamp(value, 0.0, 1.0) * bitShift);
	comp -= comp.xxyz * bitMask;
	return comp;
}

float decode_rgba8888(vec4 vec) {
	vec4 bitShift = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
	return dot(vec, bitShift);
}
*/

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

float if_then_else(float condition, float trueValue, float falseValue) {
	float result = trueValue * condition;
	result += falseValue * when_not(condition);
	return result;
}

vec3 if_then_else(float condition, vec3 trueValue, vec3 falseValue) {
	vec3 result = trueValue * condition;
	result += falseValue * when_not(condition);
	return result;
}

vec4 if_then_else(float condition, vec4 trueValue, vec4 falseValue) {
	vec4 result = trueValue * condition;
	result += falseValue * when_not(condition);
	return result;
}

float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float noise = 0.0;
	noise += pnoise2(P+vec2(u_random0+u_random4, u_random1+u_random6), baseFrequency * 1.0) * baseFactor / 1.0;
	noise += pnoise2(P+vec2(u_random2+u_random4, u_random3+u_random6), baseFrequency * 2.0) * baseFactor / 2.0;
	noise += pnoise2(P+vec2(u_random4+u_random4, u_random5+u_random6), baseFrequency * 4.0) * baseFactor / 4.0;
	noise += pnoise2(P+vec2(u_random6+u_random4, u_random7+u_random6), baseFrequency * 8.0) * baseFactor / 8.0;
	noise += pnoise2(P+vec2(u_random8+u_random4, u_random9+u_random6), baseFrequency * 16.0) * baseFactor / 16.0;
	noise += pnoise2(P+vec2(u_random0+u_random5, u_random7+u_random7), baseFrequency * 32.0) * baseFactor / 32.0;
	noise += pnoise2(P+vec2(u_random2+u_random5, u_random5+u_random7), baseFrequency * 64.0) * baseFactor / 64.0;
	noise += pnoise2(P+vec2(u_random4+u_random5, u_random3+u_random7), baseFrequency * 128.0) * baseFactor / 128.0;

	noise += pnoise2(P+vec2(u_random6+u_random5, u_random1+u_random7), baseFrequency * 256.0) * baseFactor / 256.0;
	noise += pnoise2(P+vec2(u_random8+u_random5, u_random3+u_random7), baseFrequency * 512.0) * baseFactor / 512.0;
	noise += pnoise2(P+vec2(u_random0+u_random5, u_random7+u_random8), baseFrequency * 1024.0) * baseFactor / 1024.0;
	return noise;
}

float fractalNoiseCheap(vec2 P, float baseFrequency, float baseFactor) {
	float noise = 0.0;
	noise += pnoise2(P+vec2(u_random0+u_random4, u_random1+u_random6), baseFrequency * 1.0) * baseFactor / 1.0;
	noise += pnoise2(P+vec2(u_random2+u_random4, u_random3+u_random6), baseFrequency * 2.0) * baseFactor / 2.0;
	noise += pnoise2(P+vec2(u_random4+u_random4, u_random5+u_random6), baseFrequency * 4.0) * baseFactor / 4.0;
	noise += pnoise2(P+vec2(u_random6+u_random4, u_random7+u_random6), baseFrequency * 8.0) * baseFactor / 8.0;
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

	float baseHeight = u_heightMin + (u_heightMax - u_heightMin) / 2.0; 
	float base = u_heightFrequency;
	float range = u_heightMax - u_heightMin;

	float height = baseHeight;
	height += fractalNoise(P, base, range);

	#ifdef mountainsFlag
		float mountainFrequency = u_heightFrequency;
		float mountainHeight = ridge(2.0 * fractalNoise(P, 2.0, 1.0) - 1.0) * u_heightMountains;
		float mountainFactor = smoothstep(0.0, 0.6, ((pnoise2(P+r1, mountainFrequency) + 1.0) * 0.5));
		height = max(height, mountainHeight * mountainFactor);
	#endif
	
	height = heightTransform(height);
	
	return max(u_heightWater, height);
}

float dummyHeight(vec2 P) {
	return sin(P.x * 100.0) * 0.5 + 0.5;
}

#ifdef planetColorsFlag
vec4 planetColor(vec2 P, float height, float distEquator) {
	float h = (clamp(height, u_heightMin, u_heightMax) - u_heightMin) * (u_heightMax - u_heightMin);
	float v1 = fractalNoiseCheap(P+vec2(u_random0 + u_random7), u_planetColorFrequency0, 1.0) * 0.5 + 0.5;
	float v2 = fractalNoiseCheap(P+vec2(u_random0 + u_random6), u_planetColorFrequency1, 1.0) * 0.5 + 0.5;
	float v3 = fractalNoiseCheap(P+vec2(u_random0 + u_random5), u_planetColorFrequency2, 1.0) * 0.5 + 0.5;
	float v4 = fractalNoiseCheap(P+vec2(u_random0 + u_random3), u_planetColorFrequency3, 1.0) * 0.5 + 0.5;

	vec4 color1 = mix(u_planetColor0, u_planetColor1, v1);
	color1 = mix(color1, u_planetColor2, v2);
	vec4 color2 = mix(u_planetColor3, u_planetColor4, v3);
	color2 = mix(color2, u_planetColor5, v4);
	vec4 color = mix(color1, color2, h);
	return color;
}
#endif


void main() {
	vec2 r1 = vec2(u_random0+u_random5, u_random1+u_random4);
	vec2 r2 = vec2(u_random0+u_random6, u_random1+u_random3);
	vec2 r3 = vec2(u_random0+u_random7, u_random1+u_random2);

	vec3 bumpColor = vec3(0.0, 0.0, 0.0);
	vec3 diffuseColor = vec3(0.0, 0.0, 0.0);
	vec3 normalColor = vec3(0.5, 0.5, 1.0);
	vec3 specularColor = vec3(0.0, 0.0, 0.0);
	vec3 emissiveColor = vec3(0.0, 0.0, 0.0);
	
	float height = calculateHeight(v_texCoords0);
	float distEquator = abs(v_texCoords0.t - 0.5) * 2.0;

	#if defined(createBumpFlag) || defined(createDiffuseFlag) || defined(createSpecularFlag)
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

		#ifdef planetColorsFlag
			vec4 diffuseColorAndSpecularValue = planetColor(v_texCoords0, height, distEquator);
		#endif
	#endif

	#if defined(createBumpFlag)
		//bumpColor = vec3((height - u_heightWater) / 3.0);
		bumpColor = encode_rgb888((height - u_heightWater) * 0.1);
	#endif

	#if defined(createDiffuseFlag)
		diffuseColor = vec3(1.0, 0.0, 0.0);
		#ifdef diffuseTextureFlag
			diffuseColor = texture2D(u_diffuseTexture, vec2(clamp(height, 0.0, 1.0), distEquator)).rgb;
		#endif
		#ifdef planetColorsFlag
			diffuseColor = diffuseColorAndSpecularValue.rgb;
		#endif
		#ifdef debugColorFlag
			diffuseColor = vec3(height, distEquator, 1.0);
		#endif
		
		#ifdef colorNoiseFlag
			if (height > u_heightWater) {
				// make noise on land, not on water
				float colorNoise = fractalNoiseCheap(v_texCoords0+r1, u_colorFrequency, u_colorNoise);
				diffuseColor = diffuseColor * (1.0 + colorNoise);
//				float colorNoiseR = 1.0 + fractalNoiseCheap(v_texCoords0+r1, u_colorFrequency, u_colorNoise);
//				float colorNoiseG = 1.0 + fractalNoiseCheap(v_texCoords0+r2, u_colorFrequency, u_colorNoise);
//				float colorNoiseB = 1.0 + fractalNoiseCheap(v_texCoords0+r3, u_colorFrequency, u_colorNoise);
//				diffuseColor = vec3(diffuseColor.r * colorNoiseR, diffuseColor.g * colorNoiseG, diffuseColor.b * colorNoiseB);
			}
		#endif
	#endif
	
	#if defined(createNormalFlag)
		vec3 normal = vec3(0.0, 0.0, 1.0);
		if (height > u_heightWater) {
			float offset = u_normalStep;
			float heightDeltaX = calculateHeight(v_texCoords0 + vec2(offset, 0.0));
			float heightDeltaY = calculateHeight(v_texCoords0 + vec2(0.0, offset));
			float deltaX = height - heightDeltaX;
			float deltaY = height - heightDeltaY;
			vec3 tangentX = vec3(offset, 0, deltaX / 100.0);
			vec3 tangentY = vec3(0, offset, deltaY / 100.0);
			normal = normalize(cross(tangentX, tangentY));
		}
		normalColor = clamp((normal + 1.0) / 2.0, 0.0, 1.0);
	#endif
	
	#if defined(createSpecularFlag)
		#ifdef specularTextureFlag
			specularColor = texture2D(u_specularTexture, vec2(clamp(height, 0.0, 1.0), distEquator)).rgb;
		#endif
		#ifdef planetColorsFlag
			specularColor = vec3(1.0, 1.0, 1.0) * diffuseColorAndSpecularValue.a;
		#endif
	#endif
	
	#if defined(createEmissiveFlag)
		#ifdef diffuseTextureFlag
			emissiveColor = texture2D(u_diffuseTexture, vec2(clamp(height, 0.0, 1.0), distEquator)).rgb;
		#endif
		#ifdef planetColorsFlag
			emissiveColor = planetColor(v_texCoords0, height, distEquator).rgb;
		#endif
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


