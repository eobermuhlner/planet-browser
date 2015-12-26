package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Mars extends AbstractRockyPlanet {

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(250, 300);

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
		} else {
			// titan-line atmosphere
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
		}
		
		planetData.fillStandardValues(random);
		
		return planetData;
	}
}
