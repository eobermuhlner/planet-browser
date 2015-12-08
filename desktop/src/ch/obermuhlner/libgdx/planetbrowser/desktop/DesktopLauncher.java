package ch.obermuhlner.libgdx.planetbrowser.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ch.obermuhlner.libgdx.planetbrowser.Config;
import ch.obermuhlner.libgdx.planetbrowser.Config.SkinFactory;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;

public class DesktopLauncher {
	private static final boolean SCREENSHOTS = false;
	
	public static void main (String[] arg) {
		Config.skinFactory = new DesktopSkinFactory();
		Config.screenDensityFactor = SCREENSHOTS ? 1.0f : 2.0f;
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		if (SCREENSHOTS) {
			config.width = 800;
			config.height = 600;
		} else {
			config.width = 1200;
			config.height = 1000;
		}

		config.samples = 8;
		new LwjglApplication(new PlanetBrowser(), config);
	}
	
	public static class DesktopSkinFactory implements SkinFactory {
		@Override
		public Skin createSkin() {
			Skin skin = new Skin();
			skin.addRegions(new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));
			skin.add("small-font", generateFont("data/fonts/orbitron-medium.ttf", 16));
			skin.add("default-font", generateFont("data/fonts/orbitron-medium.ttf", 20));
			skin.add("large-font", generateFont("data/fonts/orbitron-medium.ttf", 26));
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
