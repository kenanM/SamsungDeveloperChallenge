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

				
				// Human 1 (Team ID 1)
				new Player(0, "Donald", 1, 4, 1, 1, 1, 0),
				new Player(0, "Bobby", 1, 2, 2, 1, 1, 0),
				new Player(0, "Dexter", 1, 3, 1, 1, 1, 0),
				new Player(0, "Edward", 1, 3, 3, 1, 1, 0),
				new Goalie(0, "Rory", 1, 2, 1, 3, 1, 0),

				// Player Store (Team ID 2)
				new Player(0, "Harvey", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Aaron", 3, 2, 1, 1, 2, 20000),
				new Player(0, "Dirty Harry", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Will", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Joules", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Tristan", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Cannon Ball' Jake", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Lucky'", 7, 7, 7, 7, 2, 30000),
				new Player(0, "Simon", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Jonathan", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Neil", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Nicholas", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Nick", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Chinese Dave", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Oswald", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Cecil", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Victor", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Quinten", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Alex", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Abel", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Cyclone' Seth", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Leo", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Howard", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Geoff", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Mike", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Peter", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Quick Shot' Bart", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Chris", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Lee", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Zidane", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Russel", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Nick", 4, 2, 1, 1, 2, 30000),
				new Player(0, "Ken", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Rampage' Rufus", 4, 2, 1, 1, 2, 30000),

				new Player(0, "Alex", 4, 2, 1, 1, 2, 30000),
				new Player(0, "'Apples'", 4, 2, 1, 1, 2, 30000),

				// The Barnsbury Blunderers (Team ID 3)
				new Player(0, "Luke", 2, 1, 1, 1, 3, 2000),
				new Player(0, "Billy", 2, 1, 1, 1, 3, 2000),
				new Player(0, "Alfie", 1, 2, 1, 1, 3, 2000),
				new Player(0, "Owen", 1, 3, 3, 1, 3, 2000),
				new Goalie(0, "Amiable Andy", 1, 1, 4, 1, 3, 2000),

				// Scouting for goals (Team ID 4)
				new Player(0, "Oliver", 8, 1, 1, 1, 2000),
				new Player(0, "Madman Max", 4, 4, 1, 1, 4, 2000),
				new Player(0, "Tiny Tom", 3, 5, 2, 1, 4, 2000),
				new Player(0, "Fred", 5, 3, 3, 1, 4, 2000),
				new Goalie(0, "David", 1, 8, 8, 1, 4, 2000),

				// Goal Direction (Team ID 5)
				new Player(0, "Krazy Kevin", 7, 7, 7, 4, 5, 2000),
				new Player(0, "Aiden", 540.0f, 5, 80.0f, 5, 5, 2000),
				new Player(0, "Ryan", 550.0f, 5, 100.0f, 2, 5, 2000),
				new Player(0, "Scott", 530.0f, 150.0f, 10, 3, 5, 2000),
				new Goalie(0, "Matthew", 10, 1, 10, 10, 5, 2000),

				// Random Rovers (Team ID 6)
				new Player(0, "Mad Michael", 520.0f, 150.0f, 100.0f, 420.0f, 6, 2000),
				new Player(0, "Riley", 540.0f, 200.0f, 80.0f, 380.0f, 6, 2000),
				new Player(0, "Ben", 550.0f, 100.0f, 100.0f, 420.0f, 6, 2000),
				new Player(0, "Dylan", 530.0f, 150.0f, 80.0f, 420.0f, 6, 2000),
				new Goalie(0, "Frank", 520.0f, 150.0f, 100.0f, 500.0f, 6, 2000),

				// Real Reunited (Team ID 7)
				new Player(0, "Angry Adam ", 520.0f, 150.0f, 100.0f, 420.0f, 7, 2000),
				new Player(0, "James ", 540.0f, 200.0f, 80.0f, 380.0f, 7, 2000),
				new Player(0, "William", 550.0f, 100.0f, 100.0f, 420.0f, 7, 2000),
				new Player(0, "Zain", 530.0f, 150.0f, 80.0f, 420.0f, 7, 2000),
				new Goalie(0, "David", 520.0f, 150.0f, 100.0f, 500.0f, 7, 2000),

				// Fake Madrid (Team ID 8)
				new Player(0, "Troubled Trevor", 520.0f, 150.0f, 100.0f, 420.0f, 8, 2000),
				new Player(0, "Felix", 540.0f, 200.0f, 80.0f, 380.0f, 8, 2000),
				new Player(0, "Nathan", 550.0f, 100.0f, 100.0f, 420.0f, 8, 2000),
				new Player(0, "Trevor", 530.0f, 150.0f, 80.0f, 420.0f, 8, 2000),
				new Goalie(0, "Louis", 520.0f, 150.0f, 100.0f, 500.0f, 8, 2000),

				// Men Reunited (Team ID 9)
				new Player(0, "Naughty Nev", 520.0f, 150.0f, 100.0f, 420.0f, 9, 2000),
				new Player(0, "Harry", 540.0f, 200.0f, 80.0f, 380.0f, 9, 2000),
				new Player(0, "Jack", 550.0f, 100.0f, 100.0f, 420.0f, 9, 2000),
				new Player(0, "Charlie", 530.0f, 150.0f, 80.0f, 420.0f, 9, 2000),
				new Goalie(0, "Jacob", 520.0f, 150.0f, 100.0f, 500.0f, 9, 2000),

				// Malden Mavericks (Team ID 10)
				new Player(0, "Guy", 520.0f, 150.0f, 100.0f, 420.0f, 10, 2000),
				new Player(0, "Ross", 540.0f, 200.0f, 80.0f, 380.0f, 10, 2000),
				new Player(0, "Michael", 550.0f, 100.0f, 100.0f, 420.0f, 10, 2000),
				new Player(0, "Henry", 530.0f, 150.0f, 80.0f, 420.0f, 10, 2000),
				new Goalie(0, "Richard", 520.0f, 150.0f, 100.0f, 500.0f, 10, 2000),

				// Team Null (Team ID 11)
				new Player(0, "Gavin the Great", 520.0f, 150.0f, 100.0f, 420.0f, 11, 2000),
				new Player(0, "Kenan the Koder", 540.0f, 200.0f, 80.0f, 380.0f, 11, 2000),
				new Player(0, "Liz la Artiste", 550.0f, 100.0f, 100.0f, 420.0f, 11, 2000),
				new Player(0, "Allan the Tackler", 530.0f, 150.0f, 80.0f, 420.0f, 11, 2000),
				new Goalie(0, "Stephen the Swift", 520.0f, 150.0f, 100.0f, 500.0f, 11, 2000)};
		
		for (Player player : players) {
			insertPlayer(database, player);
		}
	}

	private static long insertPlayer(SQLiteDatabase database, Player player) {
		Log.v(PlayerDataSource.class.toString(),
				"adding player: " + player.toString());
		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeedBarCount());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeedBarCount());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkillBarCount());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkillBarCount());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());
		values.put(COST_COLUMN_NAME, player.getPlayerCost());
		Log.v("db", "player's team_id: " + player.getTeamID());
		return database.insert(PLAYER_TABLE_NAME, null, values);
	}

	protected static List<Player> getPlayers(SQLiteDatabase database, int teamID) {
		Cursor cursor = database.query(PLAYER_TABLE_NAME, null,
				TEAM_ID_COLUMN_NAME + "=?",
				new String[] { Integer.toString(teamID) }, null, null, null);

		LinkedList<Player> result = new LinkedList<Player>();

		while (cursor.moveToNext() && cursor != null) {
			result.add(cursorToPlayer(cursor));
		}

		Log.v("GameDB", "Found a total of " + result.size()
				+ " players for team " + teamID);
		return result;
	}

	protected static Player getPlayer(SQLiteDatabase database, int playerID) {
		Cursor cursor = database.query(PLAYER_TABLE_NAME, null, ID_COLUMN_NAME
				+ "=?", new String[] { Integer.toString(playerID) }, null,
				null, null);
		return cursor.moveToFirst() ? cursorToPlayer(cursor) : null;
	}

	/**
	 * Extract a Player object from a cursor pointing at a row in the player
	 * table
	 * 
	 * @param returnAsPlayer
	 *            If true, returns object as instance of Player. Otherwise
	 *            object is returned as its subclass.
	 */
	private static Player cursorToPlayer(Cursor cursor) {
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

		return new Player(id, name, shootSpeed, runSpeed, tackleSkill,
				savingSkill, teamId, cost);

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
		return getPlayers(database, teamID);
	}

	public Player getPlayer(int playerID) {
		return getPlayer(database, playerID);
	}

	public void updatePlayer(Player player) {
		Log.v("GameDB", "updating player...ID = "
				+ player.getID() + ", name =" + player.getName());

		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeedBarCount());
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeedBarCount());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkillBarCount());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkillBarCount());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());
		values.put(COST_COLUMN_NAME, player.getPlayerCost());

		long updateCount = database.update(PLAYER_TABLE_NAME, values,
				ID_COLUMN_NAME + "=?",
				new String[] { String.valueOf(player.getID()) });
		Log.v("GameDB", "Number of rows updated = " + updateCount);
	}

}

