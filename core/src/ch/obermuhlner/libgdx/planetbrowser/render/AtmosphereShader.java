
package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AtmosphereShader implements Shader {

	private Renderable renderable;
	private final String vertexProgram;
	private final String fragmentProgram;

	private ShaderProgram program;
	
	private int u_projViewTrans;
	private int u_worldTrans;
	private int u_normalMatrix;	
	private int u_diffuseTexture;
	private int u_cameraPosition;
	private int u_cameraDirection;
	private int u_cameraUp;

	private int u_time;

	private RenderContext context;
	
	private float time;
	
	public AtmosphereShader (Renderable renderable) {
		this.renderable = renderable;

		this.vertexProgram = Gdx.files.internal("data/shaders/atmosphere.vertex.glsl").readString();
		this.fragmentProgram = Gdx.files.internal("data/shaders/atmosphere.fragment.glsl").readString();
	}
	
	@Override
	public void init () {
		String prefix = createPrefix();
		program = new ShaderProgram(prefix + vertexProgram, prefix + fragmentProgram);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_normalMatrix = program.getUniformLocation("u_normalMatrix");
		u_diffuseTexture = program.getUniformLocation("u_diffuseTexture");
		u_cameraPosition = program.getUniformLocation("u_cameraPosition");
		u_cameraDirection = program.getUniformLocation("u_cameraDirection");
		u_cameraUp = program.getUniformLocation("u_cameraUp");
		u_time = program.getUniformLocation("u_time");
	}

	private String createPrefix() {
		StringBuilder prefix = new StringBuilder();
		
		return prefix.toString();
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		this.context = context;
		
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);

		program.begin();
		program.setUniformMatrix(u_projViewTrans, camera.combined);
		program.setUniformf(u_cameraPosition, camera.position.x, camera.position.y, camera.position.z, 1.1881f / camera.far * camera.far);
		program.setUniformf(u_cameraDirection, camera.direction.x, camera.direction.y, camera.direction.z);
		program.setUniformf(u_cameraUp, camera.up.x, camera.up.y, camera.up.z);
		program.setUniformf(u_time, System.currentTimeMillis() / 1000.0f);
		
	}

	@Override
	public void end () {
		program.end();
	}

	private final Matrix3 tmpM = new Matrix3();

	@Override
	public void render (Renderable renderable) {
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// world transformation
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		program.setUniformMatrix(u_normalMatrix, tmpM.set(renderable.worldTransform).inv().transpose());
		
		// diffuse texture
		TextureAttribute textureAttribute = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
		if (textureAttribute != null) {
			int textureUnit = context.textureBinder.bind(textureAttribute.textureDescription);
			program.setUniformi(u_diffuseTexture, textureUnit);
		}

		// time
		program.setUniformf(u_time, time += Gdx.graphics.getDeltaTime());
		
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
			return new AtmosphereShader(renderable);
		}
	}
}
