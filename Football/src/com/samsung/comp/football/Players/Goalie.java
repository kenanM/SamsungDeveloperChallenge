package com.samsung.comp.football.Players;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.BallOwnerSetListener;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.Actions.Utils;

public class Goalie extends Player {

	private static final long serialVersionUID = 8190782604566806240L;

	protected Vector2 goal;
	protected Vector2 middle;
	protected static final float DEFENSIVE_DISTANCE_FROM_GOAL = 150;
	private final Texture unselectableHoverTexture;
	private final TextureRegion unselectableRegion;

	private AbstractGame game;

	public Goalie(float playerX, float playerY, TeamColour teamColour,
			AbstractGame game, float saving) {
		super(playerX, playerY, teamColour);
		this.game = game;
		unselectableHoverTexture = new Texture(
				Gdx.files.internal("unselectableHover.png"));
		unselectableRegion = new TextureRegion(unselectableHoverTexture, 13,
				13, 64, 64);

		savingSkill = saving;

		if (teamColour == TeamColour.BLUE) {
			this.goal = Game.BLUE_GOAL;
			this.TEAM = TeamColour.BLUE;
			this.rotation = 270;
			this.middle = Game.BLUE_GOAL.cpy().sub(0,
					DEFENSIVE_DISTANCE_FROM_GOAL);

			this.hoverTexture = new Texture(
					Gdx.files.internal("blue hover.png"));
			this.selectTexture = new Texture(
					Gdx.files.internal("blueSelect.png"));
			this.walkSheet = new Texture(Gdx.files.internal("greenPlayer.png"));
			this.walkAnimation = new Animation(0.10f,
					Utils.createTextureRegion(walkSheet, NUMBER_OF_FRAMES));
		} else {
			this.goal = Game.RED_GOAL;
			this.TEAM = TeamColour.RED;
			this.rotation = 90;
			this.middle = Game.RED_GOAL.cpy().add(0,
					DEFENSIVE_DISTANCE_FROM_GOAL);

			this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
			this.selectTexture = new Texture(
					Gdx.files.internal("redSelect.png"));

			this.walkSheet = new Texture(Gdx.files.internal("yellowPlayer.png"));
			this.walkAnimation = new Animation(0.10f,
					Utils.createTextureRegion(walkSheet, NUMBER_OF_FRAMES));
		}
		this.currentFrame = walkAnimation.getKeyFrame(0);
		
		game.getBall().addBallOwnerSetListener(new BallOwnerSetListener() {
			@Override
			public void onBallOwnerSet(Ball ball, Player newOwner) {
				ballOwnerSet(newOwner);
			}
		});

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
	public TextureRegion getHighlightTexture() {
		if (!hasBall() && game.getCurrentTeamColour() == getTeam()) {
			return unselectableRegion;
		} else {
			return super.getHighlightTexture();
		}
	}

	public void ballOwnerSet(Player newOwner) {
		if (newOwner == this) {
			game.onGoalieObtainsBall();
		}
	}
}
