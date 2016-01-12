package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;

import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class PlanetData {

	public boolean hasLife;
	
	public double period; // sec
	public double radius; // m
	public double density; // kg/m^3
	public double mass; // kg
	public double temperature; // K
	public double liquidSurface; // 0.0 to 1.0 (earth = 0.75 water)

	public double atmospherePressure; // Pa
	public Map<Molecule, Double> atmosphere;
	public Map<Molecule, Double> clouds;
	
	public Color atmosphereScatterColor;
	public Color atmospherePassColor;
	public Color atmosphereFogColor;
	public double fogLevel;
	
	public void fillStandardValues(Random random) {
		if (radius == 0) {
			radius = Units.EARTH_RADIUS * random.nextDouble(0.1, 2.5);
		}
		
		if (period == 0) {
			period = Units.SECONDS_PER_HOUR * random.nextDouble(5, 40);
		}

		if (density == 0) {
			density = Units.EARTH_DENSITY * random.nextDouble(0.8, 1.2);
		}

		if (mass == 0) {
			mass = density * Units.volumeSphere(radius);
		}
		
		if (temperature == 0) {
			temperature = random.nextDouble(100, 280);
		}
		
		// atmosphere pressures
		// pluto : 1 Pa (10 microbar)
		// earth : 101.325E3 Pa
		// venus : 9.2E6 Pa
		// mars : 0.636E6 Pa
		// europa : 0.1E-6 Pa
		// titan : 146.7E3 Pa
		// triton : 1.5 Pa
		// io : 3.3E-5 to 3E-4 Pa (SO2 main, SO, NaCl, S, O)
		// enceladus : (91% water vapor, 4% nitrogen, 3.2% carbon dioxide, and 1.7% methane)

	}
}
