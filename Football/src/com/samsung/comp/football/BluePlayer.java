package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class BluePlayer extends Player {

	private static final long serialVersionUID = 2L;

	public BluePlayer(float x, float y) {
		super(x, y);
		this.TEAM = TeamColour.BLUE;
		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.walkSheet = new Texture(Gdx.files.internal("bluePlayer.png"));
		this.walkAnimation = new Animation(0.025f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}
}
