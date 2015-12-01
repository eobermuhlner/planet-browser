package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GasPlanetShader implements Shader {

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
	private int u_planetColor0;
	private int u_planetColor1;
	private int u_planetColor2;
	
	public GasPlanetShader (String vertexProgram, String fragmentProgram) {
		this.vertexProgram = vertexProgram;
		this.fragmentProgram = fragmentProgram;
	}
	
	@Override
	public void init () {
		program = new ShaderProgram(vertexProgram, fragmentProgram);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
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
	
	public static class Provider extends BaseShaderProvider {
		@Override
		protected Shader createShader(Renderable renderable) {
			String vert = Gdx.files.internal("data/shaders/jupiter.vertex.glsl").readString();
			String frag = Gdx.files.internal("data/shaders/jupiter.fragment.glsl").readString();

			return new GasPlanetShader(vert, frag);
		}
	}
}
