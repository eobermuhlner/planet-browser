package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Mars extends AbstractRockyPlanet {

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(50, 400);
		
		if (planetData.temperature > 60) {
			if (random.nextBoolean(0.7)) {
				// mars/venus-like atmosphere
				planetData.atmosphere = random.nextProbabilityMap(
						p(random.nextGaussian(95), Molecule.CO2),
						p(random.nextGaussian(3), Molecule.N2),
						p(random.nextGaussian(0.001), Molecule.Ar),
						p(random.nextGaussian(0.001), Molecule.O2),
						p(random.nextGaussian(0.0001), Molecule.SO2),
						// undocumented molecules - just for fun
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.H2S),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.HCl),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.CH4),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.H2O),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.0001) : 0.0, Molecule.Kr),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.0001) : 0.0, Molecule.Ne)
						);

				if (planetData.temperature > 300) {
					//Venus
					planetData.clouds = random.nextProbabilityMap(
							p(random.nextDouble(10, 40), Molecule.SO2),
							p(random.nextDouble(5, 20), Molecule.H2SO4)
							);		
				} else {
					//Mars
					planetData.clouds = random.nextProbabilityMap(
							p(random.nextDouble(10, 40), Molecule.H2O),
							p(random.nextDouble(10, 40), Molecule.CO2)
							);		
				}
				
			} else {
				// titan-like atmosphere
				planetData.atmosphere = random.nextProbabilityMap(
						p(random.nextGaussian(98), Molecule.N2),
						p(random.nextGaussian(1), Molecule.CH4),
						p(random.nextGaussian(0.5), Molecule.H2),
						// undocumented molecules - just for fun
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.H2S),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.HCl),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.CH4),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.001) : 0.0, Molecule.H2O),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.0001) : 0.0, Molecule.Kr),
						p(random.nextBoolean(0.1) ? random.nextGaussian(0.0001) : 0.0, Molecule.Ne)
						);
				
				//Titan
				planetData.clouds = random.nextProbabilityMap(
						p(random.nextDouble(10, 40), Molecule.CH4),
						p(random.nextDouble(10, 40), Molecule.HCN)
						);		

			}
		} else {
			// pluto-like atmosphere
			planetData.atmosphere = random.nextProbabilityMap(
					p(random.nextGaussian(99.7), Molecule.N2),
					p(random.nextGaussian(0.25), Molecule.CH4),
					p(random.nextGaussian(0.02), Molecule.CO),
					p(random.nextGaussian(0.0002), Molecule.C2H6),
					p(random.nextGaussian(0.0001), Molecule.C2H4),
					p(random.nextGaussian(0.0003), Molecule.C2H2)
					);
		}
		
		planetData.atmosphereScatterColor = randomColor(random, ATMOSPHERE_COLORS, 0.2f, 0.2f);
		planetData.atmospherePassColor = randomColor(random, ATMOSPHERE_COLORS, 0.2f, 0.2f);

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	@Override
	public Array<Attribute> createMaterialAttributes(Random random, PlanetData planetData, DisposableContainer disposables, float xFrom, float xTo, float yFrom, float yTo, int textureSize) {
		// make sure Mars looks different from Moon
		random.nextFloat();
		
		return super.createMaterialAttributes(random, planetData, disposables, xFrom, xTo, yFrom, yTo, textureSize);
	}
}
