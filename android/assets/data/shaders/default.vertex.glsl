
//varying vec3 v_debug_vec3;

#if defined(diffuseTextureFlag) || defined(emissiveTextureFlag) || defined(specularTextureFlag)
#define textureFlag
#endif

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#if defined(specularFlag) || defined(fogFlag) || defined(normalTextureFlag) || defined (emissiveColorFlag) || defined (atmosphereFlag)
#define cameraPositionFlag
#endif

#if defined(atmosphereFlag)
#define normalFlag
#define blendedFlag
#endif

attribute vec3 a_position;
uniform mat4 u_projViewTrans;

#if defined(colorFlag)
varying vec4 v_color;
attribute vec4 a_color;
#endif // colorFlag

#ifdef normalFlag
attribute vec3 a_normal;
attribute vec3 a_tangent;
uniform mat3 u_normalMatrix;
#if !defined(normalTextureFlag)
varying vec3 v_normal;
#endif
#endif // normalFlag

#ifdef textureFlag
attribute vec2 a_texCoord0;
varying vec2 v_texCoords0;
#endif // textureFlag

#ifdef normalTextureFlag
attribute vec2 a_texCoord1;
varying vec2 v_texCoords1;
varying vec3 v_lightVecTangent;
#endif // normalTextureFlag

#ifdef boneWeight0Flag
#define boneWeightsFlag
attribute vec2 a_boneWeight0;
#endif //boneWeight0Flag

#ifdef boneWeight1Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight1;
#endif //boneWeight1Flag

#ifdef boneWeight2Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight2;
#endif //boneWeight2Flag

#ifdef boneWeight3Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight3;
#endif //boneWeight3Flag

#ifdef boneWeight4Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight4;
#endif //boneWeight4Flag

#ifdef boneWeight5Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight5;
#endif //boneWeight5Flag

#ifdef boneWeight6Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight6;
#endif //boneWeight6Flag

#ifdef boneWeight7Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight7;
#endif //boneWeight7Flag

#if defined(numBones) && defined(boneWeightsFlag)
#if (numBones > 0) 
#define skinningFlag
#endif
#endif

uniform mat4 u_worldTrans;

#if defined(numBones)
#if numBones > 0
uniform mat4 u_bones[numBones];
#endif //numBones
#endif

#ifdef shininessFlag
uniform float u_shininess;
#else
const float u_shininess = 20.0;
#endif // shininessFlag

#ifdef blendedFlag
uniform float u_opacity;
varying float v_opacity;

#ifdef alphaTestFlag
uniform float u_alphaTest;
varying float v_alphaTest;
#endif //alphaTestFlag
#endif // blendedFlag

#ifdef lightingFlag
varying vec3 v_lightDiffuse;

#ifdef ambientLightFlag
uniform vec3 u_ambientLight;
#endif // ambientLightFlag

#ifdef ambientCubemapFlag
uniform vec3 u_ambientCubemap[6];
#endif // ambientCubemapFlag 

#ifdef sphericalHarmonicsFlag
uniform vec3 u_sphericalHarmonics[9];
#endif //sphericalHarmonicsFlag

#ifdef specularFlag
varying vec3 v_lightSpecular;
#endif // specularFlag

#ifdef emissiveColorFlag
uniform vec4 u_emissiveColor;
varying vec4 v_emissiveColor;
#endif

#ifdef cameraPositionFlag
uniform vec4 u_cameraPosition;
#endif // cameraPositionFlag

#ifdef fogFlag
varying float v_fog;
#endif // fogFlag

#ifdef atmosphereFlag
varying float v_lambertFactorNormalToCamera;
#endif

#if defined(numDirectionalLights) && (numDirectionalLights > 0)
struct DirectionalLight
{
	vec3 color;
	vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
#endif // numDirectionalLights

#if defined(numPointLights) && (numPointLights > 0)
struct PointLight
{
	vec3 color;
	vec3 position;
};
uniform PointLight u_pointLights[numPointLights];
#endif // numPointLights

#if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
#define ambientFlag
#endif //ambientFlag

#ifdef shadowMapFlag
uniform mat4 u_shadowMapProjViewTrans;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag
#endif //shadowMapFlag

#if defined(ambientFlag) && defined(separateAmbientFlag)
varying vec3 v_ambientLight;
#endif //separateAmbientFlag

#endif // lightingFlag

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

vec3 if_then_else(float condition, vec3 trueValue, vec3 falseValue) {
	vec3 result = trueValue * condition;
	result += falseValue * when_not(condition);
	return result;
}

// https://en.wikipedia.org/wiki/Relative_luminance
float luminance(vec3 color) {
	return (0.2126*color.r + 0.7152*color.g + 0.0722*color.b);
}

void main() {
	#ifdef textureFlag
		v_texCoords0 = a_texCoord0;
	#endif // textureFlag

	#ifdef normalTextureFlag
		v_texCoords1 = a_texCoord0;
	#endif // textureFlag
	
	#if defined(colorFlag)
		v_color = a_color;
	#endif // colorFlag
	
	#ifdef emissiveColorFlag
		v_emissiveColor = u_emissiveColor;
	#endif
		
	#ifdef blendedFlag
		v_opacity = u_opacity;
		#ifdef alphaTestFlag
			v_alphaTest = u_alphaTest;
		#endif //alphaTestFlag
	#endif // blendedFlag
	
	#ifdef skinningFlag
		mat4 skinning = mat4(0.0);
		#ifdef boneWeight0Flag
			skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
		#endif //boneWeight0Flag
		#ifdef boneWeight1Flag				
			skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
		#endif //boneWeight1Flag
		#ifdef boneWeight2Flag		
			skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
		#endif //boneWeight2Flag
		#ifdef boneWeight3Flag
			skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
		#endif //boneWeight3Flag
		#ifdef boneWeight4Flag
			skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
		#endif //boneWeight4Flag
		#ifdef boneWeight5Flag
			skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
		#endif //boneWeight5Flag
		#ifdef boneWeight6Flag
			skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
		#endif //boneWeight6Flag
		#ifdef boneWeight7Flag
			skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
		#endif //boneWeight7Flag
	#endif //skinningFlag

	#ifdef skinningFlag
		vec4 pos = u_worldTrans * skinning * vec4(a_position, 1.0);
	#else
		vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	#endif
		
	gl_Position = u_projViewTrans * pos;
		
	#ifdef shadowMapFlag
		vec4 spos = u_shadowMapProjViewTrans * pos;
		v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
		v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	#endif //shadowMapFlag
	
	#if defined(specularFlag) || defined(emissiveColorFlag) 
		vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);
	#endif
	
	#if defined(normalFlag)
		#if defined(skinningFlag)
			vec3 normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);
		#else
			vec3 normal = normalize(u_normalMatrix * a_normal);
		#endif

		#if (!defined (normalTextureFlag))
			v_normal = normal;
		#endif
	#endif // normalFlag

    #ifdef fogFlag
        vec3 flen = u_cameraPosition.xyz - pos.xyz;
        float fog = dot(flen, flen) * u_cameraPosition.w;
        v_fog = min(fog, 1.0);
    #endif
    
    #ifdef atmosphereFlag
    	// normalized vector from vertex position to camera position
		vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);
		// lambert factor represents angle between vertex normal and vector to camera 
		v_lambertFactorNormalToCamera = clamp(dot(normal, viewVec), 0.0, 1.0);
		v_opacity = 1.0;
    #endif

	#if defined(normalTextureFlag)
		vec3 strongestLightDir = vec3(0.0, 0.0, 1.0);
	#endif // normalTextureFlag

	#ifdef lightingFlag
		#if	defined(ambientLightFlag)
        	vec3 ambientLight = u_ambientLight;
		#elif defined(ambientFlag)
        	vec3 ambientLight = vec3(0.0);
		#endif
			
		#ifdef ambientCubemapFlag 		
			vec3 squaredNormal = normal * normal;
			vec3 isPositive  = step(0.0, normal);
			ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
					squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
					squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
		#endif // ambientCubemapFlag

		#ifdef sphericalHarmonicsFlag
			ambientLight += u_sphericalHarmonics[0];
			ambientLight += u_sphericalHarmonics[1] * normal.x;
			ambientLight += u_sphericalHarmonics[2] * normal.y;
			ambientLight += u_sphericalHarmonics[3] * normal.z;
			ambientLight += u_sphericalHarmonics[4] * (normal.x * normal.z);
			ambientLight += u_sphericalHarmonics[5] * (normal.z * normal.y);
			ambientLight += u_sphericalHarmonics[6] * (normal.y * normal.x);
			ambientLight += u_sphericalHarmonics[7] * (3.0 * normal.z * normal.z - 1.0);
			ambientLight += u_sphericalHarmonics[8] * (normal.x * normal.x - normal.y * normal.y);			
		#endif // sphericalHarmonicsFlag

		#ifdef ambientFlag
			#ifdef separateAmbientFlag
				v_ambientLight = ambientLight;
				v_lightDiffuse = vec3(0.0);
			#else
				v_lightDiffuse = ambientLight;
			#endif //separateAmbientFlag
		#else
	        v_lightDiffuse = vec3(0.0);
		#endif //ambientFlag

			
		#ifdef specularFlag
			v_lightSpecular = vec3(0.0);
		#endif // specularFlag
		
		#ifdef emissiveColorFlag
			float NdotL = clamp(dot(normal, viewVec), 0.0, 1.0);
			v_emissiveColor = u_emissiveColor * NdotL * 0.75 + u_emissiveColor * 0.25;
		#endif // emissiveColorFlag

		#if defined(normalTextureFlag)
			float strongestLuminance = 0.0;
		#endif // normalTextureFlag
					
		#if defined(numDirectionalLights) && (numDirectionalLights > 0) && defined(normalFlag)
			for (int i = 0; i < numDirectionalLights; i++) {
				vec3 lightDir = -u_dirLights[i].direction;
				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
				vec3 value = u_dirLights[i].color * NdotL;
				#if defined(normalTextureFlag)
					#if (numDirectionalLights == 1) && (!defined(numPointLights) || numPointLights == 0)
						strongestLightDir = normalize(lightDir);
					#else
						float lum = luminance(value);
						strongestLightDir = if_then_else(when_gt(lum, strongestLuminance), normalize(lightDir), strongestLightDir);
						strongestLuminance = max(lum, strongestLuminance);
					#endif
				#endif // normalTextureFlag
				v_lightDiffuse += value;
				#ifdef specularFlag
					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
					v_lightSpecular += value * pow(halfDotView, u_shininess);
				#endif // specularFlag
			}
		#endif // numDirectionalLights

		#if defined(numPointLights) && (numPointLights > 0) && defined(normalFlag)
			for (int i = 0; i < numPointLights; i++) {
				vec3 lightDir = u_pointLights[i].position - pos.xyz;
				float dist2 = dot(lightDir, lightDir);
				lightDir *= inversesqrt(dist2);
				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
				vec3 value = u_pointLights[i].color * NdotL;
				//vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));
				#if defined(normalTextureFlag)
					#if (numPointLights == 1) && (!defined (numDirectionalLights) || numDirectionalLights == 0)
						strongestLightDir = normalize(lightDir);
					#else
						float lum = luminance(value);
						strongestLightDir = if_then_else(when_gt(lum, strongestLuminance), normalize(lightDir), strongestLightDir);
						strongestLuminance = max(lum, strongestLuminance);
					#endif
				#endif // normalTextureFlag
				v_lightDiffuse += value;
				#ifdef specularFlag
					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
					v_lightSpecular += value * pow(halfDotView, u_shininess);
				#endif // specularFlag
			}
		#endif // numPointLights

	#endif // lightingFlag

	#if defined(normalTextureFlag)
		// matrix to convert eye space into tangent space
		vec3 n = normalize (a_normal);
		vec3 t = normalize (a_tangent);
		vec3 b = cross (n, t);
		
		// normalized vector from vertex position to light position in tangent space - passed to fragment shader
		v_lightVecTangent = normalize (vec3(dot (strongestLightDir, t), dot (strongestLightDir, b), dot (strongestLightDir, n)));
	#endif // normalTextureFlag
}
