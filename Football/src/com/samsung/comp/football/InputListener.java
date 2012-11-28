package com.samsung.comp.football;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.Action;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class InputListener implements SPenTouchListener, SPenHoverListener {

	private static final String TAG = "InputListener";
	private List<Action> actions;
	private final Game game;
	private boolean detectPresses = false;
	private List<Player> players;

	public InputListener(Game game) {
		players = new ArrayList<Player>();
		actions = new ArrayList<Action>();
		this.game = game;
		
	}

	public void beginInputStage(List<Player> players) {
		detectPresses = true;
		this.players = players;
	}

	private void isHoveringOverPlayer(Vector2 hoverPoint) {
		Log.v("hover", "hoverPoint " +hoverPoint.toString() + " player location: "+players.get(0).x+","+players.get(0).y);
		Vector2 playerVector;
		for (Player player : players) {
			playerVector = new Vector2(player.x+16, player.y+16);
			if (playerVector.epsilonEquals(hoverPoint, 32f)) {
				Log.v("Input", "Hover");
				player.highlight();
				return;
			}
		}
	}

	@Override
	public void onTouchButtonDown(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onTouchButtonDown: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onTouchButtonUp(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onTouchButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public boolean onTouchFinger(View arg0, MotionEvent arg1) {
		if (detectPresses) {
			Log.v(TAG, "onTouchFinger: " + arg1.getX() + ", " + arg1.getY());
			detectPresses=false;
			game.beginExecution(actions);
		}
		return false;
	}

	@Override
	public boolean onTouchPen(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onTouchPen: " + arg1.getX() + ", " + arg1.getY());
		return false;
	}

	@Override
	public boolean onTouchPenEraser(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onTouchPenEraser: " + arg1.getX() + ", " + arg1.getY());
		return false;
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		if (detectPresses) {
			//Log.v(TAG, "onHover: " + event.getX() + ", " + event.getY());
			Vector2 hoverPoint = new Vector2(event.getX(), event.getY());
			isHoveringOverPlayer(hoverPoint);
		}
		return false;
	}

	@Override
	public void onHoverButtonDown(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onHoverButtonDown: " + arg1.getX() + ", " + arg1.getY());
	}

	@Override
	public void onHoverButtonUp(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onHoverButtonUp: " + arg1.getX() + ", " + arg1.getY());
	}

}
