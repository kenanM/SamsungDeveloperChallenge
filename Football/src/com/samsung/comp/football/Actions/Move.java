package com.samsung.comp.football.Actions;

import java.util.Random;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Player;

public class Move extends Action {

	Vector2[] path;
	int positionInPath;
	Player player;
	Action nextAction;
	float velocity = 200;

	public Move(Player player, Vector2[] path) {
		this.player = player;
		this.path = path;
		this.nextAction = new Stop();
	}

	public Move(Player player, Vector2[] path, Action nextAction) {
		this.player = player;
		this.path = path;
		this.nextAction = nextAction;
	}

	@Override
	public void executeNextStep(float time) {

		if (complete) {
			nextAction.executeNextStep(time);
			return;
		}

		// Overview: We loop through each of the points in the list, if they are
		// are within range set our players position to be that point keep going
		// until either we run out of distance or we can't reach the next point
		// in which case move towards it using a utility method
		Vector2 position = new Vector2(player.getPlayerX(), player.getPlayerY());
		float distance = time * velocity;

		while (distance > 0) {
			Vector2 target = path[positionInPath];
			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				position.set(target);
				positionInPath++;
				if (positionInPath == path.length) {
					complete = true;
					return;
				}
			} else {
				// Move towards the next position (which is out of reach).
				Vector2 movement = Utils.getMoveVector(position, target,
						distance);
				position.add(movement);
				break;
			}
		}

		player.x = Player.translatePlayerCoordinate(position.x);
		player.y = Player.translatePlayerCoordinate(position.y);
	}

	public Vector2[] getPath(){
		return path;
	}
}
