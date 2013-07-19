package com.samsung.comp.football.Players;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.samsung.comp.events.ActionFiredListener;
import com.samsung.comp.football.Ball;
import com.samsung.comp.football.Game;
import com.samsung.comp.football.Actions.Action;
import com.samsung.comp.football.Actions.Followable;
import com.samsung.comp.football.Actions.Kick;
import com.samsung.comp.football.Actions.Move;
import com.samsung.comp.football.Actions.MoveToPosition;
import com.samsung.comp.football.Actions.Pass;
import com.samsung.comp.football.Actions.Utils;

public class Player extends Rectangle implements Followable {

	public enum TeamColour {
		RED, BLUE
	}

	private static final long serialVersionUID = 1L;
	private static final int PLAYER_SIZE = 64;
	private static final int HOVER_SIZE = 64;

	protected Texture walkSheet;
	protected Animation walkAnimation;
	protected TextureRegion currentFrame;
	private float stateTime = 0l;
	/** The dimensions of the run animation */
	protected static final int NUMBER_OF_FRAMES = 10;

	protected ActionFiredListener listener;

	protected Texture hoverTexture;
	protected Texture selectTexture;
	protected TextureRegion hoverRegion;

	protected static Texture notificationTexture;
	private float notificationTime = 0f;

	protected TeamColour TEAM;

	protected float shootSpeed = 550;
	protected float runSpeed = 150;
	protected float tackleSkill = 100;
	protected float tacklePreventionSkill = 40;
	protected float savingSkill = 420;

	private boolean isPurchased = true;

	// TODO: Player shot accuracy?
	// private float accuracy;
	Vector2[] path;
	int positionInPath = 0;
	float rotation;
	protected Action action;
	private float timeSinceKick = Game.BALL_PASS_TIME;
	private float timeSinceFailedTackle = Game.BALL_CHANGE_TIME;
	private Ball ball;

	public Player(float x, float y, TeamColour teamColour) {
		this.x = translatePlayerCoordinate(x);
		this.y = translatePlayerCoordinate(y);
		this.width = PLAYER_SIZE;
		this.height = PLAYER_SIZE;
		this.TEAM = teamColour;

		if (teamColour == TeamColour.RED) {
			this.rotation = 90;
			this.hoverTexture = new Texture(Gdx.files.internal("red hover.png"));
			this.selectTexture = new Texture(
					Gdx.files.internal("redSelect.png"));
			this.walkSheet = new Texture(Gdx.files.internal("redPlayer.png"));
			this.walkAnimation = new Animation(0.10f,
					Utils.createTextureRegion(walkSheet, NUMBER_OF_FRAMES));
		} else {
			this.TEAM = TeamColour.BLUE;
			this.rotation = 270;
			this.hoverTexture = new Texture(
					Gdx.files.internal("blue hover.png"));
			this.selectTexture = new Texture(
					Gdx.files.internal("blueSelect.png"));
			this.walkSheet = new Texture(Gdx.files.internal("bluePlayer.png"));
			this.walkAnimation = new Animation(0.10f,
					Utils.createTextureRegion(walkSheet, NUMBER_OF_FRAMES));
		}
		this.currentFrame = walkAnimation.getKeyFrame(0);

		this.hoverRegion = new TextureRegion(hoverTexture, HOVER_SIZE,
				HOVER_SIZE);
	}

	public Player(float x, float y, TeamColour teamColour, float shoot,
			float run, float tackle, float tackleStop, float savingSkill) {

		this(x, y, teamColour);

		this.shootSpeed = shoot;
		this.runSpeed = run;
		this.tackleSkill = tackle;
		this.tacklePreventionSkill = tackleStop;
		this.savingSkill = savingSkill;
	}

	@Override
	public Vector2 getPosition() {
		return getPlayerPosition();
	}

	public void setListener(ActionFiredListener listener) {
		this.listener = listener;
	}

	public void clearListener() {
		this.listener = null;
	}

	public static void create(Texture texture) {
		notificationTexture = texture;
	}

	public static int getPlayerSize() {
		return PLAYER_SIZE;
	}

	public void setTimeSinceKick(float time) {
		timeSinceKick = time;
	}

	public float getTimeSinceKick() {
		return timeSinceKick;
	}

	public void setTimeSinceFailedTackle(float time) {
		timeSinceFailedTackle = time;
	}

	public float getTimeFailedTackle() {
		return timeSinceFailedTackle;
	}

	public void setNoticationTime(float time) {
		this.notificationTime = time;
	}

	public void addAction(Action newAction) {
		if (this.action == null) {
			this.action = newAction;
		} else {
			this.action.addNextAction(newAction);
		}
	}

	public Action getAction(int index) throws IndexOutOfBoundsException {
		if (index == 0) {
			throw new IndexOutOfBoundsException();
		}
		int i = 1;
		Action act = action;
		while (i != index && act != null && act.getNextAction() != null) {
			act = act.getNextAction();
			i++;
		}
		return act;
	}

	public void setAction(Action newAction, int index) {
		if (index == 0) {
			action = newAction;
			return;
		} else {
			Action act = getAction(index);
			if (act == null) {
				return;
			} else {
				act.setNextAction(newAction);
			}
		}
	}

	public Action getAction() {
		return action;
	}

	public Action getFinalAction() {
		if (this.action == null) {
			return null;
		} else {
			return action.getFinalAction();
		}
	}

	/**
	 * Used for player selection
	 * 
	 * @return the final point in the chain of actions
	 */
	public Vector2 getFuturePosition() {
		if (action != null) {
			Vector2 futurePosition = action.getFuturePosition();
			if (futurePosition != null) {
				return futurePosition;
			}
		}
		return getPlayerPosition();
	}

	/**
	 * Used for player selection
	 * 
	 * @return The player's current position and all of the locations that each
	 *         action returns
	 */
	public List<Vector2> getPositionList() {
		ArrayList<Vector2> points = new ArrayList<Vector2>();

		points.add(getPlayerPosition());

		if (action != null) {
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(action);

			while (actions.get(actions.size() - 1) != null) {
				Action currentAction = actions.get(actions.size() - 1);
				if (currentAction.getPosition() != null) {
					points.add(currentAction.getPosition());
				}
				actions.add(currentAction.getNextAction());
			}
		}
		return points;
	}

	/**
	 * Used for line time indicators and ghost player indicators
	 * 
	 * @param time
	 *            time in seconds
	 * @return the position the player will be at after time seconds
	 */
	public Vector2 getFuturePosition(float time, boolean returnNulls) {
		if (action != null) {
			Vector2 futurePosition = action.getFuturePosition(time,
					getPlayerPosition(), runSpeed, positionInPath, returnNulls);
			return futurePosition;
		} else {
			return null;
		}
	}

	/**
	 * Used for passing the ball between players
	 * 
	 * @param time
	 *            time in seconds
	 * @param initialPosition
	 *            position of this player to start calculating from
	 * @return gets the position this player will be at after the input amount
	 *         of time, starting from initialPosition. Used for passing to a
	 *         moving player
	 */
	public Vector2 getFuturePosition(float time, int pointInCurrentPath) {
		if (action != null) {
			Vector2 futurePosition = action.getFuturePosition(time,
					getPlayerPosition(), runSpeed, pointInCurrentPath, false);
			return futurePosition;
		} else {
			return getPlayerPosition();
		}
	}

	public int getPositionInPath() {
		return positionInPath;
	}

	public void clearActions() {
		if (this.action != null) {
			this.action.clearSubsequentActions();
		}
		this.action = null;
	}

	public void undoLastAction() {
		LinkedList<Action> actions = getActions();

		if (actions.size() == 0) {
			return;
		}

		if (actions.size() == 1) {
			this.action = null;
		}

		if (actions.size() >= 2) {
			actions.get(actions.size() - 2).setNextAction(null);
		}
	}

	protected void resetPathIndex() {
		positionInPath = 0;
	}

	public void reset() {
		clearActions();
		resetPathIndex();
		path = null;
	}

	public void executeAction() {
		if (action != null) {
			if (listener != null) {
				listener.onActionFired(this, action);
			}
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

	public Vector2 getBallPosition() {
		// TODO: hard coded value
		return getPlayerPosition().add(
				Utils.getMoveVector(getPlayerPosition(), rotation, 25));
	}

	public TextureRegion getTexture() {
		return currentFrame;
	}

	public TextureRegion getHighlightTexture() {
		return hoverRegion;
	}

	public Rectangle getHighlightRectangle() {
		return new Rectangle(translateHoverCoordinate(this.getPlayerX()),
				translateHoverCoordinate(this.getPlayerY()), HOVER_SIZE,
				HOVER_SIZE);
	}

	public int getShootSpeedBarCount() {
		return (int) (shootSpeed / getShootSpeedBarValue());
	}

	public int getRunSpeedBarCount() {
		return (int) (runSpeed / getRunSpeedBarValue());
	}

	public int getTackleSkillBarCount() {
		return (int) (tackleSkill / getTackleSkillBarValue());
	}

	public int getTacklePreventionSkillBarCount() {
		return (int) (tacklePreventionSkill / getTacklePreventionBarValue());
	}

	public float getSavingSkillBarCount() {
		return (int) (savingSkill / getSavingSkillBarValue());
	}

	public float getShootSpeedBarValue() {
		return 24;
	}

	public float getRunSpeedBarValue() {
		return 10;
	}

	public float getTackleSkillBarValue() {
		return 4;
	}

	public float getTacklePreventionBarValue() {
		return 4;
	}

	public float getSavingSkillBarValue() {
		return 22;
	}

	public float getShootSpeed() {
		return shootSpeed;
	}

	public float getRunSpeed() {
		return runSpeed;
	}

	public float getTackleSkill() {
		return tackleSkill;
	}

	public float getTacklePreventionSkill() {
		return tacklePreventionSkill;
	}

	public float getSavingSkill() {
		return savingSkill;
	}

	public void setShootSpeed(float newShootSpeed) {
		this.shootSpeed = newShootSpeed;
	}

	public void setRunSpeed(float newRunSpeed) {
		this.runSpeed = newRunSpeed;
	}

	public void setTackleSkill(float newTackleSkill) {
		this.tackleSkill = newTackleSkill;
	}

	public void setTacklePreventionSkill(float newTacklePreventionSkill) {
		this.tacklePreventionSkill = newTacklePreventionSkill;
	}

	public void setSavingSkill(float newSavingSkill) {
		this.savingSkill = newSavingSkill;
	}

	public void trimAllSkills() {
		float minRunSpeed = 0;
		float minShootSpeed = 0;
		float minTackleSkill = 0;
		float minTacklePreventionSkill = 0;
		float minSavingSkill = 0;

		float maxRunSpeed = 25 * getRunSpeedBarValue();
		float maxShootSpeed = 25 * getShootSpeedBarValue();
		float maxTackleSkill = 25 * getTackleSkillBarValue();
		float maxTacklePreventionSkill = 25 * getTacklePreventionBarValue();
		float maxSavingSkill = 25 * getSavingSkillBarValue();

		this.runSpeed = Math.max(minRunSpeed, this.runSpeed);
		this.shootSpeed = Math.max(minShootSpeed, this.shootSpeed);
		this.tackleSkill = Math.max(minTackleSkill, this.tackleSkill);
		this.tacklePreventionSkill = Math.max(minTacklePreventionSkill,
				this.tacklePreventionSkill);
		this.savingSkill = Math.max(minSavingSkill, this.savingSkill);

		this.runSpeed = Math.min(maxRunSpeed, this.runSpeed);
		this.shootSpeed = Math.min(maxShootSpeed, this.shootSpeed);
		this.tackleSkill = Math.min(maxTackleSkill, this.tackleSkill);
		this.tacklePreventionSkill = Math.min(maxTacklePreventionSkill,
				this.tacklePreventionSkill);
		this.savingSkill = Math.min(maxSavingSkill, this.savingSkill);
	}

	public boolean isPurchased() {
		// TODO: figure out player ownership
		return isPurchased;
	}

	public String getName() {
		// TODO: Sort out player names
		return "Gavin";
	}

	public void draw(SpriteBatch batch) {
		// draw sprite as is or stretch to fill rectangle
		// batch.draw(this.getTexture(), this.x, this.y);
		// batch.draw(this.getTexture(), this.x, this.y, PLAYER_SIZE,
		// PLAYER_SIZE);

		// Gets a region centered on the player
		TextureRegion asd = new TextureRegion(getTexture(), getTexture()
				.getRegionWidth() / 2 - PLAYER_SIZE / 2, getTexture()
				.getRegionHeight() / 2 - PLAYER_SIZE / 2, PLAYER_SIZE,
				PLAYER_SIZE);

		batch.draw(asd, x, y, PLAYER_SIZE / 2, PLAYER_SIZE / 2, PLAYER_SIZE,
				PLAYER_SIZE, 1, 1, rotation, true);

		if (this.notificationTime > 0) {
			batch.draw(notificationTexture,
					getPlayerX() - notificationTexture.getWidth() / 2, this.y
							- notificationTexture.getHeight()
							+ (notificationTime * 25),
					notificationTexture.getWidth(),
					notificationTexture.getHeight(), 0, 0,
					notificationTexture.getWidth(),
					notificationTexture.getHeight(), false, true);
		}
	}

	public void drawHighlight(SpriteBatch batch) {
		batch.draw(getHighlightTexture(),
				translateHoverCoordinate(getPlayerX()),
				translateHoverCoordinate(getPlayerY()));
	}

	public void drawSelect(SpriteBatch batch, float stateTime) {

		// Converts the state time into a scale based on a cosine wave
		double breathingSpeed = stateTime * 2.5;
		double scaleDouble = (Math.cos(breathingSpeed) / 2) + 0.75;
		float scale = (float) (scaleDouble);
		batch.draw(new TextureRegion(selectTexture), x, y, PLAYER_SIZE / 2,
				PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE, scale, scale, 0);
	}

	public void dispose() {
		notificationTexture.dispose();
		hoverTexture.dispose();
		walkSheet.dispose();
	}

	public void move(Vector2[] path) {
		this.path = path;
	}

	public void kick(Ball ball, Vector2 target) {
		if (hasBall()) {

			Vector2 ballVelocity = Utils.getMoveVector(ball.getBallPosition(),
					target, shootSpeed);
			ball.move(ballVelocity);
			rotation = ballVelocity.angle();
			ball.resetTimeSinceTackle();
			timeSinceKick = 0;
			ball.removeOwner();
		}
		if (action.getNextAction() != null) {
			executeNextAction();
		}
	}

	public void shortKick(Ball ball, Vector2 target) {
		if (hasBall()) {

			Vector2 movementVector = new Vector2(target.x
					- ball.getBallPosition().x, target.y
					- ball.getBallPosition().y);

			// equations of motion -> v^2 = 2ax
			double targetSpeed = Math.sqrt(2 * ball.getDeceleration()
					* movementVector.dst(Vector2.Zero));

			double lowestSpeed = Math.min(targetSpeed, shootSpeed);

			Vector2 ballVelocity = Utils.getMoveVector(getPlayerPosition(),
					target, (float) lowestSpeed);
			ball.move(ballVelocity);
			rotation = ballVelocity.angle();
			ball.resetTimeSinceTackle();
			timeSinceKick = 0;
			ball.removeOwner();
		}
		if (action.getNextAction() != null) {
			executeNextAction();
		}
	}

	public void mark(Player target, Ball ball) {

		Vector2 markPosition = target.getPlayerPosition().add(
				Utils.getMoveVector(target.getPlayerPosition(),
						ball.getBallPosition(), 32));

		this.path = new Vector2[] { markPosition };

		if (hasBall()) {
			executeNextAction();
			path = null;
		}
	}

	public void followBall(Ball target) {
		this.path = new Vector2[] { target.getBallPosition() };

		if (hasBall()) {
			executeNextAction();
			path = null;
		}
	}

	public void pass(Ball ball, Player target) {
		if (hasBall()) {

			float initialDistance = ball.getBallPosition().dst(
					target.getPlayerPosition());

			// equations of motion -> v^2 - u^2 = 2ax
			// u^2 = v^2 - 2ax

			// The ideal initial speed is where the ball reaches the target and
			// meets (ball speed - target's savingSkill <= 100).
			// Don't pass with an initial speed faster than this.
			// Note: a target moving towards the ball may have a negligible
			// failure rate

			float idealFinalSpeed = target.getSavingSkill() - 20;

			float idealInitialSpeed = (float) Math.sqrt(idealFinalSpeed
					* idealFinalSpeed
					- (2 * (-ball.getDeceleration() * initialDistance)));

			float lowestSpeed = Math.min(idealInitialSpeed, shootSpeed);

			float time = initialDistance / lowestSpeed;
			Vector2 targetFuturePosition = target.getFuturePosition(time,
					target.getPositionInPath());

			// repeat once with new time for more accuracy
			time = targetFuturePosition.dst(ball.getBallPosition())
					/ lowestSpeed;
			targetFuturePosition = target.getFuturePosition(time,
					target.getPositionInPath());

			Vector2 ballVelocity = Utils.getMoveVector(ball.getBallPosition(),
					targetFuturePosition, lowestSpeed);

			ball.move(ballVelocity);
			rotation = ballVelocity.angle();
			ball.resetTimeSinceTackle();
			timeSinceKick = 0;
			ball.removeOwner();
		}
		if (action.getNextAction() != null) {
			executeNextAction();
		}
	}

	protected void executeNextAction() {
		if (action == null) {
			return;
		}
		if (action.getNextAction() == null) {
			action = null;
		} else {
			action = action.getNextAction();
			action.execute(this);
		}
	}

	public void update(float time) {

		Vector2 position = moveAlongPath(time);
		executeAction();

		timeSinceKick = timeSinceKick + time;
		timeSinceFailedTackle = timeSinceFailedTackle + time;
		notificationTime -= (notificationTime > 0) ? time : 0;

		this.x = Player.translatePlayerCoordinate(position.x);
		this.y = Player.translatePlayerCoordinate(position.y);
		// restrictToField();
	}

	/**
	 * Overview: We loop through each of the points in the list, if they are are
	 * within range set our players position to be that point keep going until
	 * either we run out of distance or we can't reach the next point in which
	 * case move towards it using a utility method
	 */
	protected Vector2 moveAlongPath(float time) {
		float distance = time * runSpeed;
		Vector2 position = getPlayerPosition();
		Vector2 oldPosition = position.cpy();

		while (distance > 0 && path != null && path.length > 0
				&& positionInPath < path.length) {

			Vector2 target = path[positionInPath];
			if (position.dst(target) < distance) {
				distance -= position.dst(target);
				position.set(target);
				positionInPath++;
				rotation = Utils.getMoveVector(oldPosition, target, distance)
						.angle();
				if (positionInPath == path.length) {
					path = new Vector2[] {};
					resetPathIndex();
					if (action instanceof Move
							|| action instanceof MoveToPosition) {
						executeNextAction();
					}
					break;
				}
			} else {
				// Move towards the next position (which is out of reach).
				Vector2 movement = Utils.getMoveVector(position, target,
						distance);
				position.add(movement);
				rotation = movement.angle();
				break;
			}
		}

		if (oldPosition.dst(position) > 0) {
			this.stateTime += time;
			this.currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		}
		return position;
	}

	public boolean hasBall() {
		return ball != null;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public void removeBall() {
		this.ball = null;
	}

	public LinkedList<Action> getActions() {
		LinkedList<Action> temp = new LinkedList<Action>();
		if (action != null) {
			temp.add(action);
			temp.addAll(action.getActions());
		}
		return temp;
	}

	public boolean kicksBall() {
		List<Action> actions = getActions();
		Gdx.app.log("Player", "found: " + actions.size() + " actions");
		for (Action action : actions) {
			if (action instanceof Kick || action instanceof Pass) {
				return true;
			}
		}
		Gdx.app.log("Player", "player doesn't kick ball.. returning false");
		return false;
	}

	public Rectangle getTackleHitbox() {
		return new Rectangle(getPlayerPosition().x - 12,
				getPlayerPosition().y - 12, 24, 24);
	}

	public void setRotation(float value) {
		rotation = value;
	}

	private void restrictToField() {
		this.x = this.x > (Game.VIRTUAL_SCREEN_WIDTH - PLAYER_SIZE) ? Game.VIRTUAL_SCREEN_WIDTH
				- PLAYER_SIZE
				: this.x;
		this.y = this.y > (Game.VIRTUAL_SCREEN_HEIGHT - PLAYER_SIZE) ? Game.VIRTUAL_SCREEN_HEIGHT
				- PLAYER_SIZE
				: this.y;

		this.x = this.x < (0 + PLAYER_SIZE) ? 0 + PLAYER_SIZE : this.x;
		this.y = this.y < (0 + PLAYER_SIZE) ? 0 + PLAYER_SIZE : this.y;
	}

	public Vector2[] getTimeLinePoints() {
		return new Vector2[] { getFuturePosition(1, true),
				getFuturePosition(2, true), getFuturePosition(3, true),
				getFuturePosition(4, true), getFuturePosition(5, true) };
	}

}
