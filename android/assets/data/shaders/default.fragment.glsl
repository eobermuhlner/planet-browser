#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

//varying vec3 v_debug_vec3;

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#ifdef normalFlag
varying vec3 v_normal;
#endif //normalFlag

#if defined(colorFlag)
varying vec4 v_color;
#endif

#ifdef atmosphereFlag
#define blendedFlag;
uniform vec4 u_atmosphereCenterColor;
uniform vec4 u_atmosphereHorizonColor;
uniform vec4 u_atmosphereRefractionColor;
uniform float u_atmosphereEnd;
uniform float u_atmosphereRefractionFactor;
varying float v_lambertFactorNormalToCamera;
varying float v_lambertFactorLightToCamera;
#endif

#ifdef blendedFlag
varying float v_opacity;
#ifdef alphaTestFlag
varying float v_alphaTest;
#endif //alphaTestFlag
#endif //blendedFlag

#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
#define textureFlag
varying MED vec2 v_texCoords0;
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef emissiveTextureFlag
uniform sampler2D u_emissiveTexture;
#endif

//#define emissiveDarkFlag
#ifdef emissiveDarkFlag
const float emissiveDarkLowThreshold = 0.00;
const float emissiveDarkHighThreshold = 0.20;
#endif

#ifdef specularColorFlag
uniform vec4 u_specularColor;
#endif

#ifdef emissiveColorFlag
varying vec4 v_emissiveColor;
#endif

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
varying MED vec2 v_texCoords1;
#endif

#if defined(normalTextureFlag)
varying vec3 v_lightVecTangent;
#endif

#ifdef lightingFlag
varying vec3 v_lightDiffuse;

#if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
#define ambientFlag
#endif //ambientFlag

#ifdef specularFlag
varying vec3 v_lightSpecular;
#endif //specularFlag

#ifdef emissiveFlag
varying vec3 v_lightEmissive;
#endif //emissiveFlag

#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));	
}

float getShadow() 
{
	return (//getShadowness(vec2(0,0)) + 
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif //shadowMapFlag

#if defined(ambientFlag) && defined(separateAmbientFlag)
varying vec3 v_ambientLight;
#endif //separateAmbientFlag

#endif //lightingFlag

#ifdef fogFlag
uniform vec4 u_fogColor;
varying float v_fog;
#endif // fogFlag

float transform(float fromMin, float fromMax, float toMin, float toMax, float value) {
		return (value - fromMin) / (fromMax - fromMin) * (toMax - toMin) + toMin;
}

void main() {
	#if defined(normalFlag)
		#if defined(normalTextureFlag)
			vec3 normal = texture2D(u_normalTexture, v_texCoords1).rgb;
			normal = normalize(normal * 2.0 - 1.0); // consider supporting different formats of normal texture
			float lambertFactor = clamp(dot(normal, v_lightVecTangent), 0.0, 1.0);
		#else
			vec3 normal = v_normal;
		#endif
	#endif
	
	#if defined(emissiveTextureFlag) && defined(emissiveColorFlag) && defined(colorFlag)
		vec4 emissive = texture2D(u_emissiveTexture, v_texCoords0) * v_emissiveColor * v_color;
	#elif defined(emissiveTextureFlag) && defined(emissiveColorFlag)
		vec4 emissive = texture2D(u_emissiveTexture, v_texCoords0) * v_emissiveColor;
	#elif defined(emissiveTextureFlag) && defined(colorFlag)
		vec4 emissive = texture2D(u_emissiveTexture, v_texCoords0) * v_color;
	#elif defined(emissiveTextureFlag)
		vec4 emissive = texture2D(u_emissiveTexture, v_texCoords0);
	#elif defined(emissiveColorFlag) && defined(colorFlag)
		vec4 emissive = v_emissiveColor * v_color;
	#elif defined(emissiveColorFlag)
		vec4 emissive = v_emissiveColor;
	#else
		vec4 emissive = vec4(0.0);
	#endif
	
	#if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_texCoords0) * u_diffuseColor * v_color;
	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_texCoords0) * u_diffuseColor;
	#elif defined(diffuseTextureFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_texCoords0) * v_color;
	#elif defined(diffuseTextureFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_texCoords0);
	#elif defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = u_diffuseColor * v_color;
	#elif defined(diffuseColorFlag)
		vec4 diffuse = u_diffuseColor;
	#elif defined(colorFlag)
		vec4 diffuse = v_color;
	#else
		#if defined(atmosphereFlag)
			//float atmosphereEnd = 0.7;

			float atmosphereReflectionFactor = 1.0 - v_lambertFactorNormalToCamera;
			atmosphereReflectionFactor = atmosphereReflectionFactor * atmosphereReflectionFactor;
		
			vec4 diffuse = mix(u_atmosphereCenterColor, u_atmosphereHorizonColor, atmosphereReflectionFactor / u_atmosphereEnd);
		
			float atmosphereRefractionFactor = clamp(transform(-0.30, 0.5, 1.0, 0.0, v_lambertFactorLightToCamera), 0.0, 1.0);
			atmosphereRefractionFactor *= u_atmosphereRefractionFactor;
			
			emissive = u_atmosphereRefractionColor * atmosphereRefractionFactor * (atmosphereReflectionFactor / u_atmosphereEnd);

			if (atmosphereReflectionFactor > u_atmosphereEnd) {
				float atmosphereFadeout = (atmosphereReflectionFactor - u_atmosphereEnd) / (1.0 - u_atmosphereEnd);
				atmosphereFadeout = 1.0 - atmosphereFadeout;
				atmosphereFadeout = atmosphereFadeout * atmosphereFadeout;
				
				diffuse *= atmosphereFadeout;
				emissive *= atmosphereFadeout;
			}
			
			//emissive = vec4(vec3(transform(-1.0, 1.0, 0.0, 1.0, v_lambertFactorLightToCamera)), 0.5);
		#else
			vec4 diffuse = vec4(0.0);
		#endif
	#endif

	#if defined(normalTextureFlag)
		diffuse = diffuse * lambertFactor;
	#endif

	#if (!defined(lightingFlag))  
		gl_FragColor = emissive + diffuse;
	#elif (!defined(specularFlag))
		#if defined(ambientFlag) && defined(separateAmbientFlag)
			#ifdef shadowMapFlag
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - vec4(v_ambientLight + getShadow() * v_lightDiffuse, 1.0);
				#endif
				gl_FragColor = emissive + (diffuse * (v_ambientLight + getShadow() * v_lightDiffuse));
				//gl_FragColor = texture2D(u_shadowTexture, v_shadowMapUv.xy);
			#else
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - vec4(v_ambientLight + v_lightDiffuse, 1.0);
				#endif
				gl_FragColor = emissive + (diffuse * vec4(v_ambientLight + v_lightDiffuse, 1.0));
			#endif //shadowMapFlag
		#else
			#ifdef shadowMapFlag
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - (getShadow() * vec4(v_lightDiffuse, 1.0));
				#endif
				gl_FragColor = emissive + getShadow() * diffuse * vec4(v_lightDiffuse, 1.0);
			#else
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - vec4(v_lightDiffuse, 1.0);
				#endif
				gl_FragColor = emissive + diffuse * vec4(v_lightDiffuse, 1.0);
			#endif //shadowMapFlag
		#endif
	#else
		#if defined(specularTextureFlag) && defined(specularColorFlag)
			vec4 specular = texture2D(u_specularTexture, v_texCoords0) * u_specularColor * vec4(v_lightSpecular, 1.0);
		#elif defined(specularTextureFlag)
			vec4 specular = texture2D(u_specularTexture, v_texCoords0) * vec4(v_lightSpecular, 1.0);
		#elif defined(specularColorFlag)
			vec4 specular = u_specularColor * vec4(v_lightSpecular, 1.0);
		#else
			vec4 specular = vec4(v_lightSpecular, 1.0);
		#endif
			
		#if defined(ambientFlag) && defined(separateAmbientFlag)
			#ifdef shadowMapFlag
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - vec4(getShadow() * v_lightDiffuse + v_ambientLight), 1.0);
				#endif
				gl_FragColor = emissive + (diffuse * vec4(getShadow() * v_lightDiffuse + v_ambientLight), 1.0) + specular;
				//gl_FragColor = texture2D(u_shadowTexture, v_shadowMapUv.xy);
			#else
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - (v_lightDiffuse + v_ambientLight));
				#endif
				gl_FragColor = emissive + (diffuse * vec4(v_lightDiffuse + v_ambientLight, 1.0)) + specular;
			#endif //shadowMapFlag
		#else
			#ifdef shadowMapFlag
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - vec4(getShadow() * v_lightDiffuse, 1.0);
				#endif
				gl_FragColor = emissive + getShadow() * ((diffuse * vec4(v_lightDiffuse, 1.0)) + specular);
			#else
				#ifdef emissiveDarkFlag
					emissive *= 1.0 - smoothstep(emissiveDarkLowThreshold, emissiveDarkHighThreshold, vec4(v_lightDiffuse, 1.0));
				#endif
				gl_FragColor = emissive + (diffuse * vec4(v_lightDiffuse, 1.0)) + specular;
			#endif //shadowMapFlag
		#endif
	#endif //lightingFlag

	#ifdef fogFlag
		gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
	#endif // end fogFlag

	#ifdef blendedFlag
		gl_FragColor.a = gl_FragColor.a * v_opacity;
		#ifdef alphaTestFlag
			if (gl_FragColor.a <= v_alphaTest)
				discard;
		#endif
	#else
		gl_FragColor.a = 1.0;
	#endif
	
	// for debugging:
	//gl_FragColor.rgb = v_debug_vec3;
}
