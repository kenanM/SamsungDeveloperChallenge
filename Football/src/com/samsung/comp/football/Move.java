package com.samsung.comp.football;

import android.graphics.Point;

public class Move extends Action {

	Point[] path;
	int positionInPath;
	Player player;
	Action nextAction;

	public Move(Player player, Point[] path) {
		this.player = player;
		this.path = path;
		this.nextAction = new Stop();
	}

	public Move(Player player, Point[] path, Action nextAction) {
		this.player = player;
		this.path = path;
		this.nextAction = nextAction;
	}
	
	@Override
	public void executeNextStep() {

		if (positionInPath == path.length) {
			complete = true;
		}
		
		if (complete) {
			nextAction.executeNextStep();
			return;
		}

		positionInPath++;
		Point nextPosition = path[positionInPath];
		player.x = nextPosition.x;
		player.y = nextPosition.y;

	}

}
