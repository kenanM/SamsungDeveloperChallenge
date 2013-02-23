package com.samsung.comp.football.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.samsung.comp.football.Actions.Utils;

public class BluePlayer extends Player {

	private static final long serialVersionUID = 2L;

	public BluePlayer(float x, float y) {
		super(x, y);
		this.TEAM = TeamColour.BLUE;
		this.rotation = 90;
		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.selectTexture = new Texture(Gdx.files.internal("blueSelect.png"));
		this.walkSheet = new Texture(Gdx.files.internal("bluePlayer.png"));
		this.walkAnimation = new Animation(0.10f, Utils.createTextureRegion(
				walkSheet, NUMBER_OF_FRAMES));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}

	public BluePlayer(float x, float y, float shoot, float run, float tackle,
			float tackleStop) {
		super(x, y);
		this.TEAM = TeamColour.RED;
		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.selectTexture = new Texture(Gdx.files.internal("blueSelect.png"));
		this.walkSheet = new Texture(Gdx.files.internal("bluePlayer.png"));
		this.walkAnimation = new Animation(0.10f, Utils.createTextureRegion(
				walkSheet, NUMBER_OF_FRAMES));
		this.currentFrame = walkAnimation.getKeyFrame(0);

		shootSpeed = shoot;
		runSpeed = run;
		tackleSkill = tackle;
		tacklePreventionSkill = tackleStop;
		this.savingSkill = savingSkill;
	}
}
