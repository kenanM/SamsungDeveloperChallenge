package com.samsung.comp.football.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.samsung.comp.football.AbstractGame;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.Actions.Utils;

public class BlueGoalie extends Goalie {

	private static final long serialVersionUID = 7960723633348803798L;

	public BlueGoalie(float playerX, float playerY, AbstractGame game,
			float saving) {
		super(playerX, playerY, game);
		savingSkill = saving;
		this.goal = Game.BLUE_GOAL;
		this.TEAM = TeamColour.BLUE;
		this.rotation = 90;
		this.middle = Game.BLUE_GOAL.cpy().add(0, DEFENSIVE_DISTANCE_FROM_GOAL);

		this.hoverTexture = new Texture(Gdx.files.internal("blue hover.png"));
		this.selectTexture = new Texture(Gdx.files.internal("blueSelect.png"));

		this.walkSheet = new Texture(Gdx.files.internal("greenPlayer.png"));
		this.walkAnimation = new Animation(0.10f, Utils.createTextureRegion(
				walkSheet, NUMBER_OF_FRAMES));
		this.currentFrame = walkAnimation.getKeyFrame(0);
	}

}
