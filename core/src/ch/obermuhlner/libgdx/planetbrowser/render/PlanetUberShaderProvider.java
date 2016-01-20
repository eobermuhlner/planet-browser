package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

public class PlanetUberShaderProvider extends UberShaderProvider  {

	public PlanetUberShaderProvider() {
		super("default");
	}

	@Override
	protected Shader createShader(Renderable renderable) {
		if (renderable.material.get(RingFloatAttribute.Ring) != null) {
			return RingShader.PROVIDER.getShader(renderable);
		}
		if (renderable.material.get(SunFloatAttribute.SunNoise) != null) {
			return new SunShader.Provider().createShader(renderable);
		}
		
		return super.createShader(renderable);
	}
}
