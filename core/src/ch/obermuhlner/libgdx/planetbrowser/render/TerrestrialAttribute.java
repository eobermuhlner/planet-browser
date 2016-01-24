package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

import ch.obermuhlner.libgdx.planetbrowser.util.CompareUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class TerrestrialAttribute extends AbstractRandomAttribute {

	public static final String TerrestrialAlias = "terrestrial";
	public static final long Terrestrial = register(TerrestrialAlias);

	public static final String LINEAR = "h = h;";
	public static final String SMOOTH = "h = smoothstep(0.0, 1.0, h);";
	public static final String POWER_2 = "h = h * h;";
	public static final String POWER_3 = "h = h * h * h;";
	public static final String SQRT = "h = sqrt(h);";
	public static final String MID_0 = "h = abs(h - 0.5) * 2.0;";
	public static final String POWER_2_MID_0 = "h = abs((h - 0.5) * (h - 0.5)) * 2.0;";
	public static final String SQRT_MID_0 = "h = sqrt(abs(h - 0.5) * 2.0);";
	public static final String CONTINENT_POWER_2 = ""
			+ "if (h <= u_heightWater) {"
			+ "  return h;"
			+ "} else {"
			+ "  h = (h - u_heightWater) / (1.0 - u_heightWater);"
			+ "  return h * h * (1.0 - u_heightWater) + u_heightWater;"
			+ "}";
	public static final String CONTINENT_POWER_3 = ""
			+ "if (h <= u_heightWater) {"
			+ "  return h;"
			+ "} else {"
			+ "  h = (h - u_heightWater) / (1.0 - u_heightWater);"
			+ "  return h * h * h * (1.0 - u_heightWater) + u_heightWater;"
			+ "}";

	public static String functionPowerMid0(float power) {
		return "h = abs(pow(h - 0.5, " + power + ")) * 2.0;";
	}
	
	public static String functionPower(float power) {
		return "h = pow(h, " + power + ");";
	}
	
	public int heightFrequency = 5;
	public float heightMin = 0.1f;
	public float heightMax = 0.9f;
	public float heightWater = 0.0f;
	public float iceLevel = 0.0f;
	public int craterBaseGrid = 0;
	public float craterProbability = 1.0f;
	public String heightFunction = LINEAR;
	
	public Color[] planetColors;
	public int[] planetColorFrequencies = null;
	
	protected TerrestrialAttribute(long type, float[] randomValues) {
		super(type, randomValues);
	}

	@Override
	public int compareTo(Attribute o) {
		int cmp = super.compareTo(o);
		if (cmp != 0) {
			return cmp;
		}

		TerrestrialAttribute other = (TerrestrialAttribute) o;
		cmp = CompareUtil.compare(heightFrequency, other.heightFrequency);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(heightMin, other.heightMin);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(heightMax, other.heightMax);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(heightWater, other.heightWater);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(iceLevel, other.iceLevel);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(craterBaseGrid, other.craterBaseGrid);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(heightFunction, other.heightFunction);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(planetColors, other.planetColors);
		if (cmp != 0) {
			return cmp;
		}
		cmp = CompareUtil.compare(planetColorFrequencies, other.planetColorFrequencies);
		return cmp;
	}

	@Override
	public Attribute copy() {
		TerrestrialAttribute copy = new TerrestrialAttribute(type, randomValues);
		copy.heightFrequency = heightFrequency;
		copy.heightMin = heightMin;
		copy.heightMax = heightMax;
		copy.heightWater = heightWater;
		copy.iceLevel = iceLevel;
		copy.craterBaseGrid = craterBaseGrid;
		copy.craterProbability = craterProbability;
		copy.heightFunction = heightFunction;
		copy.planetColors = planetColors;
		copy.planetColorFrequencies = planetColorFrequencies;
		return copy;
	}

	public static TerrestrialAttribute createTerrestrial(Random random) {
		float[] randomValues = new float[10];
		for (int i = 0; i < randomValues.length; i++) {
			randomValues[i] = random.nextFloat();
		}

		return new TerrestrialAttribute(Terrestrial, randomValues);
	}
}
