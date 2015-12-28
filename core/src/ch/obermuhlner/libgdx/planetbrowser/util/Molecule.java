package ch.obermuhlner.libgdx.planetbrowser.util;

import ch.obermuhlner.libgdx.planetbrowser.ui.SimpleHtml;

public enum Molecule {
	H2("hydrogen"),
	H2O("water"),
	H2S("hydrogen sulfide"),
	HCl("hydrogen chloride"),
	HF("hydrogen fluoride"),
	He("helium"),
	Ar("argon"),
	Ne("neon"),
	Kr("krypton"),
	Xe("xenon"),
	Ni("nickel"),
	Fe("iron"),
	O2("oxygen"),
	N2("nitrogen"),
	NH3("ammonia"),
	NH4SH("ammonium hydrosulfide"),
	N2H8S("ammonium sulfide", "(NH4)2S"),
	N2H4("hydrazine"),
	H2SO4("sulphuric acid"),
	S_2("sulfide", "S_-2", "S<sup>2-</sup>"),
	Cl_("chloride", "Cl_-", "Cl<sup>-</sup>"),
	/**
	Density: 1.15 kg/m
	Formula: CO
	Boiling point: -191.5 C
	Molar mass: 28.01 g/mol
	Melting point: -205 C
	 */
	CO("carbon monoxide"),
	/**
	Formula: CO2
	Boiling point: -78.5 C
	Melting point: -55.6 C
	Molar mass: 44.01 g/mol
	*/
	CO2("carbon dioxide"),
	CH4("methane"),
	C2H6("ethane"),
	C2H4("ethylene"),
	C2H2("acetylene"),
	HCN("hydrogene cyanide"),
	SO2("sulfur dioxide"),
	PH3("phosphine"),
	
	SiO2("silicon dioxide"),
	SiO4_4("orthosilicate", "SiO4_-4", "SiO<sub>4</sub><sup>-4</sup>"),
	MgO("magnesium oxide"),
	FeO("iron oxide"),
	Al2O3("aluminium oxide"),
	CaO("calcium oxide"),
	Na2O("natrium oxide"),
	K2O("kalium oxide"),
	TiO("titanium oxide"),
	TiO2("titanium dioxide"),
	VO("vanadium monoxide"),
	VO2("vanadium dioxide"),
	V2O3("vanadium trioxide"),
	V2O5("vanadium pentoxide"),
	P2O5("phosphorus pentoxide"),
	
	Na("sodium"),
	K("potassium"),
	S2("sulfur"),
	Cl2("chlorine");
	
	private String humanName;
	private String formula;
	private String htmlFormula;

	Molecule(String humanName) {
		this(humanName, null, null);
	}

	Molecule(String humanName, String formula) {
		this(humanName, formula, null);
	}
	
	Molecule(String humanName, String formula, String htmlFormula) {
		this.humanName = humanName;
		this.formula = formula;
		this.htmlFormula = htmlFormula;
	}

	public String getHumanName() {
		return humanName;
	}
	
	public String getFormula() {
		return formula != null ? formula : name();
	}
	
	public String getHtmlFormula() {
		return htmlFormula != null ? htmlFormula : SimpleHtml.moleculeToHtml(getFormula());
	}
}
