package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class TerrestrialPlanetFloatAttribute extends FloatAttribute {

	public static final String HeightMinAlias = "heightMin";
	public static final long HeightMin = register(HeightMinAlias);

	public static final String HeightMaxAlias = "heightMax";
	public static final long HeightMax = register(HeightMaxAlias);

	public static final String HeightFrequencyAlias = "heightFrequency";
	public static final long HeightFrequency = register(HeightFrequencyAlias);

	public static final String HeightWaterAlias = "heightWater";
	public static final long HeightWater = register(HeightWaterAlias);

	public static final String IceLevelAlias = "iceLevel";
	public static final long IceLevel = register(IceLevelAlias);

	public static final String ColorNoiseAlias = "colorNoise";
	public static final long ColorNoise = register(ColorNoiseAlias);

	public static final String ColorFrequencyAlias = "colorFrequency";
	public static final long ColorFrequency = register(ColorFrequencyAlias);

	public static final String HeightMountainsAlias = "heightMountains";
	public static final long HeightMountains = register(HeightMountainsAlias);

	public static final String CreateTextureAlias = "createTexture";
	public static final long CreateTexture = register(CreateTextureAlias);

	public static final int CREATE_BUMP_TEXTURE = 1;
	public static final int CREATE_DIFFUSE_TEXTURE = 2;
	public static final int CREATE_NORMAL_TEXTURE = 4;
	public static final int CREATE_SPECULAR_TEXTURE = 8;
	public static final int CREATE_EMISSIVE_TEXTURE = 16;
	
	public static TerrestrialPlanetFloatAttribute createHeightMin (float value) {
		return new TerrestrialPlanetFloatAttribute(HeightMin, value);
	}

	public static TerrestrialPlanetFloatAttribute createHeightMax (float value) {
		return new TerrestrialPlanetFloatAttribute(HeightMax, value);
	}

	public static TerrestrialPlanetFloatAttribute createHeightFrequency (int value) {
		return new TerrestrialPlanetFloatAttribute(HeightFrequency, value);
	}

	public static TerrestrialPlanetFloatAttribute createHeightWater (float value) {
		return new TerrestrialPlanetFloatAttribute(HeightWater, value);
	}

	public static TerrestrialPlanetFloatAttribute createIceLevel (float value) {
		return new TerrestrialPlanetFloatAttribute(IceLevel, value);
	}

	public static TerrestrialPlanetFloatAttribute createColorNoise (float value) {
		return new TerrestrialPlanetFloatAttribute(ColorNoise, value);
	}

	public static TerrestrialPlanetFloatAttribute createColorFrequency (int value) {
		return new TerrestrialPlanetFloatAttribute(ColorFrequency, value);
	}

	public static TerrestrialPlanetFloatAttribute createHeightMountains (float value) {
		return new TerrestrialPlanetFloatAttribute(HeightMountains, value);
	}

	public static TerrestrialPlanetFloatAttribute createCreateBump () {
		return new TerrestrialPlanetFloatAttribute(CreateTexture, CREATE_BUMP_TEXTURE);
	}

	public static TerrestrialPlanetFloatAttribute createCreateDiffuse () {
		return new TerrestrialPlanetFloatAttribute(CreateTexture, CREATE_DIFFUSE_TEXTURE);
	}

	public static TerrestrialPlanetFloatAttribute createCreateNormal () {
		return new TerrestrialPlanetFloatAttribute(CreateTexture, CREATE_NORMAL_TEXTURE);
	}
	
	public static TerrestrialPlanetFloatAttribute createCreateSpecular () {
		return new TerrestrialPlanetFloatAttribute(CreateTexture, CREATE_SPECULAR_TEXTURE);
	}

	public static TerrestrialPlanetFloatAttribute createCreateEmissive () {
		return new TerrestrialPlanetFloatAttribute(CreateTexture, CREATE_EMISSIVE_TEXTURE);
	}

	public static TerrestrialPlanetFloatAttribute createTextures(boolean bump, boolean diffuse, boolean normal, boolean specular, boolean emissive) {
		int value = 0;
		if (bump) {
			value |= CREATE_BUMP_TEXTURE;
		}
		if (diffuse) {
			value |= CREATE_DIFFUSE_TEXTURE;
		}
		if (normal) {
			value |= CREATE_NORMAL_TEXTURE;
		}
		if (specular) {
			value |= CREATE_SPECULAR_TEXTURE;
		}
		if (emissive) {
			value |= CREATE_EMISSIVE_TEXTURE;
		}
		
		return new TerrestrialPlanetFloatAttribute(CreateTexture, value);
	}

	private TerrestrialPlanetFloatAttribute(long type, float value) {
		super(type, value);
	}

}
