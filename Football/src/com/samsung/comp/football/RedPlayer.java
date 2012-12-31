package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class RedPlayer extends Player {

	private static final long serialVersionUID = 3L;
	
	public RedPlayer(float playerX, float playerY) {
		super(playerX, playerY);
		this.TEAM=TeamColour.RED;
		hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		walkSheet = new Texture(Gdx.files.internal("redPlayer.png"));
		this.walkAnimation = new Animation(0.025f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);

	}

}
