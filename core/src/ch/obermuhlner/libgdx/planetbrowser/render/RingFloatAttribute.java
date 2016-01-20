package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class RingFloatAttribute extends FloatAttribute {

	public static final String RingAlias = "sunNoise";
	public static final long Ring = register(RingAlias);

	public static RingFloatAttribute createRing(float value) {
		return new RingFloatAttribute(Ring, value);
	}

	private RingFloatAttribute(long type, float value) {
		super(type, value);
	}

}
