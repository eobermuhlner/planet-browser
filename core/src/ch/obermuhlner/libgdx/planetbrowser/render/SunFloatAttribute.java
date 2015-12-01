package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class SunFloatAttribute extends FloatAttribute {

	public static final String SunNoiseAlias = "sunNoise";
	public static final long SunNoise = register(SunNoiseAlias);

	public static SunFloatAttribute createSunNoise (float value) {
		return new SunFloatAttribute(SunNoise, value);
	}

	private SunFloatAttribute(long type, float value) {
		super(type, value);
	}

}
