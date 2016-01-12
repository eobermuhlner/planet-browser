package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class MoreFloatAttribute extends FloatAttribute {

	public static final String FogLevelAlias = "fogLevel";
	public static final long FogLevel = register(FogLevelAlias);

	public static final String NormalStepAlias = "normalStep";
	public static final long NormalStep = register(NormalStepAlias);
	
	public static final String BumpFactorAlias = "bumpFactor";
	public static final long BumpFactor = register(BumpFactorAlias);
	
	public static MoreFloatAttribute createFogLevel (float value) {
		return new MoreFloatAttribute(FogLevel, value);
	}

	public static MoreFloatAttribute createNormalStep (float value) {
		return new MoreFloatAttribute(NormalStep, value);
	}

	public static MoreFloatAttribute createBumpFactor (float value) {
		return new MoreFloatAttribute(BumpFactor, value);
	}

	private MoreFloatAttribute(long type, float value) {
		super(type, value);
	}

}
