package com.samsung.comp.football.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.Actions.Utils;

public class RedGoalie extends Goalie {

	private static final long serialVersionUID = -5309756541404354402L;

	public RedGoalie(float playerX, float playerY, AbstractGame game,
			float saving) {
		super(playerX, playerY, game);
		savingSkill = saving;
		this.goal = Game.RED_GOAL;
		this.TEAM = TeamColour.RED;
		this.rotation = 270;
		this.middle = Game.RED_GOAL.cpy().sub(0, DEFENSIVE_DISTANCE_FROM_GOAL);

		this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
		this.selectTexture = new Texture(Gdx.files.internal("redSelect.png"));

		this.walkSheet = new Texture(Gdx.files.internal("yellowPlayer.png"));
		this.walkAnimation = new Animation(0.10f, Utils.createTextureRegion(
				walkSheet, NUMBER_OF_FRAMES));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}

}
