package ch.obermuhlner.libgdx.planetbrowser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Options {

	private static final String GENERATED_TEXTURES_SIZE = "generatedTexturesSize";
	private static final String SPHERE_DIVISIONS = "sphereDivisions";
	private static final String USE_MULTI_TEXTURE_RENDERING = "useMultiTextureRendering";
	
	private final Preferences optionPreferences = Gdx.app.getPreferences(Options.class.getName());
	
	public int getGeneratedTexturesSize() {
		return optionPreferences.getInteger(GENERATED_TEXTURES_SIZE, 1024);
	}
	
	public void setGeneratedTexturesSize(int generatedTexturesSize) {
		optionPreferences.putInteger(GENERATED_TEXTURES_SIZE, generatedTexturesSize);
	}
	
	public int getSphereDivisions() {
		return optionPreferences.getInteger(SPHERE_DIVISIONS, 40);
	}
	
	public void setSphereDivisions(int sphereDivisions) {
		optionPreferences.putInteger(SPHERE_DIVISIONS, sphereDivisions);
	}
	
	public boolean getUseMultiTextureRendering() {
		return optionPreferences.getBoolean(USE_MULTI_TEXTURE_RENDERING, false);
	}
	
	public void setMultiTextureRendering(boolean multiTextureRendering) {
		optionPreferences.putBoolean(USE_MULTI_TEXTURE_RENDERING, multiTextureRendering);
	}
	
	public void save() {
		optionPreferences.flush();
	}
	
	public void reset() {
		optionPreferences.clear();
		optionPreferences.flush();
	}
}
