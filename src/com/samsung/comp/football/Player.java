package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 12;

	public final TeamColour TEAM;
	// TODO implement a Move interface/AbstractClass etc
	private Move move;

	public Player(TeamColour colour) {
		this.TEAM = colour;
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
	}

	public TeamColour getTeam() {
		return TEAM;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public Texture getTexture() {
		if (getTeam() == TeamColour.RED) {
			return Game.redPlayerTexture;
		} else {
			return Game.bluePlayerTexture;
		}
	}

	public void update() {
		// TODO update the player X,Y coordinates etc..
	}

}
