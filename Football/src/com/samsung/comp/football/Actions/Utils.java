package com.samsung.comp.football.Actions;

import com.badlogic.gdx.math.Vector2;

public class Utils {

	/**
	 * Returns a vector which can be added to the current position to move a
	 * certain distance towards a target.
	 */
	public static Vector2 getMoveVector(Vector2 currentPosition,
			Vector2 target, float distance) {

		float yDistance = target.y - currentPosition.y;
		float xDistance = target.x - currentPosition.x;

		// avoids a divide by zero exception
		if (yDistance == 0 && xDistance == 0) {
			return new Vector2(0, 0);
		}

		double dir = Math.atan2(yDistance, xDistance) * 180 / Math.PI;
		double x = Math.cos(dir * Math.PI / 180) * distance;
		double y = Math.sin(dir * Math.PI / 180) * distance;
		return new Vector2((float) x, (float) y);
	}

}
