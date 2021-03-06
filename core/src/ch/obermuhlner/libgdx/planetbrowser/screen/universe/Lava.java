package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute.FractalFunction;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Lava extends AbstractPlanet {

	private static final Color[] LAVA_COLORS = new Color[] {
			new Color(0xff0000ff), // red
			new Color(0xee2200ff), // red-orange
			new Color(0xff5500ff), // orange
			new Color(0.30f, 0.30f, 0.30f, 1.0f),
			new Color(0.20f, 0.20f, 0.20f, 1.0f),
			new Color(0.15f, 0.15f, 0.15f, 1.0f),
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(Units.celsiusToKelvin(500), Units.celsiusToKelvin(1200)); // lava liquid 700 - 1200 C

		planetData.atmosphere = random.nextProbabilityMap(
				p(random.nextGaussian(50), Molecule.N2),
				p(random.nextGaussian(20), Molecule.CO2),
				p(random.nextGaussian(10.0), Molecule.SO2),
				p(random.nextGaussian(8.0), Molecule.NH3),
				p(random.nextGaussian(8.0), Molecule.CH4),
				p(random.nextGaussian(5.0), Molecule.CO),
				p(random.nextGaussian(2.0), Molecule.S2),
				p(random.nextGaussian(2.0), Molecule.H2O),
				p(random.nextGaussian(1.0), Molecule.Cl2),
				p(random.nextGaussian(0.2), Molecule.He),
				p(random.nextGaussian(0.1), Molecule.H2),
				p(random.nextGaussian(0.01), Molecule.Ar)
				);

		
		planetData.atmosphereScatterColor = ColorUtil.randomColor(random, new Color(0xb0a580ff), 0.1f, 0.1f);
		planetData.atmospherePassColor = ColorUtil.randomColor(random, new Color(0xf26666ff), 0.1f, 0.1f);

		planetData.fogLevel = random.nextDouble(0.0001, 0.001);

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	@Override
	protected long getTextureTypes(PlanetData planetData) {
		return TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Emissive;
	}
	
	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize, DisposableContainer disposables) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		float heightFlatGround = random.nextFloat(0.0f, 0.5f);
		float heightFunctionValue = random.nextBoolean(0.5) ? random.nextFloat(0.5f, 0.8f) : random.nextFloat(0.5f, 3.0f); // 0.7 gives nice rivers of lava with POWER_MID_0
		@SuppressWarnings("unchecked")
		String heightFunction = random.nextProbability(
				p(3, TerrestrialAttribute.POWER),
				p(10, TerrestrialAttribute.POWER_MID_0)
				);
		@SuppressWarnings("unchecked")
		FractalFunction fractalFunction = random.nextProbability(
				p(2, TerrestrialAttribute.FractalFunction.SignalDependentWeight),
				p(10, TerrestrialAttribute.FractalFunction.SignalDependentWeightRidged),
				p(heightFunction.equals(TerrestrialAttribute.POWER_MID_0) ? 40 : 1, TerrestrialAttribute.FractalFunction.SimpleWeight),
				p(4, TerrestrialAttribute.FractalFunction.SimpleWeightRidged)
				);

		TerrestrialAttribute terrestrialAttribute = TerrestrialAttribute.createTerrestrial(random);
		terrestrialAttribute.fractalFunction = fractalFunction;
		terrestrialAttribute.heightMin = 0.0f;
		terrestrialAttribute.heightMax = 1.0f;
		terrestrialAttribute.heightWater = heightFlatGround;
		terrestrialAttribute.heightFrequency = random.nextInt(2, 4);
		terrestrialAttribute.heightFunction = heightFunction;
		terrestrialAttribute.heightFunctionValue = heightFunctionValue;
		terrestrialAttribute.planetColors = LAVA_COLORS;
		terrestrialAttribute.planetColorFrequencies = createPlanetColorFrequencies(random);

		if (random.nextBoolean(0.6)) {
			terrestrialAttribute.craterBaseGrid = random.nextInt(5, 15);
			terrestrialAttribute.craterProbability = random.nextFloat(0.0f, 0.7f);			
		}

		materialAttributes.add(terrestrialAttribute);
		
		Material material = new Material(materialAttributes);

		return createTextures(disposables, material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
