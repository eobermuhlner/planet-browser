package ch.obermuhlner.libgdx.planetbrowser.control;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;

public class Player {
	public PerspectiveCamera camera;
	
	public float velocity;

	public Ship ship;
	public Throttle thrustForwardThrottle = new Throttle();
	//public float thrustForward = 0;
	public float thrustUp = 0;
	public float thrustRight = 0;

	public float pitch = 0;
	public float roll = 0;
	public float yaw = 0;

	// optimization: temporary variable
	private final Vector3 vec3 = new Vector3();

	public Player (Ship ship, PerspectiveCamera camera) {
		this.ship = ship;
		this.camera = camera;
		
		setStandardVelocity();
	}

	public void addThrustForward(float value) {
		//thrustForward = MathUtil.maybeZero(MathUtil.clamp(thrustForward + value, -1.0f, 1.0f));
	}

	public void addThrustUp(float value) {
		thrustUp = MathUtil.maybeZero(MathUtil.clamp(thrustUp + value, -1.0f, 1.0f));
	}

	public void addThrustRight(float value) {
		thrustRight = MathUtil.maybeZero(MathUtil.clamp(thrustRight + value, -1.0f, 1.0f));
	}

	public void addPitch(float value) {
		pitch = MathUtil.maybeZero(MathUtil.clamp(pitch + value, -1.0f, 1.0f));
	}

	public void addRoll(float value) {
		roll = MathUtil.maybeZero(MathUtil.clamp(roll + value, -1.0f, 1.0f));
	}

	public void addYaw(float value) {
		yaw = MathUtil.maybeZero(MathUtil.clamp(yaw + value, -1.0f, 1.0f));
	}

	public void update (float deltaTime) {
		float mass = ship.mass;
		float maxThrustForward = ship.forwardThrust / mass;
		float maxThrustRight = ship.rightThrust / mass;
		float maxThrustUp = ship.upThrust / mass;
		float maxThrustRoll = ship.rollThrust / mass;
		float maxThrustPitch = ship.pitchThrust / mass;
		float maxThrustYaw = ship.yawThrust / mass;
		
		boolean updateNeeded = false;
		
		if (thrustRight != 0) {
			vec3.set(camera.direction).crs(camera.up).scl(deltaTime * thrustRight * velocity * maxThrustRight);
			camera.position.add(vec3);
			updateNeeded = true;
		}
		if (thrustForwardThrottle.value != 0) {
			vec3.set(camera.direction).scl(deltaTime * thrustForwardThrottle.value * velocity * maxThrustForward);
			camera.position.add(vec3);
			updateNeeded = true;
		}
		if (thrustUp != 0) {
			vec3.set(camera.up).scl(deltaTime * thrustUp * velocity * maxThrustUp);
			camera.position.add(vec3);
			updateNeeded = true;
		}
		
		float rotateAngle = 90;
		if (yaw != 0) {
			camera.rotate(camera.up, deltaTime * -yaw * maxThrustYaw * rotateAngle);
			updateNeeded = true;
		}
		if (pitch != 0) {
			vec3.set(camera.direction).crs(camera.up).nor();
			camera.rotate(vec3, deltaTime * pitch * maxThrustPitch * rotateAngle);
			updateNeeded = true;
		}
		if (roll != 0) {
			vec3.set(camera.direction);
			camera.rotate(vec3, deltaTime * roll * maxThrustRoll * rotateAngle);
			updateNeeded = true;
		}

		if (updateNeeded) {
			camera.update(true);
		}
	}

	public void setStandardVelocity() {
		velocity = 0.0001f;
	}
}
