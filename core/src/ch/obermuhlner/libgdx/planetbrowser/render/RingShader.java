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

public class RingShader implements Shader {

	public static final Provider PROVIDER = new RingShader.Provider();

	private final String prefix;
	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_time;

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
	
	private int u_diffuseColor;
	private int u_opacity;

	public RingShader (String vertexProgram, String fragmentProgram, String prefix) {
		this.prefix = prefix;
		this.vertexProgram = vertexProgram;
		this.fragmentProgram = fragmentProgram;
	}
	
	private static String createPrefix(Renderable renderable) {
		StringBuilder prefix = new StringBuilder();
		return prefix.toString();
	}
	
	@Override
	public void init () {
		StopWatch watch = new StopWatch();
		program = new ShaderProgram(prefix + vertexProgram, prefix + fragmentProgram);
		System.out.println("Compiled ring shader in " + watch);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(ShaderUtils.createErrorMessage(program));
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_time = program.getUniformLocation("u_time");
		
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
		
		u_diffuseColor = program.getUniformLocation("u_diffuseColor");
		u_opacity = program.getUniformLocation("u_opacity");
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_NONE);
		
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

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

		RingAttribute ringAttribute = (RingAttribute)renderable.material.get(RingAttribute.Ring);

		// random
		program.setUniformf(u_random0, ringAttribute.randomValues[0]);
		program.setUniformf(u_random1, ringAttribute.randomValues[1]);
		program.setUniformf(u_random2, ringAttribute.randomValues[2]);
		program.setUniformf(u_random3, ringAttribute.randomValues[3]);
		program.setUniformf(u_random4, ringAttribute.randomValues[4]);
		program.setUniformf(u_random5, ringAttribute.randomValues[5]);
		program.setUniformf(u_random6, ringAttribute.randomValues[6]);
		program.setUniformf(u_random7, ringAttribute.randomValues[7]);
		program.setUniformf(u_random8, ringAttribute.randomValues[8]);
		program.setUniformf(u_random9, ringAttribute.randomValues[9]);

		// color
		program.setUniformf(u_diffuseColor, ringAttribute.color);			

		// opacity
		program.setUniformf(u_opacity, ringAttribute.opacity);			

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
		private String vert = Gdx.files.internal("data/shaders/ring.vertex.glsl").readString();
		private String frag = Gdx.files.internal("data/shaders/ring.fragment.glsl").readString();

		private Provider() {
		}
		
		@Override
		protected String createPrefix(Renderable renderable) {
			return RingShader.createPrefix(renderable);
		}

		@Override
		protected Shader createShader(Renderable renderable, String prefix) {
			RingShader shader = new RingShader(vert, frag, prefix);
			shader.init();
			return shader;
		}
	}
}
