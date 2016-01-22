package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

import ch.obermuhlner.libgdx.planetbrowser.util.CompareUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class GasPlanetAttribute extends AbstractRandomAttribute {

	public static final String GasPlanetAlias = "gasPlanet";
	public static final long GasPlanet = register(GasPlanetAlias);

	public Color[] planetColors;// = { Color.FIREBRICK, Color.WHITE, Color.BROWN};
	
	protected GasPlanetAttribute(long type, float[] randomValues) {
		super(type, randomValues);
	}

	@Override
	public int compareTo(Attribute o) {
		int cmp = super.compareTo(o);
		if (cmp != 0) {
			return cmp;
		}

		GasPlanetAttribute other = (GasPlanetAttribute) o;
		cmp = CompareUtil.compare(planetColors, other.planetColors);
		return cmp;
	}

	@Override
	public Attribute copy() {
		GasPlanetAttribute copy = new GasPlanetAttribute(type, randomValues);
		copy.planetColors = planetColors;
		return copy;
	}
	
	public static GasPlanetAttribute createGasPlanet(Random random) {
		float[] randomValues = new float[10];
		for (int i = 0; i < randomValues.length; i++) {
			randomValues[i] = random.nextFloat();
		}

		return new GasPlanetAttribute(GasPlanet, randomValues);
	}
}
