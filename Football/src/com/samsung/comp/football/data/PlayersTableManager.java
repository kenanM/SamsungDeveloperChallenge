package com.samsung.comp.football.data;


import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;

/**
 * A helper class for querying and other iterations with the SQLite Players
 * table.
 */
public class PlayersTableManager {

	protected static final String PLAYER_TABLE_NAME = "player";

	protected static final String ID_COLUMN_NAME = "_id";
	protected static final String PLAYER_NAME_COLUMN_NAME = "name";

	protected static final String SHOOT_SPEED_COLUMN_NAME = "shoot_speed";
	protected static final String RUN_SPEED_COLUMN_NAME = "run_speed";
	protected static final String TACKLE_SKILL_COLUMN_NAME = "tackle_skill";
	protected static final String SAVING_SKILL_COLUMN_NAME = "saving_skill";
	protected static final String TEAM_ID_COLUMN_NAME = "team_id";
	protected static final String COST_COLUMN_NAME = "cost";

	private static final String CREATE_PLAYER_TABLE = "create table "
			+ PLAYER_TABLE_NAME + "(" + ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYER_NAME_COLUMN_NAME
			+ " text not null, " + SHOOT_SPEED_COLUMN_NAME + " real not null, "
			+ RUN_SPEED_COLUMN_NAME + " real not null, "
			+ TACKLE_SKILL_COLUMN_NAME + " real not null, "
			+ SAVING_SKILL_COLUMN_NAME + " real not null, "
			+ TEAM_ID_COLUMN_NAME + " integer not null, " + COST_COLUMN_NAME
			+ " integer not null, "

			+ "FOREIGN KEY(" + TEAM_ID_COLUMN_NAME + ") REFERENCES "
			+ TeamsTableManager.TEAMS_TABLE_NAME + "("
			+ TeamsTableManager.TEAM_ID_COLUMN_NAME + ")" + ");";

	private static final String[] allColumns = { ID_COLUMN_NAME,
			PLAYER_NAME_COLUMN_NAME, SHOOT_SPEED_COLUMN_NAME,
			RUN_SPEED_COLUMN_NAME, TACKLE_SKILL_COLUMN_NAME,
			SAVING_SKILL_COLUMN_NAME, TEAM_ID_COLUMN_NAME, COST_COLUMN_NAME };

	SQLiteDatabase database;

	protected PlayersTableManager(SQLiteDatabase database) {
		this.database = database;
	}

	protected static void onCreate(SQLiteDatabase database) {
		Log.v("GameDB", "Creating Players Table...");
		database.execSQL(CREATE_PLAYER_TABLE);
		addDefaultPlayers(database);
	}

	protected static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant

		Log.w(PlayerDatabaseHelper.class.getName(),
				"Upgrading players table from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME);
		onCreate(db);
	}

	protected static void dropAndRecreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME);
		onCreate(db);
	}

	private static void addDefaultPlayers(SQLiteDatabase database) {

		Player[] players = {

				// Player Store (Team ID 2)
				new Player(0, "A", 530.0f, 150.0f, 80.0f, 420.0f, 2, 30000),
				new Goalie(0, "B", 520.0f, 150.0f, 100.0f, 500.0f, 2, 20000),

				// The Misfits (Team ID 3)
				new Player(0, "Steve", 520.0f, 150.0f, 100.0f, 420.0f, 3, 2000),
				new Player(0, "Alex", 540.0f, 200.0f, 80.0f, 380.0f, 3, 2000),
				new Player(0, "Thomas", 550.0f, 100.0f, 100.0f, 420.0f, 3, 2000),
				new Player(0, "Samuel", 530.0f, 150.0f, 80.0f, 420.0f, 3, 2000),
				new Goalie(0, "David", 520.0f, 150.0f, 100.0f, 500.0f, 3, 2000),

				// Human 1 (Team ID 1)
				new Player(0, "Bartholomew", 520.0f, 150.0f, 100.0f, 420.0f, 1,
						0),
				new Player(0, "Edgar", 540.0f, 200.0f, 80.0f, 380.0f, 1, 0),
				new Player(0, "Oswald", 550.0f, 100.0f, 100.0f, 420.0f, 1, 0),
				new Player(0, "Quinten", 530.0f, 150.0f, 80.0f, 420.0f, 1, 0),
				new Goalie(0, "Victor", 520.0f, 150.0f, 100.0f, 500.0f, 1, 0) };

		for (Player player : players) {
			insertPlayer(database, player);
		}
	}

	private static long insertPlayer(SQLiteDatabase database, Player player) {
		Log.v(PlayerDataSource.class.toString(),
				"adding player: " + player.toString());
		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeed());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeed());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkill());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkill());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());
		values.put(COST_COLUMN_NAME, player.getPlayerCost());
		Log.v("db", "player's team_id: " + player.getTeamID());
		return database.insert(PLAYER_TABLE_NAME, null, values);
	}

	/**
	 * Extract a Player object from a cursor pointing at a row in the player
	 * table
	 * 
	 * @param returnAsPlayer
	 *            If true, returns object as instance of Player. Otherwise
	 *            object is returned as its subclass.
	 */
	private static Player cursorToPlayer(Cursor cursor, boolean returnAsPlayer) {
		String name = cursor.getString(cursor
				.getColumnIndex(PLAYER_NAME_COLUMN_NAME));
		float shootSpeed = cursor.getFloat(cursor
				.getColumnIndex(SHOOT_SPEED_COLUMN_NAME));
		float runSpeed = cursor.getFloat(cursor
				.getColumnIndex(RUN_SPEED_COLUMN_NAME));
		float tackleSkill = cursor.getFloat(cursor
				.getColumnIndex(TACKLE_SKILL_COLUMN_NAME));
		float savingSkill = cursor.getFloat(cursor
				.getColumnIndex(SAVING_SKILL_COLUMN_NAME));
		int teamId = cursor.getInt(cursor.getColumnIndex(TEAM_ID_COLUMN_NAME));
		int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));

		int cost = cursor.getInt(cursor.getColumnIndex(COST_COLUMN_NAME));

		if (!returnAsPlayer) {
			return new Goalie(id, name, shootSpeed, runSpeed,
					tackleSkill, savingSkill, teamId, cost);
		} else {
			return new Player(id, name, shootSpeed, runSpeed,
					tackleSkill, savingSkill, teamId, cost);
		}
	}

	/**
	 * Extract a Goalie object from a cursor pointing at a row in the player
	 * table
	 */
	private static Goalie cursorToGoalie(Cursor cursor) {
		String name = cursor.getString(cursor
				.getColumnIndex(PLAYER_NAME_COLUMN_NAME));
		float shootSpeed = cursor.getFloat(cursor
				.getColumnIndex(SHOOT_SPEED_COLUMN_NAME));
		float runSpeed = cursor.getFloat(cursor
				.getColumnIndex(RUN_SPEED_COLUMN_NAME));
		float tackleSkill = cursor.getFloat(cursor
				.getColumnIndex(TACKLE_SKILL_COLUMN_NAME));
		float savingSkill = cursor.getFloat(cursor
				.getColumnIndex(SAVING_SKILL_COLUMN_NAME));
		int teamId = cursor.getInt(cursor.getColumnIndex(TEAM_ID_COLUMN_NAME));
		int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
		int cost = cursor.getInt(cursor.getColumnIndex(COST_COLUMN_NAME));

		return new Goalie(id, name, shootSpeed, runSpeed,
				tackleSkill, savingSkill, teamId, cost);

	}

	public long insertPlayer(Player player) {
		return insertPlayer(database, player);
	}

	/**
	 * Returns all players within a team from the DB
	 * 
	 * @param teamID
	 *            the team to return
	 * @return
	 */
	public List<Player> getPlayers(int teamID) {
		Cursor cursor = database.query(PLAYER_TABLE_NAME, null,
				TEAM_ID_COLUMN_NAME + "=?",
				new String[] { Integer.toString(teamID) }, null, null, null);

		LinkedList<Player> result = new LinkedList<Player>();

		while (cursor.moveToNext() && cursor != null) {
			result.add(cursorToPlayer(cursor, true));
		}

		Log.v("GameDB", "Found a total of " + result.size()
				+ " players for team " + teamID);
		return result;
	}

	public void updatePlayer(Player player) {
		Log.v(PlayerDataSource.class.toString(), "updating player...ID = "
				+ player.getID() + ", name =" + player.getName());

		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeed());
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeed());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkill());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkill());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());
		values.put(COST_COLUMN_NAME, player.getPlayerCost());

		long updateCount = database
				.update(PLAYER_TABLE_NAME, values, "?=?", new String[] {
						ID_COLUMN_NAME, String.valueOf(player.getID()) });
		Log.v("GameDB", "Number of rows updated = " + updateCount);
	}

}

