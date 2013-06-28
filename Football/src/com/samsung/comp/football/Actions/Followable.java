package com.samsung.comp.football.Actions;

import com.badlogic.gdx.math.Vector2;

/**
 * This is used so a MoveToPosition can begin from either a ball or a player.
 * 
 */

public interface Followable {
	public Vector2 getPosition();
}
