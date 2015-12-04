package ch.obermuhlner.libgdx.graphics.glutils;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;

public class MultiTextureFrameBuffer extends MultiFrameBuffer<Texture> {

	private int textureCount;

	public MultiTextureFrameBuffer(Format format, int width, int height, int textureCount) {
		super(format, width, height);
		this.textureCount = textureCount;
		
		build();
	}

	@Override
	protected Array<Texture> createColorTextures() {
		Array<Texture> result = new Array<Texture>();
		for (int i = 0; i < textureCount; i++) {
			result.add(createColorTexture());
		}
		return result;
	}

	@Override
	protected void disposeColorTextures(Array<Texture> colorTextures) {
		for (int i = 0; i < colorTextures.size; i++) {
			disposeColorTexture(colorTextures.get(i));
		}		
	}
	
	protected Texture createColorTexture() {
		Texture result = new Texture(width, height, format);
		result.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		result.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		return result;
	}
	
	protected void disposeColorTexture(Texture colorTexture) {
		colorTexture.dispose();
	}

}
