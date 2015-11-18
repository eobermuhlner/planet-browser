package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;

public class Jupiter extends AbstractGasPlanet {

	private static final Color[] GAS_PLANET_COLORS = new Color[] {
			new Color(0.3333f, 0.2222f, 0.1111f, 1.0f),
			new Color(0.8555f, 0.8125f, 0.7422f, 1.0f),
			new Color(0.4588f, 0.4588f, 0.4297f, 1.0f),
			new Color(0.5859f, 0.3906f, 0.2734f, 1.0f),
	};

	public Jupiter() {
		super(GAS_PLANET_COLORS);
	}

	protected float getPlanetRadius() {
		return 3.0f;
	}

}
