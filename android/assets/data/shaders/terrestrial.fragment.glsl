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

uniform int u_fractalOctaveCount;

uniform float u_heightMin;
uniform float u_heightMax;
uniform float u_heightFrequency;

#ifdef mountainsFlag
uniform float u_heightMountains;
#endif

#ifdef cratersFlag
uniform float u_craterBaseGrid;
uniform float u_craterProbability;
#endif

uniform float u_iceLevel;
uniform float u_heightWater;
uniform float u_heightFunctionValue;

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

#define M_PI 3.1415926535897932384626433832795

//
//  Wombat
//  An efficient texture-free GLSL procedural noise library
//  Source: https://github.com/BrianSharpe/Wombat
//  Derived from: https://github.com/BrianSharpe/GPU-Noise-Lib
//
//  I'm not one for copyrights.  Use the code however you wish.
//  All I ask is that credit be given back to the blog or myself when appropriate.
//  And also to let me know if you come up with any changes, improvements, thoughts or interesting uses for this stuff. :)
//  Thanks!
//
//  Brian Sharpe
//  brisharpe CIRCLE_A yahoo DOT com
//  http://briansharpe.wordpress.com
//  https://github.com/BrianSharpe
//

//
//  This is a modified version of Stefan Gustavson's and Ian McEwan's work at http://github.com/ashima/webgl-noise
//  Modifications are...
//  - faster random number generation
//  - analytical final normalization
//  - space scaled can have an approx feature size of 1.0
//

//
//  Simplex Perlin Noise 2D
//  Return value range of -1.0->1.0
//
float SimplexPerlin2D( vec2 P )
{
    //  https://github.com/BrianSharpe/Wombat/blob/master/SimplexPerlin2D.glsl

    //  simplex math constants
    const float SKEWFACTOR = 0.36602540378443864676372317075294;            // 0.5*(sqrt(3.0)-1.0)
    const float UNSKEWFACTOR = 0.21132486540518711774542560974902;          // (3.0-sqrt(3.0))/6.0
    const float SIMPLEX_TRI_HEIGHT = 0.70710678118654752440084436210485;    // sqrt( 0.5 )	height of simplex triangle
    const vec3 SIMPLEX_POINTS = vec3( 1.0-UNSKEWFACTOR, -UNSKEWFACTOR, 1.0-2.0*UNSKEWFACTOR );  //  simplex triangle geo

    //  establish our grid cell.
    P *= SIMPLEX_TRI_HEIGHT;    // scale space so we can have an approx feature size of 1.0
    vec2 Pi = floor( P + dot( P, vec2( SKEWFACTOR ) ) );

    // calculate the hash
    vec4 Pt = vec4( Pi.xy, Pi.xy + 1.0 );
    Pt = Pt - floor(Pt * ( 1.0 / 71.0 )) * 71.0;
    Pt += vec2( 26.0, 161.0 ).xyxy;
    Pt *= Pt;
    Pt = Pt.xzxz * Pt.yyww;
    vec4 hash_x = fract( Pt * ( 1.0 / 951.135664 ) );
    vec4 hash_y = fract( Pt * ( 1.0 / 642.949883 ) );

    //  establish vectors to the 3 corners of our simplex triangle
    vec2 v0 = Pi - dot( Pi, vec2( UNSKEWFACTOR ) ) - P;
    vec4 v1pos_v1hash = (v0.x < v0.y) ? vec4(SIMPLEX_POINTS.xy, hash_x.y, hash_y.y) : vec4(SIMPLEX_POINTS.yx, hash_x.z, hash_y.z);
    vec4 v12 = vec4( v1pos_v1hash.xy, SIMPLEX_POINTS.zz ) + v0.xyxy;

    //  calculate the dotproduct of our 3 corner vectors with 3 random normalized vectors
    vec3 grad_x = vec3( hash_x.x, v1pos_v1hash.z, hash_x.w ) - 0.49999;
    vec3 grad_y = vec3( hash_y.x, v1pos_v1hash.w, hash_y.w ) - 0.49999;
    vec3 grad_results = inversesqrt( grad_x * grad_x + grad_y * grad_y ) * ( grad_x * vec3( v0.x, v12.xz ) + grad_y * vec3( v0.y, v12.yw ) );

    //	Normalization factor to scale the final result to a strict 1.0->-1.0 range
    //	http://briansharpe.wordpress.com/2012/01/13/simplex-noise/#comment-36
    const float FINAL_NORMALIZATION = 99.204334582718712976990005025589;

    //	evaluate and return
    vec3 m = vec3( v0.x, v12.xz ) * vec3( v0.x, v12.xz ) + vec3( v0.y, v12.yw ) * vec3( v0.y, v12.yw );
    m = max(0.5 - m, 0.0);
    m = m*m;
    return dot(m*m, grad_results) * FINAL_NORMALIZATION;
}

float pnoise2(vec2 P, float period) {
	vec2 pos = P;
	float noise = SimplexPerlin2D(pos * period);
	if (v_texCoords0.s > 0.95) {
		float noise2 = SimplexPerlin2D((pos - vec2(1.0, 0.0)) * period);
		noise = mix(noise, noise2, smoothstep(0.95, 1.0, v_texCoords0.s));
	}
	return noise;
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

float rand(vec2 x){
    return fract(sin(dot(x.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float smoothAbs(float x, float smoothness) {
	return sqrt(x * x + smoothness);
}

#ifdef fractalFunctionSimpleWeightFlag
float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float frequency = baseFrequency;
	float weight = baseFactor;
	float noise = 0.0;
	vec2 r = P;
	for(int i=0; i<u_fractalOctaveCount; i++) {
		r += vec2(u_random0, u_random1);
		float signal = pnoise2(r, frequency);
		noise += signal * weight;
		weight *= 0.5;
		frequency *= 2.0;
	}

	return noise * 0.5 + 0.5;
}
#endif

#ifdef fractalFunctionSimpleWeightRidgedFlag
float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float frequency = baseFrequency;
	float weight = baseFactor;
	float noise = 0.0;
	vec2 r = P;
	for(int i=0; i<u_fractalOctaveCount; i++) {
		r += vec2(u_random0, u_random1);
		float signal = 1.0 - abs(pnoise2(r, frequency));
		noise += signal * weight;
		weight *= 0.5;
		frequency *= 2.0;
	}

	return noise * 0.5;
}
#endif

#ifdef fractalFunctionSignalDependentWeightFlag
float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float frequency = baseFrequency;
	float weight = baseFactor;
	float noise = 0.0;
	vec2 r = P;
	for(int i=0; i<u_fractalOctaveCount; i++) {
		r += vec2(u_random0, u_random1);
		float signal = pnoise2(r, frequency) * 0.5 + 0.5;
		noise += signal * weight;
		weight *= signal;
		frequency *= 2.0;
	}

	return noise * 0.5;
}
#endif

#ifdef fractalFunctionSignalDependentWeightRidgedFlag
float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float frequency = baseFrequency;
	vec2 r = P + vec2(u_random2, u_random3);
	float terrainType = smoothstep(0.0, 1.0, pnoise2(r, frequency) * 0.5 + 0.5);
	float signalFactorBase = u_random4 * 0.20  + 0.50;
	float signalFactorVariation = u_random5 * 0.10 + 0.20;
	float signalFactor = terrainType * signalFactorVariation + signalFactorBase;
	float lacunarity = u_random6 * 0.4 + 1.8;

	signalFactor = 0.8;
	lacunarity = 2.0;

	float weight = baseFactor;
	float noise = 0.0;
	for(int i=0; i<u_fractalOctaveCount; i++) {
		r += vec2(u_random0, u_random1);

		float signal = pnoise2(r, frequency);
		signal = 1.0 - smoothAbs(signal, u_random9 / frequency);
		signal *= signalFactor;
		
		noise += signal * weight;
		weight *= signal;
		frequency *= lacunarity;
	}

	return noise * 0.5;
}
#endif


#ifdef fractalFunctionRandomizedFlag
float fractalNoise(vec2 P, float baseFrequency, float baseFactor) {
	float frequency = baseFrequency;
	vec2 r = P + vec2(u_random2, u_random3);
	float terrainType1 = smoothstep(0.333, 0.666, pnoise2(r, frequency) * 0.5 + 0.5);
	float signalFactorBase = u_random4 * 0.20  + 0.20;
	float signalFactorVariation = u_random5 * 0.30 + 0.20;
	float signalFactor = terrainType1 * signalFactorVariation + signalFactorBase;
	float lacunarity = u_random6 * 0.4 + 1.8;

	signalFactor = 0.8;
	lacunarity = 2.0;

	float weight = baseFactor;
	float noise = 0.0;
	for(int i=0; i<u_fractalOctaveCount; i++) {
		r += vec2(u_random0, u_random1);

		float signal = pnoise2(r, frequency);
		//signal = signal * 0.5 + 0.5; // linear
		//signal = 1.0 - abs(signal); // ridged edge
		//signal = 1.0 - smoothAbs(signal, u_random9 / frequency); // ridged smoothed edge
		float signal1 = signal * 0.5 + 0.5; // linear
		float signal2 = 1.0 - smoothAbs(signal, u_random9 / frequency); // ridged smoothed edge
		signal = mix(signal1, signal2, terrainType1);
		signal *= signalFactor;

		noise += signal * weight;
		weight *= signal;
		frequency *= lacunarity;
	}

	return noise * 0.5;
}
#endif

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

float transform(float fromMin, float fromMax, float toMin, float toMax, float value) {
	return (value - fromMin) / (fromMax - fromMin) * (toMax - toMin) + toMin;
}


#ifdef cratersFlag
float roundInnerRim(float x) {
    return x * x * 4.0 - 3.0;
}

float flatInnerRim(float x, float edgeX) {
	float roundX = x - edgeX;
    return step(edgeX, x) * roundX * roundX * 3.0 - 1.0;
}

float outerRim(float x) {
    float v = 1.0 + x;
    v = 1.0 / v / v;
    return v;
}

float craterComplexSteps(float distance, vec2 craterPos, float craterAngle, vec2 pos, float craterNoise, vec2 random) {
	float centralRadius = 0.05 + rand(craterPos + random  + u_random3 + u_random0) * 0.15;
	float rimRadius = 0.7 + rand(craterPos + random  + u_random3 + u_random1) * 0.2;
	float stepSmooth = 0.02 + rand(craterPos + random  + u_random3 + u_random2) * 0.05;
	float stepDelta = 0.06 + rand(craterPos + random  + u_random3 + u_random3) * 0.02;
	float stepCount = 4.0;
	float step = rimRadius - stepDelta * stepCount;
	float stepNoiseFactor = 0.05;
	float craterDepth = 0.8;
	float heightNoise = 0.07;
	
	float centralToFlat = mix(
		0.1 + craterNoise * 0.6,
		0.0,
		smoothstep(0.0, centralRadius, distance));
	float noise1 = pnoise2(craterPos + craterAngle + random + u_random1, 2.0 * M_PI * 0.3) * stepNoiseFactor;
	float step1 = mix(
		centralToFlat,
		0.2 + craterNoise * heightNoise,
		smoothstep(step, step + stepSmooth, distance + noise1));
	step += stepDelta;
	float noise2 = pnoise2(craterPos + craterAngle + random + u_random2, 2.0 * M_PI * 0.3) * stepNoiseFactor;
	float step2 = mix(
		step1,
		0.4 + craterNoise * heightNoise,
		smoothstep(step, step + stepSmooth, distance + noise2));
	step += stepDelta;
	float noise3 = pnoise2(craterPos + craterAngle + random + u_random3, 2.0 * M_PI * 0.3) * stepNoiseFactor;
	float step3 = mix(
		step2,
		0.6 + craterNoise * heightNoise,
		smoothstep(step, step + stepSmooth, distance + noise3));
	float noise4 = pnoise2(craterPos + craterAngle + random + u_random3, 2.0 * M_PI * 0.3) * stepNoiseFactor;
	float step4 = mix(
		step3,
		0.8 + craterNoise * heightNoise,
		smoothstep(step, step + stepSmooth, distance + noise4));
	step += stepDelta;
	float noise5 = pnoise2(craterPos + craterAngle + random + u_random4, 2.0 * M_PI * 0.3) * stepNoiseFactor;
	float step5 = mix(
		step4,
		1.0 + craterNoise * heightNoise * 0.5,
		smoothstep(step, step + stepSmooth, distance + noise5));
	step += stepDelta;
	float fadeOut = mix(
		step5 - craterDepth, 
		0.0,
		smoothstep(rimRadius, 1.0, distance));
	return fadeOut;
}

float craterComplexFlat(float distance, vec2 craterPos, float craterNoise, vec2 random) {
	float centralRadius = rand(craterPos + random  + u_random3 + u_random0) * 0.2 + 0.1; 
	float flatRadius = 0.2; 
    float rimRadius = 0.8;
	float craterDepth = 0.6;

    float centralToFlat = mix(
		0.1 + craterNoise * 0.6,
    	flatInnerRim(distance / rimRadius, flatRadius),
    	smoothstep(0.0, centralRadius, distance));
    float innerOuter = mix(
		centralToFlat,
		1.0 + craterNoise * 0.2,
		smoothstep(rimRadius * 0.95, rimRadius * 1.05, distance));
	float fadeOut = mix(
		innerOuter - craterDepth, 
		0.0,
		smoothstep(rimRadius, 1.0, distance));
	return fadeOut; 
}

float craterSimpleFlat(float distance, vec2 craterPos, float craterNoise, vec2 random) {
	float flatRadius = rand(craterPos + random  + u_random3 + u_random0) * 0.2 + 0.4; 
    float rimRadius = 0.7;
    float innerOuter = mix(
    	0.0,
		1.0 + craterNoise * 0.5,
		smoothstep(flatRadius, rimRadius * 1.1, distance));
	float fadeOut = mix(
		innerOuter, 
		0.0,
		smoothstep(rimRadius, 1.0, distance));
	return fadeOut; 
}

float craterSimpleRound(float distance, vec2 craterPos, float craterNoise, vec2 random) {
    float rimRadius = 0.7;
    float innerOuter = mix(
		roundInnerRim(distance / rimRadius),
		1.0 + craterNoise * 0.5,
		smoothstep(rimRadius * 0.95, rimRadius * 1.05, distance));
	float fadeOut = mix(
		innerOuter, 
		0.0,
		smoothstep(rimRadius, 1.0, distance));
	return fadeOut; 
}

void calculateCrater(vec2 pos, float grid, vec2 random1, vec2 random2, 
		out vec2 craterPos, out float craterDistance, out float craterRadius, out float craterHeight, out float craterAngleSin) {
    vec2 bigPos = pos * grid;
    vec2 floorBigPos = floor(bigPos);

   	vec2 fractPos = bigPos - floorBigPos;
   	float minRadius = 0.1;
   	float maxRadius = 0.5;
    float radius = rand(floorBigPos + random1 + random2) * (maxRadius - minRadius) + minRadius;
    float randomDeltaX = (rand(floorBigPos + random1) - 0.5) * (1.0 - radius);
    float randomDeltaY = (rand(floorBigPos + random2) - 0.5) * (1.0 - radius);
	vec2 randomDeltaPos = vec2(randomDeltaX, randomDeltaY);
    fractPos += randomDeltaPos;
    
	float distance = length(fractPos - 0.5);
	float relativeDistance = distance / radius * 2.0;
	float relativeHeight = rand(floorBigPos + random2);
	
	craterPos = floorBigPos;
	craterRadius = radius;
	craterDistance = relativeDistance;
	craterHeight = relativeHeight;
	craterAngleSin = (fractPos.y - 0.5) / distance;
}

float addCraterComplexSteps(float height, float craterBaseHeight, vec2 pos, float grid, float craterNoise, vec2 random1, vec2 random2) {
	vec2 craterPos;
	float craterDistance;
	float craterRadius;
	float craterHeight;
	float craterAngleSin;
	calculateCrater(pos, grid, random1, random2, 
		craterPos, craterDistance, craterRadius, craterHeight, craterAngleSin);
	if (rand(craterPos) >= u_craterProbability) {
		return height;
	}
	float crater = craterComplexSteps(craterDistance, craterPos, asin(craterAngleSin), pos, craterNoise, random1);
	return height + crater * craterBaseHeight * craterRadius * (0.6 + craterHeight * 0.4);
}

float addCraterComplexFlat(float height, float craterBaseHeight, vec2 pos, float grid, float craterNoise, vec2 random1, vec2 random2) {
	vec2 craterPos;
	float craterDistance;
	float craterRadius;
	float craterHeight;
	float craterAngleSin;
	calculateCrater(pos, grid, random1, random2, 
		craterPos, craterDistance, craterRadius, craterHeight, craterAngleSin);
	if (rand(craterPos) >= u_craterProbability) {
		return height;
	}
	float crater = craterComplexFlat(craterDistance, craterPos, craterNoise, random1);
	return height + crater * craterBaseHeight * craterRadius * (0.3 + craterHeight * 0.4);
}

float addCraterSimpleFlat(float height, float craterBaseHeight, vec2 pos, float grid, float craterNoise, vec2 random1, vec2 random2) {
	vec2 craterPos;
	float craterDistance;
	float craterRadius;
	float craterHeight;
	float craterAngleSin;
	calculateCrater(pos, grid, random1, random2, 
		craterPos, craterDistance, craterRadius, craterHeight, craterAngleSin);
	if (rand(craterPos) >= u_craterProbability) {
		return height;
	}
	float crater = craterSimpleFlat(craterDistance, craterPos, craterNoise, random1);
	return height + crater * craterBaseHeight * craterRadius * (0.3 + craterHeight * 0.4);
}

float addCraterSimpleRound(float height, float craterBaseHeight, vec2 pos, float grid, float craterNoise, vec2 random1, vec2 random2) {
	vec2 craterPos;
	float craterDistance;
	float craterRadius;
	float craterHeight;
	float craterAngleSin;
	calculateCrater(pos, grid, random1, random2, 
		craterPos, craterDistance, craterRadius, craterHeight, craterAngleSin);
	if (rand(craterPos) >= u_craterProbability) {
		return height;
	}
	float crater = craterSimpleRound(craterDistance, craterPos, craterNoise, random1);
	return height + crater * craterBaseHeight * craterRadius * (0.3 + craterHeight * 0.4);
}

#endif // cratersFlag

float calculateHeight(vec2 P) {
	float base = u_heightFrequency;
	float range = u_heightMax - u_heightMin;

	float height = u_heightMin;
	height += fractalNoise(P, base, range);

	#ifdef mountainsFlag
		float mountainFrequency = u_heightFrequency;
		float mountainHeight = ridge(2.0 * fractalNoise(P, 2.0, 1.0) - 1.0) * u_heightMountains;
		float mountainFactor = smoothstep(0.0, 0.6, ((pnoise2(P+vec2(u_random3+u_random4, u_random8+u_random5), mountainFrequency) + 1.0) * 0.5));
		height = max(height, mountainHeight * mountainFactor);
	#endif
	
	height = heightTransform(height);
	height = max(height + 0.05, 0.05);

	#ifdef cratersFlag
		float craterNoise = fractalNoiseCheap(P + u_random4, 512.0, 4.0) * 0.5 + 0.5;
		float craterHeightRange = range * 10.0 / u_craterBaseGrid;
		height = addCraterComplexSteps(height, craterHeightRange / 1.0, P, u_craterBaseGrid * 1.0, craterNoise, vec2(u_random9+u_random0, u_random8+u_random0), vec2(u_random8+u_random9, u_random7+u_random9));
		height = addCraterComplexSteps(height, craterHeightRange / 2.0, P, u_craterBaseGrid * 2.0, craterNoise, vec2(u_random9+u_random1, u_random8+u_random1), vec2(u_random8+u_random8, u_random7+u_random8));
		height = addCraterComplexFlat(height, craterHeightRange / 3.0, P, u_craterBaseGrid * 3.0, craterNoise, vec2(u_random9+u_random1, u_random8+u_random1), vec2(u_random8+u_random8, u_random7+u_random8));
		height = addCraterComplexFlat(height, craterHeightRange / 4.0, P, u_craterBaseGrid * 4.0, craterNoise, vec2(u_random9+u_random2, u_random8+u_random2), vec2(u_random8+u_random7, u_random7+u_random7));
		height = addCraterComplexFlat(height, craterHeightRange / 5.0, P, u_craterBaseGrid * 5.0, craterNoise, vec2(u_random9+u_random1, u_random8+u_random1), vec2(u_random8+u_random8, u_random7+u_random8));
		height = addCraterSimpleFlat(height, craterHeightRange / 6.0, P, u_craterBaseGrid * 6.0, craterNoise, vec2(u_random9+u_random3, u_random8+u_random3), vec2(u_random8+u_random6, u_random7+u_random6));
		height = addCraterSimpleFlat(height, craterHeightRange / 8.0, P, u_craterBaseGrid * 8.0, craterNoise, vec2(u_random9+u_random4, u_random8+u_random4), vec2(u_random8+u_random5, u_random7+u_random5));
		height = addCraterSimpleFlat(height, craterHeightRange / 14.0, P, u_craterBaseGrid * 14.0, craterNoise, vec2(u_random9+u_random5, u_random8+u_random5), vec2(u_random8+u_random4, u_random7+u_random4));
		height = addCraterSimpleRound(height, craterHeightRange / 20.0, P, u_craterBaseGrid * 20.0, craterNoise, vec2(u_random9+u_random6, u_random8+u_random6), vec2(u_random8+u_random3, u_random7+u_random3));
		height = addCraterSimpleRound(height, craterHeightRange / 40.0, P, u_craterBaseGrid * 40.0, craterNoise, vec2(u_random9+u_random7, u_random8+u_random7), vec2(u_random8+u_random2, u_random7+u_random2));
		height = addCraterSimpleRound(height, craterHeightRange / 80.0, P, u_craterBaseGrid * 80.0, craterNoise, vec2(u_random9+u_random8, u_random8+u_random8), vec2(u_random8+u_random1, u_random7+u_random1));
		height = addCraterSimpleRound(height, craterHeightRange / 160.0, P, u_craterBaseGrid * 160.0, craterNoise, vec2(u_random9+u_random9, u_random8+u_random9), vec2(u_random8+u_random0, u_random7+u_random0));
		height = addCraterSimpleRound(height, craterHeightRange / 320.0, P, u_craterBaseGrid * 320.0, craterNoise, vec2(u_random0+u_random1, u_random9+u_random0), vec2(u_random7+u_random9, u_random6+u_random9));
	#endif
	
	return height;
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
		bumpColor = encode_rgb888((height - u_heightWater) * 0.01);
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


