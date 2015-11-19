package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

public class UberShaderProvider extends BaseShaderProvider {

	public static final UberShaderProvider DEFAULT = new UberShaderProvider("default");
	
	private String shaderName;

	public UberShaderProvider (String shaderName) {
		this.shaderName = shaderName;
	}
	
	@Override
	protected Shader createShader(Renderable renderable) {
		String name = shaderName;
		
		String vert = Gdx.files.internal("data/shaders/" + name + ".vertex.glsl").readString();
		String frag = Gdx.files.internal("data/shaders/" + name + ".fragment.glsl").readString();
		
		DefaultShader.Config config = new DefaultShader.Config(vert, frag);
		config.numDirectionalLights = 0;
		config.numPointLights = 1;
		return new DefaultShader(renderable, config);
	}
}
