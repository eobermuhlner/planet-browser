package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import ch.obermuhlner.libgdx.planetbrowser.util.StopWatch;

public class GasPlanetShader implements Shader {

	public static final Provider PROVIDER = new GasPlanetShader.Provider();

	private final String prefix;
	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_time;
	private int u_normalStep;

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

	public GasPlanetShader (String vertexProgram, String fragmentProgram, String prefix) {
		this.prefix = prefix;
		this.vertexProgram = vertexProgram;
		this.fragmentProgram = fragmentProgram;
	}
	
	private static String createPrefix(Renderable renderable) {
		StringBuilder prefix = new StringBuilder();

		int createTexture = (int) TerrestrialPlanetShader.getFloatAttributeValue(renderable, TerrestrialPlanetFloatAttribute.CreateTexture, TerrestrialPlanetFloatAttribute.CREATE_DIFFUSE_TEXTURE);
		int createTextureCount = 0;
		if ((createTexture & TerrestrialPlanetFloatAttribute.CREATE_BUMP_TEXTURE) != 0) {
			prefix.append("#define createBumpFlag\n");
			prefix.append("#define createBumpOutput " + createTextureCount + "\n");
			createTextureCount++;
		}
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

		return prefix.toString();
	}
	
	@Override
	public void init () {
		StopWatch watch = new StopWatch();
		program = new ShaderProgram(prefix + vertexProgram, prefix + fragmentProgram);
		System.out.println("Compiled gasplanet shader in " + watch);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(ShaderUtils.createErrorMessage(program));
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_time = program.getUniformLocation("u_time");
		u_normalStep = program.getUniformLocation("u_normalStep");
		
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
		
		u_planetColor0 = program.getUniformLocation("u_planetColor0");
		u_planetColor1 = program.getUniformLocation("u_planetColor1");
		u_planetColor2 = program.getUniformLocation("u_planetColor2");
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
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
		
		program.setUniformf(u_normalStep, TerrestrialPlanetShader.getFloatAttributeValue(renderable, MoreFloatAttribute.NormalStep, 0.0001f));

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

		// color array
		ColorArrayAttribute colorArrayAttribute = (ColorArrayAttribute) renderable.material.get(ColorArrayAttribute.PlanetColors);
		if (colorArrayAttribute != null) {
			program.setUniformf(u_planetColor0, colorArrayAttribute.colors[0].r, colorArrayAttribute.colors[0].g, colorArrayAttribute.colors[0].b);			
			program.setUniformf(u_planetColor1, colorArrayAttribute.colors[1].r, colorArrayAttribute.colors[1].g, colorArrayAttribute.colors[1].b);			
			program.setUniformf(u_planetColor2, colorArrayAttribute.colors[2].r, colorArrayAttribute.colors[2].g, colorArrayAttribute.colors[2].b);			
		}
		
		// mesh
		renderable.meshPart.render(program);
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
		private String vert = Gdx.files.internal("data/shaders/jupiter.vertex.glsl").readString();
		private String frag = Gdx.files.internal("data/shaders/jupiter.fragment.glsl").readString();

		private Provider() {
		}
		
		@Override
		protected String createPrefix(Renderable renderable) {
			return GasPlanetShader.createPrefix(renderable);
		}

		@Override
		protected Shader createShader(Renderable renderable, String prefix) {
			GasPlanetShader shader = new GasPlanetShader(vert, frag, prefix);
			shader.init();
			return shader;
		}
	}
}
