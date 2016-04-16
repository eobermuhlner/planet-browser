package ch.obermuhlner.libgdx.planetbrowser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Config {

	public static final boolean SCREENSHOTS = false;

	public static float screenDensityFactor = 1.0f;

	public static float terrainZoomFactor = 1000.0f;

	public static SkinFactory skinFactory = new DefaultSkinFactory();
	
	private static Skin skin;
	
	public static Skin getSkin() {
		if (skin == null) {
			skin = skinFactory.createSkin();
		}
		return skin;
	}

	public static int getFontSize(int theFontSize) {
		float density = Gdx.graphics.getDensity();
		return Math.round(theFontSize * density * screenDensityFactor * 0.8f);
	}

	public interface SkinFactory {
		Skin createSkin();
	}

	private static class DefaultSkinFactory implements SkinFactory {
		@Override
		public Skin createSkin() {
			Skin skin = new Skin();
			skin.addRegions(new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));
			skin.add("small-font", new BitmapFont());
			skin.add("default-font", new BitmapFont());
			skin.add("large-font", new BitmapFont());
			skin.load(Gdx.files.internal("data/ui/uiskin.json"));
			return skin;
		}
	}
}
