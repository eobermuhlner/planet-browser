package ch.obermuhlner.libgdx.planetbrowser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	/**
	 * Ammonium hydrosulfide
	 */
	NH4SH("ammonium hydrosulfide"),
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
	MgO("magnesium oxide"),
	FeO("iron oxide"),
	Al2O3("aluminium oxide"),
	CaO("calcium oxide"),
	Na2O("natrium oxide"),
	K2O("kalium oxide"),
	TiO2("titanium dioxide"),
	P2O5("phosphorus pentoxide"),
	
	S2("sulfur"),
	Cl2("chlorine");
	
	private String humanName;

	Molecule(String humanName) {
		this.humanName = humanName;
	}
	
	public String getHumanName() {
		return humanName;
	}
	
	private Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
	public String toHtml() {
		StringBuilder html = new StringBuilder();
		
		String string = name();
		
		int pos = 0;
		Matcher matcher = NUMBER_PATTERN.matcher(string);
		while(matcher.find()) {
			html.append(string.substring(pos, matcher.start()));
			
			html.append("<sub>");
			html.append(matcher.group(0));
			html.append("</sub>");
			
			pos = matcher.end();
		}
		html.append(string.substring(pos));
		
		return html.toString();
	}
}
