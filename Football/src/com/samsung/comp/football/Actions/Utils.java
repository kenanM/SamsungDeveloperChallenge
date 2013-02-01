package com.samsung.comp.football.Actions;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Utils {

	/**
	 * Returns a vector which can be added to the current position to move a
	 * certain distance towards a target.
	 */
	public static Vector2 getMoveVector(Vector2 currentPosition,
			Vector2 target, float hypotenuse) {

		float yDistance = target.y - currentPosition.y;
		float xDistance = target.x - currentPosition.x;

		// avoids a divide by zero exception
		if (yDistance == 0 && xDistance == 0) {
			return new Vector2(0, 0);
		}

		double dir = Math.atan2(yDistance, xDistance) * 180 / Math.PI;
		double x = Math.cos(dir * Math.PI / 180) * hypotenuse;
		double y = Math.sin(dir * Math.PI / 180) * hypotenuse;
		return new Vector2((float) x, (float) y);
	}

	/**
	 * Returns a vector using a position, distance & angle which can be added to
	 * the current position to move a certain distance towards a target.
	 */
	public static Vector2 getMoveVector(Vector2 currentPosition, float angle,
			float hypotenuse) {

		double angleRadians = Math.toRadians(angle);

		double y = Math.sin(angleRadians) * hypotenuse;
		double x = Math.cos(angleRadians) * hypotenuse;

		return new Vector2((float) x, (float) y);
	}

	static public float randomFloat(Random rng, float a, float b) {
		if (rng == null) {
			rng = new Random();
			rng.setSeed(System.nanoTime());
		}
		return ((b - a) * rng.nextFloat() + a);
	}

	/** Create a one dimensional TextureRegion array */
	public static TextureRegion[] createTextureRegion(Texture animation,
			int numberOfFrames) {
		TextureRegion[][] temp = TextureRegion.split(animation,
				animation.getWidth() / numberOfFrames, animation.getHeight());
		// The split function gives us a two dimensional array so turn it into a
		// one dimensional one
		TextureRegion[] result = new TextureRegion[numberOfFrames];
		for (int i = 0; i < numberOfFrames; i++) {
			result[i] = temp[0][i];
		}
		return result;
	}

}
