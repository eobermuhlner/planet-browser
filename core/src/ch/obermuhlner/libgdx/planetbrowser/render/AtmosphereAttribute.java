package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class AtmosphereAttribute extends Attribute {
	public static final String AtmosphereAlias = "atmosphere";
	public static final long Atmosphere = register(AtmosphereAlias);

	public final Color centerColor;
	public final Color horizonColor;
	public final Color refractionColor;
	public final float refractionFactor;
	public final float atmosphereEnd;

	/**
	 * 
	 * @param color the atmosphere diffuse color (raleigh scattering). Blue for earth.
	 * @param refractionColor the atmosphere refraction color (mie scattering). Red for earth.
	 * @param refractionFactor the factor (strength) for the refraction. 0.5-0.6 are good values.
	 * @param atmosphereEnd the horizon magic value.
	 * 	0.8 if atmosphere is 1.01 larger than planet.
	 * 	0.7 if atmosphere is 1.02 larger than planet.
	 * 	0.5 if atmosphere is 1.04 larger than planet.
	 * 	0.3 if atmosphere is 1.10 larger than planet.
	 */
	public AtmosphereAttribute(Color color, Color refractionColor, float refractionFactor, float atmosphereEnd) {
		this(color, 0.0f, 0.4f, refractionColor, refractionFactor, atmosphereEnd);
	}

	public AtmosphereAttribute(Color color, float centerAlpha, float horizonAlpha, Color refractionColor, float refractionFactor, float atmosphereEnd) {
		this(new Color(color.r, color.g, color.b, centerAlpha),
			new Color(color.r, color.g, color.b, horizonAlpha),
			refractionColor,
			refractionFactor,
			atmosphereEnd);
	}

	public AtmosphereAttribute(Color centerColor, Color horizonColor, Color refractionColor, float refractionFactor, float atmosphereEnd) {
		super(Atmosphere);
		
		this.centerColor = centerColor;
		this.horizonColor = horizonColor;
		this.refractionColor = refractionColor;
		this.refractionFactor = refractionFactor;
		this.atmosphereEnd = atmosphereEnd;
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
			cmp = other.refractionColor.toIntBits() - refractionColor.toIntBits();
		}
		if (cmp == 0) {
			cmp = Float.compare(other.refractionFactor, refractionFactor);
		}
		if (cmp == 0) {
			cmp = Float.compare(other.atmosphereEnd, atmosphereEnd);
		}
		return cmp;
	}

	@Override
	public Attribute copy() {
		return new AtmosphereAttribute(centerColor, horizonColor, refractionColor, refractionFactor, atmosphereEnd);
	}

}
