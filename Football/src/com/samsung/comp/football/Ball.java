package com.samsung.comp.football;

import android.graphics.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Ball extends Rectangle{

	private static final Texture TEXTURE = new Texture(Gdx.files.internal("redPlayer.png"));
	private static final int BALL_SIZE = 4;
	private Point ballPosition;
	private float ballSpeed = 0.3f;
	
}
