package com.samsung.comp.football.Players;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.Actions.Utils;

public abstract class Goalie extends Player {

	private static final long serialVersionUID = 8190782604566806240L;

	protected Vector2 goal;
	protected Vector2 middle;
	protected static final float DEFENSIVE_DISTANCE_FROM_GOAL = 150;
	private final Texture unselectableHoverTexture;

	private Game game;

	public Goalie(float playerX, float playerY, Game game) {
		super(playerX, playerY);
		this.game = game;
		unselectableHoverTexture = new Texture(
				Gdx.files.internal("unselectableHover.png"));
	}

	@Override
	public void update(float time) {
		if (hasBall()) {
			Log.v("Goalie", "I have the ball gavin!");
			if (getPlayerPosition().dst(goal) > DEFENSIVE_DISTANCE_FROM_GOAL * 1.5) {
				executeNextAction();
			}
			super.update(time);
		} else {
			Ball ball = game.getBall();
			float ballDistanceFromGoal = ball.getBallPosition().dst(goal);

			if (ballDistanceFromGoal <= DEFENSIVE_DISTANCE_FROM_GOAL) {
				// If the ball is within a defensive distance of the goal move
				// towards it
				this.path = new Vector2[] { ball.getBallPosition() };
			} else if (ballDistanceFromGoal <= Game.PLAYING_AREA_HEIGHT / 2) {
				// If the ball is in my half of the playing field move in
				// between it and the goal
				Vector2 distanceVector = Utils.getMoveVector(goal,
						ball.getBallPosition(), DEFENSIVE_DISTANCE_FROM_GOAL);
				Vector2 target = goal.cpy().add(distanceVector);
				// setAction(new Move( new Vector2[] { target}));
				this.path = new Vector2[] { target };
			} else {
				// Otherwise move to middle of goal defense area
				this.path = new Vector2[] { middle };
			}

			if (this.getPlayerPosition().x == path[0].x
					&& this.getPlayerPosition().y == path[0].y) {
				Log.v("Goalie", "setting path to null");
				this.path = null;
			}

			resetPathIndex();
			super.update(time);
		}
	}

	@Override
	public Texture getHighlightTexture() {
		if (!hasBall() && game.getHumanColour() == getTeam()) {
			return unselectableHoverTexture;
		} else {
			return super.getHighlightTexture();
		}
	}
}
