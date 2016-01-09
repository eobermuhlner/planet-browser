package ch.obermuhlner.libgdx.planetbrowser.render;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public abstract class PrefixShaderProvider implements ShaderProvider {

	private final Map<String, Shader> shaders = new HashMap<String, Shader>();
	
	@Override
	public Shader getShader(Renderable renderable) {
		Shader suggestedShader = renderable.shader;
		if (suggestedShader != null && suggestedShader.canRender(renderable)) {
			return suggestedShader;
		}

		String prefix = createPrefix(renderable);
		
		Shader shader = shaders.get(prefix);
		if (shader == null) {
			shader = createShader(renderable, prefix);
			shaders.put(prefix, shader);
		}

		return shader;
	}

	protected abstract String createPrefix(Renderable renderable);

	protected abstract Shader createShader(Renderable renderable, String prefix);

	@Override
	public void dispose() {
		// do not dispose
	}
}
