package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Stop;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 25;
	private static final int HOVER_SIZE = 64;

	private static Texture redPlayerTexture;
	private static Texture bluePlayerTexture;
	private static Texture blueHoverTexture;
	private static Texture redHoverTexture;

	private boolean isHighlighted = false;
	private final TeamColour TEAM;
	private Action action;

	public Player(TeamColour colour, float playerX, float playerY) {
		this.TEAM = colour;
		this.x = translatePlayerCoordinate(playerX);
		this.y = translatePlayerCoordinate(playerY);
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
		action = new Stop();
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public void clearAction() {
		this.action = new Stop();
	}

	public void executeNextStep(float time) {
		action.executeNextStep(time);
	}

	public TeamColour getTeam() {
		return TEAM;
	}

	public float getPlayerX() {
		return x + (PLAYER_SIZE / 2);
	}

	public float getPlayerY() {
		return y + (PLAYER_SIZE / 2);
	}

	// Takes a player's x or y co-ordinate and translates it to a player's
	// drawable x or y
	public static float translatePlayerCoordinate(float c) {
		return c - (PLAYER_SIZE / 2);
	}

	// Takes a player's x or y co-ordinate and translates it to a hover
	// texture's x or y
	public static float translateHoverCoordinate(float c) {
		return c - (HOVER_SIZE / 2);
	}

	public Vector2 getPlayerPosition() {
		return new Vector2(getPlayerX(), getPlayerY());
	}

	public void highlight() {
		isHighlighted = true;
	}

	public boolean isHighlighted() {
		if (isHighlighted) {
			isHighlighted = false;
			return true;
		} else {
			return false;
		}
	}

	public Texture getTexture() {
		if (TEAM == TeamColour.RED) {
			return redPlayerTexture;
		} else {
			return bluePlayerTexture;
		}
	}

	public Texture getHighlightTexture() {
		if (getTeam() == TeamColour.RED) {
			return redHoverTexture;
		} else {
			return blueHoverTexture;
		}
	}

	public static void create(Texture redPlayer, Texture redHover,
			Texture bluePlayer, Texture blueHover) {
		redPlayerTexture = redPlayer;
		redHoverTexture = redHover;
		bluePlayerTexture = bluePlayer;
		blueHoverTexture = blueHover;
	}

	public void render(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(this.getTexture(), this.x, this.y);
		batch.draw(this.getTexture(), this.x, this.y, PLAYER_SIZE, PLAYER_SIZE);
		if (this.isHighlighted()) {
			batch.draw(this.getHighlightTexture(),
					translateHoverCoordinate(getPlayerX()),
					translateHoverCoordinate(getPlayerY()));
		}
	}

	public static void dispose() {
		bluePlayerTexture.dispose();
		redPlayerTexture.dispose();
	}

}
