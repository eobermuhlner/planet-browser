package ch.obermuhlner.libgdx.planetbrowser.render;

public class TerrestrialHeightShaderFunctionAttribute extends ShaderFunctionAttribute {
	public static final String TerrestrialHeightFunctionAlias = "heightFunction";
	public static final long TerrestrialHeightFunction = register(TerrestrialHeightFunctionAlias);

	public static final String LINEAR = "h = h;";
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

	public static String functionPowerMid0(float power) {
		return "h = abs(pow(h - 0.5, " + power + ")) * 2.0;";
	}
	
	public static String functionPower(float power) {
		return "h = pow(h, " + power + ");";
	}
	
	public TerrestrialHeightShaderFunctionAttribute(String code) {
		super(TerrestrialHeightFunction, code);
	}

}
