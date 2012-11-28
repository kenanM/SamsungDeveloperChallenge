package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 25;

	private boolean isHighlighted=false;
	
	public final TeamColour TEAM;

	public Player(TeamColour colour, float x, float y) {
		this.TEAM = colour;
		this.x = x;
		this.y = y;
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
	}

	public TeamColour getTeam() {
		return TEAM;
	}

	public Texture getTexture() {
		if (getTeam() == TeamColour.RED) {
			return Game.redPlayerTexture;
		} else {
			return Game.bluePlayerTexture;
		}
	}

	public void highlight() {
		isHighlighted = true;
	}

	public boolean isHighlighted() {
		if(isHighlighted){
			isHighlighted=false;
			return true;
		} else{
			return false;
		}
	}

	public Texture getHighlightTexture() {
		if (getTeam() == TeamColour.RED) {
			return Game.redHoverTexture;
		} else {
			return Game.blueHoverTexture;
		}
	}

}
