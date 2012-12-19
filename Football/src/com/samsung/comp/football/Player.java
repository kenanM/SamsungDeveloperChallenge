package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 25;
	private static Texture redPlayerTexture;
	private static Texture bluePlayerTexture;
	private static Texture blueHoverTexture;
	private static Texture redHoverTexture;

	private boolean isHighlighted=false;
	private final TeamColour TEAM;
	

	public Player(TeamColour colour, float playerX, float playerY) {
		this.TEAM = colour;
		this.x = translatePlayerCoordinate(playerX);
		this.y = translatePlayerCoordinate(playerY);
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
	}

	public TeamColour getTeam() {
		return TEAM;
	}
	
	public float getPlayerX() {
		return x + (PLAYER_SIZE/2);
	}
	
	public float getPlayerY() {
		return y + (PLAYER_SIZE/2);
	}
	
	// Takes a player's x or y co-ordinate and translates it to drawable x or y
	public static float translatePlayerCoordinate(float c){
		return c - (PLAYER_SIZE/2);
	}
	
	public Vector2 getPlayerPosition(){
		return new Vector2(getPlayerX(), getPlayerY());
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

	public static void setTexture(TeamColour team, Texture texture) {
		if (team==TeamColour.RED) {
			redPlayerTexture = texture;
		}
		else{
			bluePlayerTexture = texture;
		}
	}
	
	public Texture getTexture() {
		if (TEAM == TeamColour.RED) {
			return redPlayerTexture;
		} else {
			return bluePlayerTexture;
		}
	}

	public static void setHighlightTexture(TeamColour team, Texture texture) {
		if (team==TeamColour.RED) {
			redHoverTexture = texture;
		}
		else{
			blueHoverTexture = texture;
		}
	}
	
	public Texture getHighlightTexture() {
		if (getTeam() == TeamColour.RED) {
			return redHoverTexture;
		} else {
			return blueHoverTexture;
		}
	}
	
	public static void dispose(){
		bluePlayerTexture.dispose();
		redPlayerTexture.dispose();
	}

}
