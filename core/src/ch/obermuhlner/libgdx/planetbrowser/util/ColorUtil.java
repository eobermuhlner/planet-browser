package ch.obermuhlner.libgdx.planetbrowser.util;

import com.badlogic.gdx.graphics.Color;

public class ColorUtil {

	public static Color[] randomColors(Random random, int colorCount, Color[] colors, float deltaColor, float deltaLuminance) {
		Color[] result = new Color[colorCount];
		for (int i = 0; i < result.length; i++) {
			result[i] = randomColor(random, colors, deltaColor, deltaLuminance);
		}
		return result;
	}

	public static Color randomColor(Random random, Color[] colors, float deltaColor, float deltaLuminance) {
		return randomColor(random, colors[random.nextInt(colors.length)], deltaColor, deltaLuminance);
	}
	
	public static Color randomColor(Random random, Color color, float deltaColor, float deltaLuminance) {
		float randomLuminance = random.nextFloat(1 - deltaLuminance, 1 + deltaLuminance);
		return new Color(
			MathUtil.clamp(color.r * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			MathUtil.clamp(color.g * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			MathUtil.clamp(color.b * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			1.0f);
	}
	
	public static Color randomColor(Random random) {
		return new Color(
			random.nextFloat(),
			random.nextFloat(),
			random.nextFloat(),
			1.0f
			);
	}

}
