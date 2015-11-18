package ch.obermuhlner.libgdx.planetbrowser;

import com.badlogic.gdx.Gdx;

public class Config {

	public static boolean useGeneratedFonts = true;
	
	public static float screenDensityFactor = 1.0f;

	public static int textureSize = 2048;

	public static int getFontSize(int theFontSize) {
		float density = Gdx.graphics.getDensity();
		return Math.round(theFontSize * density * screenDensityFactor * 0.8f);
	}

}
