package com.samsung.comp.football;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.football.Actions.*;
import com.samsung.comp.football.Actions.Utils;

public class Player extends Rectangle {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 25;
	private static final int HOVER_SIZE = 64;

	private static Texture redPlayerTexture;
	private static Texture bluePlayerTexture;
	private static Texture blueHoverTexture;
	private static Texture redHoverTexture;

	private boolean isHighlighted = false;
	private final TeamColour TEAM;

	private float shootSpeed = 400;
	private float runSpeed = 300;
	private float tackleSkill = 100;
	private float tacklePreventionSkill = 0;

	// TODO: Player shot accuracy?
	// private float accuracy;
	Vector2[] path;
	int positionInPath = 0;
	private Action action;

	// TODO: MUST INITIALISE PLAYER STATS
	public Player(TeamColour colour, float playerX, float playerY) {
		this.TEAM = colour;
		this.x = translatePlayerCoordinate(playerX);
		this.y = translatePlayerCoordinate(playerY);
		width = PLAYER_SIZE;
		height = PLAYER_SIZE;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	private void clearAction() {
		this.action = null;
	}
	
	private void resetPathIndex() {
		positionInPath = 0;
	}
	
	public void reset() {
		clearAction();
		resetPathIndex();
	}

	public void executeAction() {
		if (action != null) {
			action.execute(this);
		}
	}

	public TeamColour getTeam() {
		return TEAM;
	}

	public float getPlayerX() {
		return x + (PLAYER_SIZE / 2);
	}

	public float getPlayerY() {
		return y + (PLAYER_SIZE / 2);
	}

	public Vector2 getPlayerPosition() {
		return new Vector2(getPlayerX(), getPlayerY());
	}

	/**
	 * Takes a player position's x or y component and translates it to the
	 * player's drawable x or y component.
	 * 
	 * @param c
	 *            The player position's x or y component.
	 * @return The drawable x or y component of the player.
	 */

	public static float translatePlayerCoordinate(float c) {
		return c - (PLAYER_SIZE / 2);
	}

	/**
	 * Takes a player position's x or y component and translates it to the hover
	 * texture's drawable x or y component.
	 * 
	 * @param c
	 *            The player position's x or y componen.
	 * @return The drawable x or y component of the hover texture.
	 */
	public static float translateHoverCoordinate(float c) {
		return c - (HOVER_SIZE / 2);
	}

	public void highlight() {
		isHighlighted = true;
	}

	public boolean isHighlighted() {
		if (isHighlighted) {
			isHighlighted = false;
			return true;
		} else {
			return false;
		}
	}

	public Texture getTexture() {
		if (TEAM == TeamColour.RED) {
			return redPlayerTexture;
		} else {
			return bluePlayerTexture;
		}
	}

	public Texture getHighlightTexture() {
		if (getTeam() == TeamColour.RED) {
			return redHoverTexture;
		} else {
			return blueHoverTexture;
		}
	}

	public float getTackleSkill() {
		return tackleSkill;
	}

	public float getTacklePreventionSkill() {
		return tacklePreventionSkill;
	}

	public static void create(Texture redPlayer, Texture redHover,
			Texture bluePlayer, Texture blueHover) {
		redPlayerTexture = redPlayer;
		redHoverTexture = redHover;
		bluePlayerTexture = bluePlayer;
		blueHoverTexture = blueHover;
	}

	public void draw(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(this.getTexture(), this.x, this.y);
		batch.draw(this.getTexture(), this.x, this.y, PLAYER_SIZE, PLAYER_SIZE);
		if (this.isHighlighted()) {
			batch.draw(this.getHighlightTexture(),
					translateHoverCoordinate(getPlayerX()),
					translateHoverCoordinate(getPlayerY()));
		}
	}

	public static void dispose() {
		bluePlayerTexture.dispose();
		redPlayerTexture.dispose();
	}

	public void move(Vector2[] path) {
		this.path = path;
		executeNextAction();
	}

	public void kick(Ball ball, Vector2 target) {
		Vector2 ballVelocity = Utils.getMoveVector(getPlayerPosition(), target,
				shootSpeed);
		ball.move(ballVelocity);
		executeNextAction();
		ball.removeOwner();
	}

	public void mark(Player target) {
		move(new Vector2[] { target.getPlayerPosition() });
	}

	// TODO: Account for a moving player.
	public void pass(Ball ball, Player target) {
		kick(ball, target.getPlayerPosition());
	}

	private void executeNextAction() {
		if (action.getNextAction() == null) {
			action = null;
		} else {
			action = action.getNextAction();
			action.execute(this);
		}
	}

	public void update(float time) {

		Vector2 position = moveAlongPath(time);

		this.x = Player.translatePlayerCoordinate(position.x);
		this.y = Player.translatePlayerCoordinate(position.y);

	}

	/**
	 * Overview: We loop through each of the points in the list, if they are are
	 * within range set our players position to be that point keep going until
	 * either we run out of distance or we can't reach the next point in which
	 * case move towards it using a utility method
	 */
	private Vector2 moveAlongPath(float time) {
		float distance = time * runSpeed;
		Vector2 position = getPlayerPosition();

		while (distance > 0 && path != null && path.length > 0) {
			Vector2 target = path[positionInPath];

			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				position.set(target);
				positionInPath++;
				if (positionInPath != 0 && positionInPath == path.length) {
					path = new Vector2[] {};
					if (action instanceof Move) {
						executeNextAction();
						resetPathIndex();
					}
					break;
				}
			} else {
				// Move towards the next position (which is out of reach).
				Vector2 movement = Utils.getMoveVector(position, target,
						distance);
				position.add(movement);
				break;
			}
		}
		return position;
	}
}
