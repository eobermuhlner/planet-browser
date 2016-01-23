package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.Color;

import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Jupiter extends AbstractGasPlanet {

	private static final Color[] GAS_PLANET_COLORS = new Color[] {
			new Color(0.3333f, 0.2222f, 0.1111f, 1.0f),
			new Color(0.8555f, 0.8125f, 0.7422f, 1.0f),
			new Color(0.4588f, 0.4588f, 0.4297f, 1.0f),
			new Color(0.5859f, 0.3906f, 0.2734f, 1.0f),
	};

	public Jupiter() {
		super(GAS_PLANET_COLORS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.radius = Units.JUPITER_RADIUS * random.nextDouble(0.5, 2.5);
		planetData.density = Units.JUPITER_DENSITY * random.nextDouble(0.8, 1.2);
		boolean hot = random.nextBoolean(0.5);
		planetData.temperature = hot ? random.nextDouble(150, 1600) : random.nextDouble(100, 150);

		planetData.atmosphere = random.nextProbabilityMap(
				p(random.nextGaussian(90), Molecule.H2),
				p(random.nextGaussian(10), Molecule.He),
				p(random.nextGaussian(0.3), Molecule.CH4),
				p(random.nextGaussian(0.003), Molecule.NH3),
				p(random.nextGaussian(0.0006), Molecule.C2H6)
				);

		planetData.atmosphereScatterColor = ColorUtil.randomColor(random, random.next(GAS_PLANET_COLORS), 0.2f, 0.2f);
		planetData.atmospherePassColor = ColorUtil.randomColor(random, new Color(0xf26666ff), 0.2f, 0.2f);
		planetData.fogLevel = random.nextDouble(0.0005, 0.001);
		//planetData.fogLevel = 0.0;
		
		// see: https://en.wikipedia.org/wiki/Sudarsky%27s_gas_giant_classification
		
		if (planetData.temperature < 150) {
			// Sudarsky Type 1 (Ammonia clouds)
			//Jupiter
			//The upper clouds, located in the pressure range 0.6–0.9 bar, are made of ammonia ice.
			//Below these ammonia ice clouds, denser clouds made of ammonium hydrosulfide or ammonium sulfide (between 1–2 bar) and water (3–7 bar) are thought to exist
			planetData.clouds = random.nextProbabilityMap(
					p(random.nextDouble(10, 40), Molecule.NH3),
					p(random.nextDouble(10, 40), Molecule.NH4SH),
					p(random.nextDouble(10, 40), Molecule.N2H8S),
					p(random.nextDouble(5, 20), Molecule.H2O)
					);		
		} else if (planetData.temperature < 250) {
			// Sudarsky Type 2 (Water clouds)
			planetData.clouds = random.nextProbabilityMap(
					p(random.nextDouble(0, 5), Molecule.NH3),
					p(random.nextDouble(0, 5), Molecule.NH4SH),
					p(random.nextDouble(0, 5), Molecule.N2H8S),
					p(random.nextDouble(20, 40), Molecule.H2O)
					);		
			
		} else if (planetData.temperature < 700) {
			// Sudarsky Type 3 (350K - 800K) (Cloudless)
			// featureless blue (similar Neptune)
		} else if (planetData.temperature < 800) {
			// Sudarsky Type 3 (700K - 800K) (Cloudless + cirrus)
			// featureless blue (similar Neptune)
			// Above 700 K (800 °F, 430 °C), sulfides and chlorides might provide cirrus-like clouds
			planetData.clouds = random.nextProbabilityMap(
					p(random.nextDouble(10, 40), Molecule.S_2),
					p(random.nextDouble(10, 40), Molecule.Cl_)
					);		
		} else if (planetData.temperature < 1400) {
			// Sudarsky Type 4 (900K - 1400K) (Alkalimetal clouds)
			// sodium, potassium
			// maybe sometimes titanium/vanadium oxide
			boolean isSubtypeTiVo = random.nextBoolean(0.2);
			planetData.clouds = random.nextProbabilityMap(
					p(random.nextDouble(10, 40), Molecule.Na),
					p(random.nextDouble(10, 40), Molecule.K),
					p(isSubtypeTiVo ? random.nextDouble(0, 10) : 0, Molecule.TiO),
					p(isSubtypeTiVo ? random.nextDouble(0, 5) : 0, Molecule.TiO2),
					p(isSubtypeTiVo ? random.nextDouble(0, 10) : 0, Molecule.VO),
					p(isSubtypeTiVo ? random.nextDouble(0, 5) : 0, Molecule.VO2)
					);		
			
		} else {
			// Sudarsky Type 5 (>1400K) (Silicate clouds)
			planetData.clouds = random.nextProbabilityMap(
					p(random.nextDouble(10, 40), Molecule.SiO2),
					p(random.nextDouble(10, 40), Molecule.SiO4_4),
					p(random.nextDouble(10, 40), Molecule.Fe)
					);		
		}
		
		planetData.fillStandardValues(random);
		
		return planetData;
	}

	protected float getPlanetRadius() {
		return 3.0f;
	}

}
