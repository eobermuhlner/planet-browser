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
	public static final String POWER = "h = pow(h, u_heightFunctionValue);";
	public static final String SQRT = "h = sqrt(h);";
	public static final String MID_0 = "h = abs(h - 0.5) * 2.0;";
	public static final String POWER_2_MID_0 = "h = abs((h - 0.5) * (h - 0.5)) * 2.0;";
	public static final String POWER_MID_0 = "h = abs(pow(h - 0.5, u_heightFunctionValue)) * 2.0;";
	public static final String SQRT_MID_0 = "h = sqrt(abs(h - 0.5) * 2.0);";
	public static final String STEPS = "h = (floor(h * u_heightFunctionValue) + smoothstep(0.9, 1.0, fract(h * u_heightFunctionValue))) / u_heightFunctionValue;";
	public static final String CONTINENT_POWER = ""
			+ "if (h <= u_heightWater) {"
			+ "  return h;"
			+ "} else {"
			+ "  h = (h - u_heightWater) / (1.0 - u_heightWater);"
			+ "  return pow(h, u_heightFunctionValue) * (1.0 - u_heightWater) + u_heightWater;"
			+ "}";

	public static enum FractalFunction {
		SimpleWeight,
		SimpleWeightRidged,
		SignalDependentWeight,
		SignalDependentWeightRidged
	}
	
	public FractalFunction fractalFunction = FractalFunction.SignalDependentWeightRidged;
	public int fractalOctaveCount = 14;
	
	public int heightFrequency = 5;
	public float heightMin = 0.1f;
	public float heightMax = 0.9f;
	public float heightWater = 0.0f;
	public String heightFunction = LINEAR;
	public float heightFunctionValue = 1.0f;
	public float iceLevel = 0.0f;
	public int craterBaseGrid = 0;
	public float craterProbability = 1.0f;
	
	public Color[] planetColors;
	public int[] planetColorFrequencies = null;
	
	protected TerrestrialAttribute(long type, float[] randomValues) {
		super(type, randomValues);
	}

	public void setHeightFunctionPowerMid0(float power) {
		heightFunction = POWER_MID_0;
		heightFunctionValue = power;
	}
	
	public void setHeightFunctionPower(float power) {
		heightFunction = POWER;
		heightFunctionValue = power;
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
		cmp = CompareUtil.compare(heightFunctionValue, other.heightFunctionValue);
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
		copy.fractalFunction = fractalFunction;
		copy.fractalOctaveCount = fractalOctaveCount;
		copy.heightFrequency = heightFrequency;
		copy.heightMin = heightMin;
		copy.heightMax = heightMax;
		copy.heightWater = heightWater;
		copy.heightFunction = heightFunction;
		copy.heightFunctionValue = heightFunctionValue;
		copy.iceLevel = iceLevel;
		copy.craterBaseGrid = craterBaseGrid;
		copy.craterProbability = craterProbability;
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
