package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.precisionfootball.R;

public class Cursor {
	protected Texture texture;
	protected Vector2 location = new Vector2(0, 0);
	protected Player highlightedPlayer;
	protected float rotation;

	public Cursor() {
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setLocation(float x, float y) {
		location.set(x, y);
	}

	public Player getHighlightedPlayer() {
		return highlightedPlayer;
	}

	public void setHighlightedPlayer(Player player) {
		this.highlightedPlayer = player;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void draw(SpriteBatch batch) {
		Player highlightedPlayer = this.highlightedPlayer;
		if (highlightedPlayer != null) {
			highlightedPlayer.drawHighlight(batch);
		}

		Texture texture = this.texture;
		if (texture != null) {
			batch.draw(this.texture, location.x - texture.getWidth() / 2,
					location.y - texture.getHeight() / 2,
					texture.getWidth() / 2, texture.getHeight() / 2,
					texture.getWidth(), texture.getHeight(), 1, 1,
					this.rotation, 0, 0, texture.getWidth(),
					texture.getHeight(), false, true);
		}
	}

}
