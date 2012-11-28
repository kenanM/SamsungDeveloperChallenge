package com.samsung.comp.football;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class InputListener implements SPenTouchListener, SPenHoverListener {

	private static final String TAG = "InputListener";

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
		Log.v(TAG, "onTouchFinger: " + arg1.getX() + ", " + arg1.getY());
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
	public boolean onHover(View arg0, MotionEvent arg1) {
		Log.v(TAG, "onHover: " + arg1.getX() + ", " + arg1.getY());
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
