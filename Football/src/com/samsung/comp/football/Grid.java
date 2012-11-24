package com.samsung.comp.football;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.samsung.comp.football.Player.TeamColour;


/** Represents the field (football pitch), divided into cells, gridWidth * gridHeight. By utilising the grid class 
 * the screen resolution will not affect the size of the playing area and the grid can be stretched to the screen,
 * although stretching may cause cells to become rectangular.
 */
public class Grid {
	
	private final int GRID_WIDTH;
	private final int GRID_HEIGHT;
	private final Cell[][] CELLS;

//	int displayWidth = Gdx.graphics.getWidth();
//	int displayHeight = Gdx.graphics.getHeight();
	
	public Grid(int gridWidth, int gridHeight) {
		this.GRID_WIDTH = gridWidth;
		this.GRID_HEIGHT = gridHeight;
		this.CELLS = new Cell[gridHeight][gridWidth];
	}
	
	public int getCellWidth(){
		return Gdx.graphics.getWidth() / this.GRID_WIDTH;
	}
	
	public int getCellHeight(){
		return Gdx.graphics.getHeight() / this.GRID_HEIGHT;
	}
	
}
