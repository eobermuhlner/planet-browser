package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.MathUtils;

public class FloatArrayAttribute extends Attribute {

	public final static String FloatArrayAlias = "floatArray";
	public final static long FloatArray = register(FloatArrayAlias);

	public final float[] values;

	public FloatArrayAttribute (long type, float... floats) {
		super(type);
		this.values = floats;
	}
	
	@Override
	public Attribute copy () {
		return new FloatArrayAttribute(type, values);
	}

	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		
		FloatArrayAttribute other = (FloatArrayAttribute)o;
		if (other.values.length != values.length) {
			return other.values.length - values.length;
		}
		for (int i = 0; i < values.length; i++) {
			float thisValue = values[i];
			float otherValue = other.values[i];
			if (!MathUtils.isEqual(thisValue, otherValue)) {
				return  thisValue < otherValue ? -1 : 1;
			}
		}
		return 0;
	}
}
