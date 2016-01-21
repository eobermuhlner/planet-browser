package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Attribute;

import ch.obermuhlner.libgdx.planetbrowser.util.CompareUtil;

public abstract class AbstractRandomAttribute extends Attribute {

	public final float[] randomValues;

	protected AbstractRandomAttribute(long type, float[] randomValues) {
		super(type);
		this.randomValues = randomValues;
	}

	@Override
	public int compareTo(Attribute o) {
		int cmp = CompareUtil.compare(type, o.type);
		if (cmp != 0) {
			return cmp;
		}

		AbstractRandomAttribute other = (AbstractRandomAttribute) o;
		cmp = CompareUtil.compare(randomValues, other.randomValues);
		return cmp;
	}
}
