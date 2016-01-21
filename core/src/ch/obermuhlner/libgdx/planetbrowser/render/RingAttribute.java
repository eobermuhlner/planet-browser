package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

import ch.obermuhlner.libgdx.planetbrowser.util.CompareUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class RingAttribute extends AbstractRandomAttribute {

	public static final String RingAlias = "ring";
	public static final long Ring = register(RingAlias);

	public Color color = Color.WHITE;
	public float opacity = 0.5f;
	
	protected RingAttribute(long type, float[] randomValues) {
		super(type, randomValues);
	}

	@Override
	public int compareTo(Attribute o) {
		int cmp = super.compareTo(o);
		if (cmp != 0) {
			return cmp;
		}

		RingAttribute other = (RingAttribute) o;
		cmp = CompareUtil.compare(opacity, other.opacity);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(color, other.color);
		return cmp;
	}

	@Override
	public Attribute copy() {
		RingAttribute copy = new RingAttribute(type, randomValues);
		copy.color = color;
		return copy;
	}

	public static RingAttribute createRing(Random random) {
		float[] randomValues = new float[10];
		for (int i = 0; i < randomValues.length; i++) {
			randomValues[i] = random.nextFloat();
		}

		return new RingAttribute(Ring, randomValues);
	}
}
