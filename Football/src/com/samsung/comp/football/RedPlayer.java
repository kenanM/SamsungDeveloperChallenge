package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class RedPlayer extends Player {

	private static final long serialVersionUID = 3L;

	public RedPlayer(float x, float y) {
		super(x, y);
		this.TEAM = TeamColour.RED;
		this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		this.walkSheet = new Texture(Gdx.files.internal("redPlayer.png"));
		this.walkAnimation = new Animation(0.025f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);

	}

}
