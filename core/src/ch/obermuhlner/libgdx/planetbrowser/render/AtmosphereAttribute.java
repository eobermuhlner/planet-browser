package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class AtmosphereAttribute extends Attribute {
	public static final String AtmosphereAlias = "atmosphere";
	public static final long Atmosphere = register(AtmosphereAlias);

	public final Color centerColor;
	public final Color horizonColor;
	public final Color spaceColor;
	public final Color refractionColor;
	
	public AtmosphereAttribute(Color color, Color refractionColor) {
		this(color, 0.0f, 0.4f, 0.0f, refractionColor);
	}

	public AtmosphereAttribute(Color color, float centerAlpha, float horizonAlpha, float spaceAlpha, Color refractionColor) {
		this(new Color(color.r, color.g, color.b, centerAlpha),
			new Color(color.r, color.g, color.b, horizonAlpha),
			new Color(color.r, color.g, color.b, spaceAlpha),
			refractionColor);
	}

	public AtmosphereAttribute(Color centerColor, Color horizonColor, Color spaceColor, Color refractionColor) {
		super(Atmosphere);
		
		this.centerColor = centerColor;
		this.horizonColor = horizonColor;
		this.spaceColor = spaceColor;
		this.refractionColor = refractionColor;
	}

	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		AtmosphereAttribute other = (AtmosphereAttribute) o;
		
		int cmp = other.centerColor.toIntBits() - centerColor.toIntBits();
		if (cmp == 0) {
			cmp = other.horizonColor.toIntBits() - horizonColor.toIntBits();
		}
		if (cmp == 0) {
			cmp = other.spaceColor.toIntBits() - spaceColor.toIntBits();
		}
		if (cmp == 0) {
			cmp = other.refractionColor.toIntBits() - refractionColor.toIntBits();
		}
		return cmp;
	}

	@Override
	public Attribute copy() {
		return new AtmosphereAttribute(centerColor, horizonColor, spaceColor, refractionColor);
	}

}
