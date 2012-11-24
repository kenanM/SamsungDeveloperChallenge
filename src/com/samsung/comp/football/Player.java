package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import com.samsung.comp.football.Game.GameState;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 12;
	
	private int playerID;
	private boolean hasBall;
	// Centre point of the player and graphic
	private Point playerPosition;
	public final TeamColour TEAM;
	private List<Action> actions = new ArrayList<Action>(); 
	private float playerSpeed = 1.0f;
	
		
	
	public Player(TeamColour colour) {
		this.TEAM = colour;
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
	}
	
	
	
	public Point getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(Point playerPosition) {
		this.playerPosition = playerPosition;
	}
	
	public void setPlayerPosition(int x, int y) {
		this.playerPosition = new Point(x, y);
	}
	
	// TODO Don't know if Float.valueOf(1.1).intValue() will throw an exception. Don't know if we need to accept floats.
	public void setPlayerPosition(float x, float y) {
		int a  = Float.valueOf(x).intValue();
		int b  = Float.valueOf(y).intValue();
		this.playerPosition = new Point(a, b);
	}

	public TeamColour getTeam() {
		return TEAM;
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	
	public void addAction(Action action){
		this.actions.add(action);
	}

	public float getPlayerSpeed() {
		return playerSpeed;
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

		// Executes each action in the queue
		for(Action action : actions) {
			action.executeAction(this);
		}
		// Update and calculate the corner of the sprite based on the player's centre point
		this.x = playerPosition.x - (PLAYER_SIZE/2);
		this.y = playerPosition.y - (PLAYER_SIZE/2);
		
	}
	

}
