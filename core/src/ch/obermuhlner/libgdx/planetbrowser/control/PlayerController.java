package ch.obermuhlner.libgdx.planetbrowser.control;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntIntMap;

public class PlayerController extends InputAdapter {

	private static final int THRUST_FORWARD = Keys.W;
	private static final int THRUST_BACKWARD = Keys.S;

	private static final int THRUST_LEFT = Keys.Q;
	private static final int THRUST_RIGHT = Keys.E;

	private static final int THRUST_UP = Keys.R;
	private static final int THRUST_DOWN = Keys.F;

	private static final int ROLL_LEFT = Keys.LEFT;
	private static final int ROLL_RIGHT = Keys.RIGHT;

	private static final int PITCH_UP = Keys.UP;
	private static final int PITCH_DOWN = Keys.DOWN;

	private static final int YAW_LEFT = Keys.A;
	private static final int YAW_RIGHT = Keys.D;

	private final Player player;

	private final IntIntMap keys = new IntIntMap();

	private float power = 2.0f;
	
	private int rollLeft = ROLL_LEFT;
	private int rollRight = ROLL_RIGHT;
	
	private int pitchUp = PITCH_UP;
	private int pitchDown = PITCH_DOWN;
	
	public PlayerController (Player player) {
		this.player = player;
	}

	@Override
	public boolean keyDown (int keycode) {
		keys.put(keycode, keycode);
		if (keycode == THRUST_FORWARD || keycode == THRUST_BACKWARD) {
			player.thrustForwardThrottle.start();
		}
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		keys.remove(keycode, 0);
		if (keycode == THRUST_FORWARD || keycode == THRUST_BACKWARD) {
			player.thrustForwardThrottle.stop();
		}
		return true;
	}

	public void update (float deltaTime) {
		player.thrustForwardThrottle.setThrottle(delta(THRUST_FORWARD, THRUST_BACKWARD));
		player.thrustForwardThrottle.update(deltaTime);
		
		player.addThrustRight(calculateDelta(THRUST_RIGHT, THRUST_LEFT, player.thrustRight, deltaTime));
		player.addThrustUp(calculateDelta(THRUST_UP, THRUST_DOWN, player.thrustUp, deltaTime));

		player.addRoll(calculateDelta(rollRight, rollLeft, player.roll, deltaTime));
		player.addPitch(calculateDelta(pitchUp, pitchDown, player.pitch, deltaTime));
		player.addYaw(calculateDelta(YAW_RIGHT, YAW_LEFT, player.yaw, deltaTime));
	}

	private float delta(int keyPlus, int keyMinus) {
		if (keys.containsKey(keyPlus)) {
			return 1.0f;
		} else if (keys.containsKey(keyMinus)) {
			 return -1.0f;
		}
		return 0.0f;
	}
	
	private float calculateDelta(int keyPlus, int keyMinus, float currentValue, float deltaTime) {
		float value;
		if (keys.containsKey(keyPlus)) {
			value = 1.0f;
		} else if (keys.containsKey(keyMinus)) {
			value = -1.0f;
		} else {
			value = -currentValue * 2;
		}
		
		return value * power * deltaTime;
	}
	
}
