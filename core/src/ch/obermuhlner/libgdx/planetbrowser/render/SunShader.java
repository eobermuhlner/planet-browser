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

public class SunShader implements Shader {

	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_cameraPosition;
	
	private int u_time;
	private long startMillis;
	
	public SunShader (String vertexProgram, String fragmentProgram) {
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
		u_cameraPosition = program.getUniformLocation("u_cameraPosition");
		u_time = program.getUniformLocation("u_time");
		
		startMillis = System.currentTimeMillis();
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);

		program.begin();
		program.setUniformMatrix(u_projViewTrans, camera.combined);
		long deltaMillis = System.currentTimeMillis() - startMillis;
		program.setUniformf(u_time, deltaMillis / 1000.0f);
		
		program.setUniformf(u_cameraPosition, camera.position.x, camera.position.y, camera.position.z,
				1.1881f / (camera.far * camera.far));
	}

	@Override
	public void end () {
		program.end();
	}

	@Override
	public void render (Renderable renderable) {
		// world transformation
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		
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
			String vert = Gdx.files.internal("data/shaders/sun.vertex.glsl").readString();
			String frag = Gdx.files.internal("data/shaders/sun.fragment.glsl").readString();

			return new SunShader(vert, frag);
		}
	}
}
