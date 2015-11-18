package ch.obermuhlner.libgdx.planetbrowser.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1200;
		config.height = 1000;
		config.samples = 4;
		new LwjglApplication(new PlanetBrowser(), config);
	}
}
