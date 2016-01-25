
package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.StopWatch;

public class TerrestrialPlanetShader implements Shader {

	public static final Provider PROVIDER = new TerrestrialPlanetShader.Provider();

	private String prefix;
	
	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_diffuseTexture;
	private int u_specularTexture;
	private int u_time;

	private int u_normalStep;

	private int u_heightMin;
	private int u_heightMax;
	private int u_heightFrequency;
	private int u_heightWater;
	private int u_heightFunctionValue;
	private int u_craterBaseGrid;
	private int u_craterProbability;
	private int u_iceLevel;
	
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

	public TerrestrialPlanetShader (Renderable renderable, String vertexProgram, String fragmentProgram, String prefix) {
		this.prefix = prefix;

		TerrestrialAttribute terrestrialAttribute = (TerrestrialAttribute) renderable.material.get(TerrestrialAttribute.Terrestrial);
		fragmentProgram = fragmentProgram.replace("$HEIGHT_FUNCTION", terrestrialAttribute.heightFunction);

		this.vertexProgram = vertexProgram;
		this.fragmentProgram = fragmentProgram;
	}
	
	@Override
	public void init () {
		StopWatch watch = new StopWatch();
		program = new ShaderProgram(prefix + vertexProgram, prefix + fragmentProgram);
		System.out.println("Compiled terrestrial shader in " + watch);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(ShaderUtils.createErrorMessage(program));
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
		u_specularTexture = program.getUniformLocation("u_specularTexture");
		u_time = program.getUniformLocation("u_time");

		u_normalStep = program.getUniformLocation("u_normalStep");

		u_heightMin = program.getUniformLocation("u_heightMin");
		u_heightMax = program.getUniformLocation("u_heightMax");
		u_heightFrequency = program.getUniformLocation("u_heightFrequency");
		u_heightWater = program.getUniformLocation("u_heightWater");
		u_heightFunctionValue = program.getUniformLocation("u_heightFunctionValue");
		u_craterBaseGrid = program.getUniformLocation("u_craterBaseGrid");
		u_craterProbability = program.getUniformLocation("u_craterProbability");
		u_iceLevel = program.getUniformLocation("u_iceLevel");
		
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

	private static String createPrefix(Renderable renderable) {
		StringBuilder prefix = new StringBuilder();
		
		TerrestrialAttribute terrestrialAttribute = (TerrestrialAttribute) renderable.material.get(TerrestrialAttribute.Terrestrial);
		
		if (terrestrialAttribute.planetColors != null) {
			prefix.append("#define planetColorsFlag\n");
		}

		if (terrestrialAttribute.craterBaseGrid != 0.0f) {
			prefix.append("#define cratersFlag\n");
		}

		IntAttribute createTextureAttribute = (IntAttribute) renderable.material.get(CreateTextureAttribute.CreateTexture);
		int createTexture = createTextureAttribute.value;

		int createTextureCount = 0;
		if ((createTexture & CreateTextureAttribute.CREATE_BUMP_TEXTURE) != 0) {
			prefix.append("#define createBumpFlag\n");
			prefix.append("#define createBumpOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & CreateTextureAttribute.CREATE_DIFFUSE_TEXTURE) != 0) {
			prefix.append("#define createDiffuseFlag\n");
			prefix.append("#define createDiffuseOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & CreateTextureAttribute.CREATE_NORMAL_TEXTURE) != 0) {
			prefix.append("#define createNormalFlag\n");
			prefix.append("#define createNormalOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & CreateTextureAttribute.CREATE_SPECULAR_TEXTURE) != 0) {
			prefix.append("#define createSpecularFlag\n");
			prefix.append("#define createSpecularOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if ((createTexture & CreateTextureAttribute.CREATE_EMISSIVE_TEXTURE) != 0) {
			prefix.append("#define createEmissiveFlag\n");
			prefix.append("#define createEmissiveOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
		if (createTextureCount > 1) {
			prefix.append("#define multiTextureRenderingFlag\n");
		}
		
		if (renderable.material.get(TextureAttribute.Diffuse) != null) {
			prefix.append("#define diffuseTextureFlag\n");			
		}
		if (renderable.material.get(TextureAttribute.Specular) != null) {
			prefix.append("#define specularTextureFlag\n");			
		}

		// add comment of height function so they can be distinguished in the prefix
		prefix.append("// " + terrestrialAttribute.heightFunction + "\n");
		
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
		
		TerrestrialAttribute terrestrialAttribute = (TerrestrialAttribute) renderable.material.get(TerrestrialAttribute.Terrestrial);

		// diffuse texture
		TextureAttribute diffuseTextureAttribute = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
		if (diffuseTextureAttribute != null) {
			int textureUnit = context.textureBinder.bind(diffuseTextureAttribute.textureDescription);
			program.setUniformi(u_diffuseTexture, textureUnit);
		}

		// specular texture
		TextureAttribute specularTextureAttribute = (TextureAttribute) renderable.material.get(TextureAttribute.Specular);
		if (specularTextureAttribute != null) {
			int textureUnit = context.textureBinder.bind(specularTextureAttribute.textureDescription);
			program.setUniformi(u_specularTexture, textureUnit);
		}

		// additional data
		program.setUniformf(u_normalStep, getFloatAttributeValue(renderable, MoreFloatAttribute.NormalStep, 0.0001f));
		
		// planet data
		int heightFrequencyPowerOfTwo = terrestrialAttribute.heightFrequency;
		float heightFrequency = MathUtil.powerOfTwo(heightFrequencyPowerOfTwo);
		program.setUniformf(u_heightMin, terrestrialAttribute.heightMin);
		program.setUniformf(u_heightMax, terrestrialAttribute.heightMax);
		program.setUniformf(u_heightFrequency, heightFrequency);
		program.setUniformf(u_heightWater, terrestrialAttribute.heightWater);
		program.setUniformf(u_heightFunctionValue, terrestrialAttribute.heightFunctionValue);
		program.setUniformf(u_iceLevel, terrestrialAttribute.iceLevel);
		
		program.setUniformf(u_craterBaseGrid, terrestrialAttribute.craterBaseGrid);
		program.setUniformf(u_craterProbability, terrestrialAttribute.craterProbability);
		
		// planet color array
		if (terrestrialAttribute.planetColors != null) {
			int index = 0;
			program.setUniformf(u_planetColor0, terrestrialAttribute.planetColors[index]);
			index = (index+1) % terrestrialAttribute.planetColors.length;
			program.setUniformf(u_planetColor1, terrestrialAttribute.planetColors[index]);			
			index = (index+1) % terrestrialAttribute.planetColors.length;
			program.setUniformf(u_planetColor2, terrestrialAttribute.planetColors[index]);			
			index = (index+1) % terrestrialAttribute.planetColors.length;
			program.setUniformf(u_planetColor3, terrestrialAttribute.planetColors[index]);			
			index = (index+1) % terrestrialAttribute.planetColors.length;
			program.setUniformf(u_planetColor4, terrestrialAttribute.planetColors[index]);			
			index = (index+1) % terrestrialAttribute.planetColors.length;
			program.setUniformf(u_planetColor5, terrestrialAttribute.planetColors[index]);			
		}
		
		// planet color frequency
		float planetColorFrequency0 = terrestrialAttribute.planetColorFrequencies == null ? 8 : terrestrialAttribute.planetColorFrequencies[0];
		float planetColorFrequency1 = terrestrialAttribute.planetColorFrequencies == null ? 4 : terrestrialAttribute.planetColorFrequencies[1];
		float planetColorFrequency2 = terrestrialAttribute.planetColorFrequencies == null ? 8 : terrestrialAttribute.planetColorFrequencies[2];
		float planetColorFrequency3 = terrestrialAttribute.planetColorFrequencies == null ? 4 : terrestrialAttribute.planetColorFrequencies[3];
		program.setUniformf(u_planetColorFrequency0, planetColorFrequency0);
		program.setUniformf(u_planetColorFrequency1, planetColorFrequency1);
		program.setUniformf(u_planetColorFrequency2, planetColorFrequency2);
		program.setUniformf(u_planetColorFrequency3, planetColorFrequency3);

		// random
		program.setUniformf(u_random0, terrestrialAttribute.randomValues[0]);
		program.setUniformf(u_random1, terrestrialAttribute.randomValues[1]);
		program.setUniformf(u_random2, terrestrialAttribute.randomValues[2]);
		program.setUniformf(u_random3, terrestrialAttribute.randomValues[3]);
		program.setUniformf(u_random4, terrestrialAttribute.randomValues[4]);
		program.setUniformf(u_random5, terrestrialAttribute.randomValues[5]);
		program.setUniformf(u_random6, terrestrialAttribute.randomValues[6]);
		program.setUniformf(u_random7, terrestrialAttribute.randomValues[7]);
		program.setUniformf(u_random8, terrestrialAttribute.randomValues[8]);
		program.setUniformf(u_random9, terrestrialAttribute.randomValues[9]);

		// time
		program.setUniformf(u_time, time += Gdx.graphics.getDeltaTime());
		
		// mesh
		renderable.meshPart.render(program);
	}

	public static float getFloatAttributeValue(Renderable renderable, long attributeType, float defaultValue) {
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

	public static class Provider extends PrefixShaderProvider {
		private String vert = Gdx.files.internal("data/shaders/terrestrial.vertex.glsl").readString();
		private String frag = Gdx.files.internal("data/shaders/terrestrial.fragment.glsl").readString();

		private Provider() {
		}
		
		@Override
		protected String createPrefix(Renderable renderable) {
			return TerrestrialPlanetShader.createPrefix(renderable);
		}

		@Override
		protected Shader createShader(Renderable renderable, String prefix) {
			TerrestrialPlanetShader shader = new TerrestrialPlanetShader(renderable, vert, frag, prefix);
			shader.init();
			return shader;
		}
	}
}
