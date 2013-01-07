package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Player;

public class Mark extends Action {

	private Player target;
	private Player player;
	private static Texture targetTexture;

	public Mark(Player player, Player target) {
		this.player = player;
		this.target = target;
	}

	public static void create(Texture texture) {
		targetTexture = texture;
	}

	@Override
	public void execute(Player player) {
		player.mark(target);
	}

	public static void dispose() {
		targetTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(targetTexture, target.x - (targetTexture.getHeight() / 2),
				target.y - (targetTexture.getWidth() / 2));
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(player.x, player.y, target.x, target.y);
	}

}
