package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player.TeamColour;
import com.samsung.comp.precisionfootball.R;

public class Goal extends Rectangle {

	TeamColour goalColour;
	Texture goalTexture;

	public Goal(Texture goalTexture, TeamColour goalColour, float goalX,
			float goalY) {
		this.goalTexture = goalTexture;
		this.goalColour = goalColour;
		this.x = goalX;
		this.y = goalY;
		this.width = goalTexture.getWidth();
		this.height = goalTexture.getHeight();
	}

	public Vector2 getGoalPoint() {
		return new Vector2(this.x + this.width / 2, this.y + this.height / 2);
	}

	public Rectangle getGoalArea() {
		return this;
	}

	protected Circle getGoalCircle(float radius) {
		return new Circle(getGoalPoint(), radius);
	}

	public void draw(SpriteBatch batch) {
		batch.draw(goalTexture, x, y, goalTexture.getWidth() / 2,
				goalTexture.getHeight() / 2, goalTexture.getWidth(),
				goalTexture.getHeight(), 1, 1, 0, 0, 0, goalTexture.getWidth(),
				goalTexture.getHeight(), false, true);
	}

}
