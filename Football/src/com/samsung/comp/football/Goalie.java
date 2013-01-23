package com.samsung.comp.football;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.Utils;

public abstract class Goalie extends Player {

	private static final long serialVersionUID = 8190782604566806240L;

	protected Vector2 goal;
	private Ball ball;
	private static final float DEFENSIVE_DISTANCE_FROM_GOAL = 150;

	// TODO: Introduce coloured goalie's
	public Goalie(float playerX, float playerY, Ball ball) {
		super(playerX, playerY);
		this.ball = ball;
	}

	@Override
	public void update(float time) {
		if (hasBall()) {
			super.update(time);
		} else {

			// If the ball is within a defensive distance of the goal move
			// towards it
			if (ball.getBallPosition().dst(goal) < DEFENSIVE_DISTANCE_FROM_GOAL) {
				this.path = new Vector2[] { ball.getBallPosition() };
			}
			// Otherwise move a defensive distance in between the goal and the
			// ball
			else {
				Vector2 distanceVector = Utils.getMoveVector(goal,
						ball.getBallPosition(), DEFENSIVE_DISTANCE_FROM_GOAL);
				Vector2 target = goal.cpy().add(distanceVector);
				//setAction(new Move( new Vector2[] { target}));
				this.path = new Vector2[] { target };
			}
			super.update(time);
		}
	}
}
