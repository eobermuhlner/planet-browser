package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;

public class Neptune extends AbstractGasPlanet {

	private static final Color[] ICE_GAS_PLANET_COLORS = new Color[] {
			new Color(0.6094f, 0.6563f, 0.7695f, 1.0f),
			new Color(0.5820f, 0.6406f, 0.6406f, 1.0f),
			new Color(0.2695f, 0.5234f, 0.9102f, 1.0f),
			new Color(0.3672f, 0.4609f, 0.7969f, 1.0f),
			new Color(0.7344f, 0.8594f, 0.9102f, 1.0f),
		};

	public Neptune() {
		super(ICE_GAS_PLANET_COLORS);
	}

	protected float getPlanetRadius() {
		return 2.0f;
	}
}
