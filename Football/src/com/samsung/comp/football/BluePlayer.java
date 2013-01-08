package com.samsung.comp.football;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.samsung.comp.football.Player.TeamColour;

public class BluePlayer extends Player {

	private static final long serialVersionUID = 2L;

	public BluePlayer(float x, float y) {
		super(x, y);
		this.TEAM = TeamColour.BLUE;
		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.walkSheet = new Texture(Gdx.files.internal("overhead spritesheet strip 256 x 256.png"));
		this.walkAnimation = new Animation(0.025f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}
	
	public BluePlayer(float x, float y, float shoot, float run, float tackle, float tackleStop) {
		super(x, y);
		this.TEAM = TeamColour.RED;
		this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		this.walkSheet = new Texture(Gdx.files.internal("overhead spritesheet strip(red) 256 x 256.png"));
		this.walkAnimation = new Animation(0.025f,
				createTextureRegion(walkSheet));
		this.currentFrame = walkAnimation.getKeyFrame(0);
		
		shootSpeed = shoot;
		runSpeed = run;
		tackleSkill = tackle;
		tacklePreventionSkill = tackleStop;
	}
}
