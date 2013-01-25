package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class RedGoalie extends Goalie {

	private static final long serialVersionUID = -5309756541404354402L;

	public RedGoalie(float playerX, float playerY, Ball ball) {
		super(playerX, playerY, ball);
		this.goal = Game.RED_GOAL;
		this.TEAM = TeamColour.RED;
		this.vector = Game.RED_GOAL.cpy().sub(0, DEFENSIVE_DISTANCE_FROM_GOAL);

		this.x = vector.x;
		this.y = vector.y;

		this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		this.walkSheet = new Texture(
				Gdx.files.internal("yellowPlayer.png"));
		this.walkAnimation = new Animation(0.10f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);

	}

}
