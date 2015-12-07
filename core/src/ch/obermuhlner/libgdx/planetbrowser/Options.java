package ch.obermuhlner.libgdx.planetbrowser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Options {

	private static final String GENERATED_TEXTURES_SIZE = "generatedTexturesSize";

	private final Preferences optionPreferences = Gdx.app.getPreferences(Options.class.getName());
	
	public int getGeneratedTexturesSize() {
		return optionPreferences.getInteger(GENERATED_TEXTURES_SIZE, 1024);
	}
	
	public void setGeneratedTexturesSize(int generatedTexturesSize) {
		optionPreferences.putInteger(GENERATED_TEXTURES_SIZE, generatedTexturesSize);
	}
	
	public void save() {
		optionPreferences.flush();
	}
	
	public void reset() {
		optionPreferences.clear();
		optionPreferences.flush();
	}
}
