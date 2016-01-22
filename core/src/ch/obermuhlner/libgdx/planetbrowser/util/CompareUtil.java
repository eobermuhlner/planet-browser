package ch.obermuhlner.libgdx.planetbrowser.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class CompareUtil {

	public static int compare(Color value, Color other) {
		return value.toIntBits() - other.toIntBits();
	}
	
	public static int compare(float value, float other) {
		return MathUtils.isEqual(value, other) ? 0 : value < other ? -1 : 1;
	}
	
    public static int compare(long value, long other) {
        return (value < other) ? -1 : ((value == other) ? 0 : 1);
    }

	public static int compare(float[] array, float[] other) {
		if (array == other) {
			return 0;
		}
		if (other.length != array.length) {
			return other.length - array.length;
		}
		for (int i = 0; i < array.length; i++) {
			float thisValue = array[i];
			float otherValue = other[i];
			if (!MathUtils.isEqual(thisValue, otherValue)) {
				return  thisValue < otherValue ? -1 : 1;
			}
		}
		return 0;
	}

	public static int compare(Color[] array, Color[] other) {
		if (array == other) {
			return 0;
		}
		if (other.length != array.length) {
			return other.length - array.length;
		}
		for (int i = 0; i < array.length; i++) {
			int cmp = compare(array[i], other[i]);
			if (cmp != 0) {
				return cmp;
			}
		}
		return 0;
	}
}
