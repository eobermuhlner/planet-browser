package ch.obermuhlner.libgdx.planetbrowser.android;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ch.obermuhlner.libgdx.planetbrowser.Config;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.Config.SkinFactory;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Config.skinFactory = new AndroidSkinFactory();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PlanetBrowser(), config);
	}
	
	public static class AndroidSkinFactory implements SkinFactory {
		@Override
		public Skin createSkin() {
			Skin skin = new Skin();
			skin.addRegions(new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));
			skin.add("small-font", generateFont("data/fonts/Orbitron-Medium.ttf", 16));
			skin.add("default-font", generateFont("data/fonts/Orbitron-Medium.ttf", 20));
			skin.add("large-font", generateFont("data/fonts/Orbitron-Medium.ttf", 26));
			skin.load(Gdx.files.internal("data/ui/uiskin.json"));
			return skin;
		}
		
		private static BitmapFont generateFont(String ttfFile, int size) {
			FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(ttfFile));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = Config.getFontSize(size);
			BitmapFont font = gen.generateFont(parameter);
			gen.dispose();
			return font;
		}
	}
}
