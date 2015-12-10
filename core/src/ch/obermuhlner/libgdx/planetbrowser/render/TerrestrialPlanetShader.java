
package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;

public class TerrestrialPlanetShader implements Shader {

	private static final float DEFAULT_COLOR_NOISE = 0.0f;

	private static final float DEFAULT_HEIGHT_MOUNTAINS = 0.0f;
	
	private Renderable renderable;
	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_diffuseTexture;
	private int u_time;

	private int u_heightMin;
	private int u_heightMax;
	private int u_heightFrequency;
	private int u_heightWater;
	private int u_heightMountains;
	private int u_iceLevel;
	private int u_colorNoise;
	private int u_colorFrequency;
	
	private int u_random0;
	private int u_random1;
	private int u_random2;
	private int u_random3;
	private int u_random4;
	private int u_random5;
	private int u_random6;
	private int u_random7;
	private int u_random8;
	private int u_random9;
	
	private int u_planetColor0;
	private int u_planetColor1;
	private int u_planetColor2;
	private int u_planetColor3;
	private int u_planetColor4;
	private int u_planetColor5;

	private int u_planetColorFrequency0;
	private int u_planetColorFrequency1;
	private int u_planetColorFrequency2;
	private int u_planetColorFrequency3;

	private RenderContext context;
	
	private float time;
	
	public TerrestrialPlanetShader (Renderable renderable, String vertexProgram, String fragmentProgram) {
		this.renderable = renderable;

		String code = getShaderFunctionAttributeValue(renderable, TerrestrialHeightShaderFunctionAttribute.TerrestrialHeightFunction, "");
		fragmentProgram = fragmentProgram.replace("$HEIGHT_FUNCTION", code);

		this.vertexProgram = vertexProgram;
		this.fragmentProgram = fragmentProgram;
	}
	
	@Override
	public void init () {
		String prefix = createPrefix();
		program = new ShaderProgram(prefix + vertexProgram, prefix + fragmentProgram);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(ShaderUtils.createErrorMessage(program));
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
		u_time = program.getUniformLocation("u_time");

		u_heightMin = program.getUniformLocation("u_heightMin");
		u_heightMax = program.getUniformLocation("u_heightMax");
		u_heightFrequency = program.getUniformLocation("u_heightFrequency");
		u_heightWater = program.getUniformLocation("u_heightWater");
		u_heightMountains = program.getUniformLocation("u_heightMountains");
		u_iceLevel = program.getUniformLocation("u_iceLevel");
		u_colorNoise = program.getUniformLocation("u_colorNoise");
		u_colorFrequency = program.getUniformLocation("u_colorFrequency");
		
		u_planetColor0 = program.getUniformLocation("u_planetColor0");
		u_planetColor1 = program.getUniformLocation("u_planetColor1");
		u_planetColor2 = program.getUniformLocation("u_planetColor2");
		u_planetColor3 = program.getUniformLocation("u_planetColor3");
		u_planetColor4 = program.getUniformLocation("u_planetColor4");
		u_planetColor5 = program.getUniformLocation("u_planetColor5");

		u_planetColorFrequency0 = program.getUniformLocation("u_planetColorFrequency0");
		u_planetColorFrequency1 = program.getUniformLocation("u_planetColorFrequency1");
		u_planetColorFrequency2 = program.getUniformLocation("u_planetColorFrequency2");
		u_planetColorFrequency3 = program.getUniformLocation("u_planetColorFrequency3");

		u_random0 = program.getUniformLocation("u_random0");
		u_random1 = program.getUniformLocation("u_random1");
		u_random2 = program.getUniformLocation("u_random2");
		u_random3 = program.getUniformLocation("u_random3");
		u_random4 = program.getUniformLocation("u_random4");
		u_random5 = program.getUniformLocation("u_random5");
		u_random6 = program.getUniformLocation("u_random6");
		u_random7 = program.getUniformLocation("u_random7");
		u_random8 = program.getUniformLocation("u_random8");
		u_random9 = program.getUniformLocation("u_random9");
	}

	private String createPrefix() {
		StringBuilder prefix = new StringBuilder();
		
		if (getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.ColorNoise, DEFAULT_COLOR_NOISE) != 0.0f) {
			prefix.append("#define colorNoiseFlag\n");
		}
		if (getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightMountains, DEFAULT_HEIGHT_MOUNTAINS) != 0.0f) {
			prefix.append("#define mountainsFlag\n");
		}
		
		int createTexture = (int) getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.CreateTexture, TerrestrialPlanetFloatAttribute.CREATE_DIFFUSE_TEXTURE);
		int createTextureCount = 0;
		if ((createTexture & TerrestrialPlanetFloatAttribute.CREATE_DIFFUSE_TEXTURE) != 0) {
			prefix.append("#define createDiffuseFlag\n");
			prefix.append("#define createDiffuseOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & TerrestrialPlanetFloatAttribute.CREATE_NORMAL_TEXTURE) != 0) {
			prefix.append("#define createNormalFlag\n");
			prefix.append("#define createNormalOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & TerrestrialPlanetFloatAttribute.CREATE_SPECULAR_TEXTURE) != 0) {
			prefix.append("#define createSpecularFlag\n");
			prefix.append("#define createSpecularOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & TerrestrialPlanetFloatAttribute.CREATE_EMISSIVE_TEXTURE) != 0) {
			prefix.append("#define createEmissiveFlag\n");
			prefix.append("#define createEmissiveOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if (createTextureCount > 1) {
			prefix.append("#define multiTextureRenderingFlag\n");
		}
		
		if (renderable.material.get(ColorArrayAttribute.PlanetColors) != null) {
			prefix.append("#define planetColorsFlag\n");
		} else if (renderable.material.get(TextureAttribute.Diffuse) != null) {
			prefix.append("#define diffuseTextureFlag\n");			
		} else {
			prefix.append("#define debugColorsFlag\n");
		}
		
		return prefix.toString();
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		this.context = context;
		
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);

		program.begin();
		program.setUniformMatrix(u_projViewTrans, camera.combined);
		program.setUniformf(u_time, System.currentTimeMillis() / 1000.0f);
		
	}

	@Override
	public void end () {
		program.end();
	}

	@Override
	public void render (Renderable renderable) {
		// world transformation
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		
		// diffuse texture
		TextureAttribute textureAttribute = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
		if (textureAttribute != null) {
			int textureUnit = context.textureBinder.bind(textureAttribute.textureDescription);
			program.setUniformi(u_diffuseTexture, textureUnit);
		}

		// planet data
		int heightFrequencyPowerOfTwo = (int) getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightFrequency, 4f);
		float heightFrequency = MathUtil.powerOfTwo(heightFrequencyPowerOfTwo);
		int colorFrequencyPowerOfTwo = (int) getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.ColorFrequency, 16f);
		float colorFrequency = MathUtil.powerOfTwo(colorFrequencyPowerOfTwo);
		program.setUniformf(u_heightMin, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightMin, 0.0f));
		program.setUniformf(u_heightMax, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightMax, 1.0f));
		program.setUniformf(u_heightFrequency, heightFrequency);
		program.setUniformf(u_heightWater, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightWater, 0.0f));
		program.setUniformf(u_heightMountains, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.HeightMountains, DEFAULT_HEIGHT_MOUNTAINS));
		program.setUniformf(u_iceLevel, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.IceLevel, 0.0f));
		program.setUniformf(u_colorNoise, getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.ColorNoise, DEFAULT_COLOR_NOISE));
		program.setUniformf(u_colorFrequency, colorFrequency);
		
		// planet color array
		ColorArrayAttribute colorArrayAttribute = (ColorArrayAttribute) renderable.material.get(ColorArrayAttribute.PlanetColors);
		if (colorArrayAttribute != null) {
			int index = 0;
			program.setUniformf(u_planetColor0, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);
			index = (index+1) % colorArrayAttribute.colors.length;
			program.setUniformf(u_planetColor1, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);			
			index = (index+1) % colorArrayAttribute.colors.length;
			program.setUniformf(u_planetColor2, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);			
			index = (index+1) % colorArrayAttribute.colors.length;
			program.setUniformf(u_planetColor3, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);			
			index = (index+1) % colorArrayAttribute.colors.length;
			program.setUniformf(u_planetColor4, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);			
			index = (index+1) % colorArrayAttribute.colors.length;
			program.setUniformf(u_planetColor5, colorArrayAttribute.colors[index].r, colorArrayAttribute.colors[index].g, colorArrayAttribute.colors[index].b);			
		}
		
		// planet color frequency
		FloatArrayAttribute planetColorFrequenciesAttribute = (FloatArrayAttribute)renderable.material.get(FloatArrayAttribute.PlanetColorFrequencies);
		float planetColorFrequency0 = planetColorFrequenciesAttribute == null ? 8 : planetColorFrequenciesAttribute.values[0];
		float planetColorFrequency1 = planetColorFrequenciesAttribute == null ? 4 : planetColorFrequenciesAttribute.values[1];
		float planetColorFrequency2 = planetColorFrequenciesAttribute == null ? 8 : planetColorFrequenciesAttribute.values[2];
		float planetColorFrequency3 = planetColorFrequenciesAttribute == null ? 4 : planetColorFrequenciesAttribute.values[3];
		program.setUniformf(u_planetColorFrequency0, planetColorFrequency0);
		program.setUniformf(u_planetColorFrequency1, planetColorFrequency1);
		program.setUniformf(u_planetColorFrequency2, planetColorFrequency2);
		program.setUniformf(u_planetColorFrequency3, planetColorFrequency3);

		// random
		FloatArrayAttribute floatArrayAttribute = (FloatArrayAttribute)renderable.material.get(FloatArrayAttribute.RandomFloatArray);
		program.setUniformf(u_random0, floatArrayAttribute.values[0]);
		program.setUniformf(u_random1, floatArrayAttribute.values[1]);
		program.setUniformf(u_random2, floatArrayAttribute.values[2]);
		program.setUniformf(u_random3, floatArrayAttribute.values[3]);
		program.setUniformf(u_random4, floatArrayAttribute.values[4]);
		program.setUniformf(u_random5, floatArrayAttribute.values[5]);
		program.setUniformf(u_random6, floatArrayAttribute.values[6]);
		program.setUniformf(u_random7, floatArrayAttribute.values[7]);
		program.setUniformf(u_random8, floatArrayAttribute.values[8]);
		program.setUniformf(u_random9, floatArrayAttribute.values[9]);

		// time
		program.setUniformf(u_time, time += Gdx.graphics.getDeltaTime());
		
		// mesh
		renderable.meshPart.render(program);
	}

	private String getShaderFunctionAttributeValue(Renderable renderable, long attributeType, String defaultValue) {
		ShaderFunctionAttribute codeAttribute = (ShaderFunctionAttribute) renderable.material.get(attributeType);
		return codeAttribute == null ? defaultValue : codeAttribute.code;
	}
	
	private float getFloatAttributeValue(Renderable renderable, long attributeType, float defaultValue) {
		FloatAttribute floatAttribute = (FloatAttribute) renderable.material.get(attributeType);
		return floatAttribute == null ? defaultValue : floatAttribute.value;
	}

	@Override
	public void dispose () {
		program.dispose();
	}

	@Override
	public int compareTo (Shader other) {
		return 0;
	}

	@Override
	public boolean canRender (Renderable instance) {
		return true;
	}

	public static class Provider extends BaseShaderProvider {
		@Override
		protected Shader createShader(Renderable renderable) {
			String vert = Gdx.files.internal("data/shaders/terrestrial.vertex.glsl").readString();
			String frag = Gdx.files.internal("data/shaders/terrestrial.fragment.glsl").readString();

			return new TerrestrialPlanetShader(renderable, vert, frag);
		}
	}
}
