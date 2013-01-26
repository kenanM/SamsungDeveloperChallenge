package com.samsung.comp.football.Actions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.samsung.comp.football.Players.Player;

public class Mark extends Action {

	private Player target;
	private Player player;
	private static Texture markTexture;

	public Mark(Player player, Player target) {
		this.player = player;
		this.target = target;
	}

	public static void create(Texture texture) {
		markTexture = texture;
	}

	@Override
	public void execute(Player player) {
		player.mark(target);
	}

	public static void dispose() {
		markTexture.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(markTexture, target.x - (markTexture.getHeight() / 2),
				target.y - (markTexture.getWidth() / 2));
		super.draw(batch);
	}

	@Override
	public void draw(ShapeRenderer renderer) {
		renderer.line(player.x, player.y, target.x, target.y);
		super.draw(renderer);
	}

}
