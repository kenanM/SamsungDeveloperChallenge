package com.samsung.comp.football.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Squad;

/**
 * A helper class for querying and other iterations with the SQLite Squads
 * table. A squad holds the players deployed on the pitch.
 */
public class SquadsTableManager {

	protected static final String SQUADS_TABLE_NAME = "squads";

	protected static final String ID_COLUMN_NAME = "_id";

	protected static final String STRIKER_COLUMN_NAME = "striker";
	protected static final String MIDFIELD_A_COLUMN_NAME = "midfielderA";
	protected static final String MIDFIELD_B_COLUMN_NAME = "midfielderB";
	protected static final String DEFENDER_COLUMN_NAME = "defender";
	protected static final String GOALKEEPER_COLUMN_NAME = "goalkeeper";

	private static final String CREATE_SQUADS_TABLE = "create table "
			+ SQUADS_TABLE_NAME + "(" + ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "

			+ STRIKER_COLUMN_NAME + " integer not null, "
			+ MIDFIELD_A_COLUMN_NAME + " integer not null, "
			+ MIDFIELD_B_COLUMN_NAME + " integer not null, "
			+ DEFENDER_COLUMN_NAME + " integer not null, "
			+ GOALKEEPER_COLUMN_NAME + " integer not null, "

			+ "FOREIGN KEY(" + ID_COLUMN_NAME + ") REFERENCES "
			+ TeamsTableManager.TEAMS_TABLE_NAME + "("
			+ TeamsTableManager.TEAM_ID_COLUMN_NAME + "), "

			+ "FOREIGN KEY(" + STRIKER_COLUMN_NAME + ") REFERENCES "
			+ PlayersTableManager.PLAYER_TABLE_NAME + "("
			+ PlayersTableManager.ID_COLUMN_NAME + "), "

			+ "FOREIGN KEY(" + MIDFIELD_A_COLUMN_NAME + ") REFERENCES "
			+ PlayersTableManager.PLAYER_TABLE_NAME + "("
			+ PlayersTableManager.ID_COLUMN_NAME + "), "

			+ "FOREIGN KEY(" + MIDFIELD_B_COLUMN_NAME + ") REFERENCES "
			+ PlayersTableManager.PLAYER_TABLE_NAME + "("
			+ PlayersTableManager.ID_COLUMN_NAME + "), "

			+ "FOREIGN KEY(" + DEFENDER_COLUMN_NAME + ") REFERENCES "
			+ PlayersTableManager.PLAYER_TABLE_NAME + "("
			+ PlayersTableManager.ID_COLUMN_NAME + "), "

			+ "FOREIGN KEY(" + GOALKEEPER_COLUMN_NAME + ") REFERENCES "
			+ PlayersTableManager.PLAYER_TABLE_NAME + "("
			+ PlayersTableManager.ID_COLUMN_NAME + ")"

			+ ");";

	private static final String[] allColumns = { ID_COLUMN_NAME,
			STRIKER_COLUMN_NAME, MIDFIELD_A_COLUMN_NAME,
			MIDFIELD_B_COLUMN_NAME, DEFENDER_COLUMN_NAME,
			GOALKEEPER_COLUMN_NAME };

	SQLiteDatabase database;

	protected SquadsTableManager(SQLiteDatabase database) {
		this.database = database;
	}

	protected static void onCreate(SQLiteDatabase database) {
		Log.v("GameDB", "Creating Squads Table...");
		database.execSQL(CREATE_SQUADS_TABLE);
		addDefaultSquads(database);
	}

	protected static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant

		Log.w(PlayerDatabaseHelper.class.getName(),
				"Upgrading squads table from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + SQUADS_TABLE_NAME);
		onCreate(db);
	}

	protected static void dropAndRecreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + SQUADS_TABLE_NAME);
		onCreate(db);
	}

	private static void addDefaultSquads(SQLiteDatabase database) {
		insertSquad(database, 1, 6, 7, 8, 9, 10);
	}

	private static long insertSquad(SQLiteDatabase database, Squad squad) {
		Log.v("GameDB", "inserting squad...");
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN_NAME, squad.getSquadID());
		values.put(STRIKER_COLUMN_NAME, squad.getStriker().getID());
		values.put(MIDFIELD_A_COLUMN_NAME, squad.getMidfieldA().getID());
		values.put(MIDFIELD_B_COLUMN_NAME, squad.getMidfieldB().getID());
		values.put(DEFENDER_COLUMN_NAME, squad.getDefender().getID());
		values.put(GOALKEEPER_COLUMN_NAME, squad.getGoalKeeper().getID());

		return database.insert(SQUADS_TABLE_NAME, null, values);
	}

	private static long insertSquad(SQLiteDatabase database, int id,
			int striker, int midA, int midB, int defender, int goalKeeper) {
		Log.v("GameDB", "inserting squad...");
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN_NAME, id);
		values.put(STRIKER_COLUMN_NAME, striker);
		values.put(MIDFIELD_A_COLUMN_NAME, midA);
		values.put(MIDFIELD_B_COLUMN_NAME, midB);
		values.put(DEFENDER_COLUMN_NAME, defender);
		values.put(GOALKEEPER_COLUMN_NAME, goalKeeper);

		return database.insert(SQUADS_TABLE_NAME, null, values);
	}

	/**
	 * Extract a Squad object from a cursor pointing at a row in the squads
	 * table
	 */
	private static Squad cursorToSquad(SQLiteDatabase database, Cursor cursor) {

		int squadID = cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME));
		int strikerID = cursor.getInt(cursor
				.getColumnIndex(STRIKER_COLUMN_NAME));
		int midFieldAID = cursor.getInt(cursor
				.getColumnIndex(MIDFIELD_A_COLUMN_NAME));
		int midFieldBID = cursor.getInt(cursor
				.getColumnIndex(MIDFIELD_B_COLUMN_NAME));
		int defenderID = cursor.getInt(cursor
				.getColumnIndex(DEFENDER_COLUMN_NAME));
		int goalKeeperID = cursor.getInt(cursor
				.getColumnIndex(GOALKEEPER_COLUMN_NAME));

		return new Squad(squadID, PlayersTableManager.getPlayer(database,
				strikerID),
				PlayersTableManager.getPlayer(database, midFieldAID),
				PlayersTableManager.getPlayer(database, midFieldBID),
				PlayersTableManager.getPlayer(database, defenderID),
				PlayersTableManager.getPlayer(database, goalKeeperID));
	}

	public long insertSquad(Squad squad) {
		return insertSquad(database, squad);
	}

	/**
	 * Returns the goalie within a team
	 * 
	 * @param teamID
	 *            the team to return
	 * @return
	 */
	public Squad getSquad(int teamID) {
		Cursor cursor = database.query(SQUADS_TABLE_NAME, allColumns,
				ID_COLUMN_NAME + "=?",
				new String[] { Integer.toString(teamID) }, null, null, null);

		return cursor.moveToFirst() ? cursorToSquad(database, cursor) : null;
	}

	public void updateSquad(Squad squad) {
		Log.v("GameDB", "updating squad...ID = " + squad.getSquadID());

		ContentValues values = new ContentValues();
		values.put(ID_COLUMN_NAME, squad.getSquadID());
		values.put(STRIKER_COLUMN_NAME, squad.getStriker().getID());
		values.put(MIDFIELD_A_COLUMN_NAME, squad.getMidfieldA().getID());
		values.put(MIDFIELD_B_COLUMN_NAME, squad.getMidfieldB().getID());
		values.put(DEFENDER_COLUMN_NAME, squad.getDefender().getID());
		values.put(GOALKEEPER_COLUMN_NAME, squad.getGoalKeeper().getID());

		long updateCount = database.update(
				SQUADS_TABLE_NAME,
				values,
				"?=?",
				new String[] { ID_COLUMN_NAME,
						String.valueOf(squad.getSquadID()) });
		Log.v("GameDB", "Number of rows updated = " + updateCount);
	}

}
