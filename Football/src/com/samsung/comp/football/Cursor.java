package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Players.Player;

public class Cursor {
	protected Texture texture;
	protected Vector2 location = new Vector2(0, 0);
	protected boolean visible = false;
	protected Player highlightedPlayer;

	public Cursor() {
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void showTexture(Texture texture) {
		this.texture = texture;
		this.visible = true;
	}

	public void setLocation(float x, float y) {
		location.set(x, y);
	}

	public void setVisibility(boolean visible) {
		this.visible = visible;
	}

	public Player getHighlightedPlayer() {
		return highlightedPlayer;
	}

	public void setHighlightedPlayer(Player player) {
		this.highlightedPlayer = player;
	}

	public void draw(SpriteBatch batch) {
		if (texture != null && visible) {
			batch.draw(this.texture, location.x - texture.getWidth() / 2,
					location.y - texture.getHeight() / 2, texture.getWidth(),
					texture.getHeight(), 0, 0, texture.getWidth(),
					texture.getHeight(), false, false);

			if (highlightedPlayer != null) {
				getHighlightedPlayer().drawHighlight(batch);
			}

		}
	}

}
