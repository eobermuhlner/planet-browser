package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

public class PlanetUberShaderProvider extends UberShaderProvider  {

	public PlanetUberShaderProvider() {
		super("default");
	}

	@Override
	protected Shader createShader(Renderable renderable) {
		return super.createShader(renderable);
	}
}
