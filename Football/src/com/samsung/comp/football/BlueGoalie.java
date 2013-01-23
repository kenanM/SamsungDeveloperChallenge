package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class BlueGoalie extends Goalie {

	private static final long serialVersionUID = 7960723633348803798L;

	public BlueGoalie(float playerX, float playerY, Ball ball) {
		super(playerX, playerY, ball);

		this.goal = Game.BLUE_GOAL;
		this.TEAM = TeamColour.BLUE;
		this.vector = Game.BLUE_GOAL.cpy().add(0, DEFENSIVE_DISTANCE_FROM_GOAL);

		this.x=vector.x;
		this.y=vector.y;
		
		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.walkSheet = new Texture(
				Gdx.files.internal("overhead spritesheet strip(green).png"));
		this.walkAnimation = new Animation(0.10f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}

}
