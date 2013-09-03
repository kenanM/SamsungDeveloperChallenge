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
				// Stats 1-10
				new Player(0, "Harvey", 5, 6, 2, 2, 2, 30000),
				new Player(0, "Aaron", 1, 9, 3, 1, 2, 20000),
				new Player(0, "Dirty Harry", 3, 2, 10, 2, 2, 30000),
				new Player(0, "Will", 2, 7, 9, 2, 2, 30000),
				new Player(0, "Joules", 1, 1, 1, 10, 2, 30000),

				// Stats 5-10
				new Player(0, "Tristan", 10, 9, 5, 5, 2, 30000),
				new Player(0, "'Lucky'", 7, 7, 7, 7, 2, 30000),
				new Player(0, "Simon", 5, 5, 5, 5, 9, 30000),
				new Player(0, "'Jester'", 1, 9, 8, 5, 2, 30000),
				new Player(0, "Joey", 4, 9, 5, 7, 2, 30000),

				// Stats 5-15
				new Player(0, "Jonathan", 14, 8, 6, 9, 2, 30000),
				new Player(0, "Neil", 12, 11, 6, 7, 2, 30000),
				new Player(0, "Nicholas", 5, 12, 15, 9, 2, 30000),
				new Player(0, "Nick", 13, 5, 15, 14, 2, 30000),
				new Player(0, "Chinese Dave", 8, 12, 10, 12, 2, 30000),

				// Stats 10-15
				new Player(0, "Oswald", 15, 11, 10, 10, 2, 30000),
				new Player(0, "Cecil", 10, 15, 10, 11, 2, 30000),
				new Player(0, "Victor", 12, 12, 13, 13, 2, 30000),
				new Player(0, "Quinten", 15, 10, 15, 15, 2, 30000),
				new Player(0, "Ajay", 10, 13, 11, 15, 2, 30000),

				// Stats 10-20
				new Player(0, "Abel", 18, 13, 10, 14, 2, 30000),
				new Player(0, "Leo", 15, 16, 12, 12, 2, 30000),
				new Player(0, "Howard", 11, 15, 19, 13, 2, 30000),
				new Player(0, "Geoff", 10, 20, 15, 12, 2, 30000),
				new Player(0, "'Cyclone' Seth", 20, 20, 20, 16, 2, 30000),

				// Stats 15-20
				new Player(0, "'Quick Shot' Bart", 20, 20, 15, 15, 2, 30000),
				new Player(0, "Peter", 16, 17, 19, 15, 2, 30000),
				new Player(0, "Mike", 15, 16, 19, 18, 2, 30000),
				new Player(0, "Chris", 15, 18, 19, 19, 2, 30000),
				new Player(0, "Lee", 15, 18, 16, 20, 2, 30000),

				// Stats 15-25
				new Player(0, "Russel", 24, 17, 16, 16, 2, 30000),
				new Player(0, "Zidane", 18, 23, 18, 19, 2, 30000),
				new Player(0, "Nick", 18, 18, 24, 21, 2, 30000),
				new Player(0, "'Rampage' Rufus", 16, 22, 25, 15, 2, 30000),
				new Player(0, "Ken", 19, 21, 15, 22, 2, 30000),

				// Stats 20+
				new Player(0, "'Cannon Ball' Jake", 25, 25, 20, 20, 2, 30000),
				new Player(0, "Alex", 25, 22, 20, 21, 2, 30000),
				new Player(0, "Lance", 25, 20, 21, 23, 2, 30000),
				new Player(0, "'Apples'", 22, 24, 22, 22, 2, 30000),
				new Player(0, "Louis", 20, 21, 25, 24, 2, 30000),
				new Player(0, "Rick", 20, 25, 20, 25, 2, 30000),

				// The Barnsbury Blunderers (Team ID 3)
				new Player(0, "Luke", 2, 1, 1, 1, 3, 2000),
				new Player(0, "Billy", 2, 1, 1, 1, 3, 2000),
				new Player(0, "Alfie", 1, 2, 1, 1, 3, 2000),
				new Player(0, "Owen", 1, 3, 3, 1, 3, 2000),
				new Goalie(0, "Amiable Andy", 1, 1, 4, 1, 3, 2000),

				// Scouting for goals (Team ID 4)
				new Player(0, "Oliver", 8, 5, 5, 7, 4, 2000),
				new Player(0, "Madman Max", 7, 6, 6, 6, 4, 2000),
				new Player(0, "Tiny Tom", 3, 9, 6, 5, 4, 2000),
				new Player(0, "Fred", 5, 5, 10, 8, 4, 2000),
				new Goalie(0, "David", 1, 8, 8, 1, 4, 2000),

				// Goal Direction (Team ID 5)
				new Player(0, "Krazy Kevin", 11, 11, 6, 4, 5, 2000),
				new Player(0, "Aiden", 5, 9, 2, 9, 5, 2000),
				new Player(0, "Ryan", 8, 8, 8, 9, 5, 2000),
				new Player(0, "Scott", 8, 6, 6, 7, 5, 2000),
				new Goalie(0, "Matthew", 10, 1, 10, 10, 5, 2000),

				// Random Rovers (Team ID 6)
				new Player(0, "Mad Michael", 15, 7, 15, 10, 6, 2000),
				new Player(0, "Riley", 11, 14, 8, 12, 6, 2000),
				new Player(0, "Ben", 7, 11, 12, 9, 6, 2000),
				new Player(0, "Dylan", 13, 8, 16, 6, 6, 2000),
				new Goalie(0, "Frank", 10, 12, 11, 12, 6, 2000),

				// Real Reunited (Team ID 7) - Medium Difficulty, stats 15+
				new Player(0, "Angry Adam ", 18, 16, 19, 15, 7, 2000),
				new Player(0, "James ", 15, 19, 15, 15, 7, 2000),
				new Player(0, "William", 15, 15, 19, 15, 7, 2000),
				new Player(0, "Zain", 15, 16, 21, 19, 7, 2000),
				new Goalie(0, "David", 18, 20, 19, 8, 7, 2000),

				// Fake Madrid (Team ID 8)
				new Player(0, "Guy", 15, 20, 16, 15, 8, 2000),
				new Player(0, "Felix", 16, 18, 17, 15, 8, 2000),
				new Player(0, "Nathan", 17, 17, 16, 16, 8, 2000),
				new Player(0, "Trevor", 16, 16, 18, 18, 8, 2000),
				new Goalie(0, "Big Steve", 17, 11, 20, 21, 8, 2000),

				// Men Reunited (Team ID 9)
				new Player(0, "Naughty Nev", 19, 21, 17, 15, 9, 2000),
				new Player(0, "Harry", 18, 22, 16, 16, 9, 2000),
				new Player(0, "Jack", 19, 21, 18, 15, 9, 2000),
				new Player(0, "Charlie", 19, 20, 16, 15, 9, 2000),
				new Goalie(0, "Jacob", 23, 19, 19, 20, 9, 2000),

				// Malden Mavericks (Team ID 10)
				new Player(0, "Troubled Trevor", 25, 15, 19, 20, 10, 2000),
				new Player(0, "Ross", 24, 15, 21, 15, 10, 2000),
				new Player(0, "Michael", 15, 21, 21, 21, 10, 2000),
				new Player(0, "Henry", 15, 21, 25, 15, 10, 2000),
				new Goalie(0, "Richard", 15, 22, 25, 21, 10, 2000),

				// Team Null (Team ID 11)
				new Player(0, "Gavin the Great", 24, 25, 21, 20, 11, 2000),
				new Player(0, "Kenan the Koder", 21, 23, 21, 23, 11, 2000),
				new Player(0, "Liz la Artiste", 21, 24, 22, 25, 11, 2000),
				new Player(0, "Allan the Tackler", 21, 25, 25, 21, 11, 2000),
				new Goalie(0, "Stephen the Swift", 21, 25, 21, 25, 11, 2000) };
		
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

