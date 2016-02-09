package ch.obermuhlner.libgdx.planetbrowser.control;

import ch.obermuhlner.libgdx.planetbrowser.Config;

public class Ship {
	public float forwardThrust = Config.terrainZoomFactor * 200000f * Config.terrainZoomFactor * 0.001f;
	public float upThrust = Config.terrainZoomFactor * 2000f * Config.terrainZoomFactor * 0.001f;
	public float rightThrust = Config.terrainZoomFactor * 2000f * Config.terrainZoomFactor * 0.001f;
	public float rollThrust = 1f;
	public float pitchThrust = 1f;
	public float yawThrust = 1f;
	
	public float mass = 1f;
}