package ch.obermuhlner.libgdx.planetbrowser.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ch.obermuhlner.libgdx.planetbrowser.Config;

public class GameSkin {

	static Skin skin;
	
	public static Skin getSkin() {
		if (skin == null) {
			skin = new Skin();
			skin.addRegions(new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));
			if (Config.useGeneratedFonts) {
				skin.add("small-font", generateFont("data/fonts/orbitron-medium.ttf", 16));
				skin.add("default-font", generateFont("data/fonts/orbitron-medium.ttf", 20));
				skin.add("large-font", generateFont("data/fonts/orbitron-medium.ttf", 26));
			} else {
				skin.add("small-font", new BitmapFont());
				skin.add("default-font", new BitmapFont());
				skin.add("large-font", new BitmapFont());
			}
			skin.load(Gdx.files.internal("data/ui/uiskin.json"));			
		}
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
