package com.samsung.comp.football.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;

/**
 * This class creates and updates the players database and is heavily inspired
 * by: http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class PlayerDatabaseHelper extends SQLiteOpenHelper {

	public static final String PLAYER_TABLE_NAME = "player";

	public static final String ID_COLUMN_NAME = "_id";
	public static final String PLAYER_NAME_COLUMN_NAME = "name";

	public static final String PURCHASED_COLUMN_NAME = "purchased";
	public static final String SHOOT_SPEED_COLUMN_NAME = "shoot_speed";
	public static final String RUN_SPEED_COLUMN_NAME = "run_speed";
	public static final String TACKLE_SKILL_COLUMN_NAME = "tackle_skill";
	public static final String TACKLE_PREVENTION_SKILL_COLUMN_NAME = "tackle_prevention";
	public static final String SAVING_SKILL_COLUMN_NAME = "saving_skill";
	public static final String TEAM_ID_COLUMN_NAME = "team_id";
	public static final String GOALIE_COLUMN_NAME = "goalie";

	public static final String DATABASE_NAME = "database.db";
	public static final int DATABASE_VERSION = 2;

	private static final String CREATE_PLAYER_TABLE = "create table "
			+ PLAYER_TABLE_NAME + "(" + ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYER_NAME_COLUMN_NAME
			+ " text not null, " + PURCHASED_COLUMN_NAME
			+ " integer not null, " + SHOOT_SPEED_COLUMN_NAME
			+ " real not null, " + RUN_SPEED_COLUMN_NAME + " real not null, "
			+ TACKLE_SKILL_COLUMN_NAME + " real not null, "
			+ TACKLE_PREVENTION_SKILL_COLUMN_NAME + " real not null, "
			+ SAVING_SKILL_COLUMN_NAME + " real not null, "
			+ TEAM_ID_COLUMN_NAME + " integer not null, " + GOALIE_COLUMN_NAME
			+ " integer not null" + "FOREIGN KEY(" + TEAM_ID_COLUMN_NAME
			+ ") REFERENCES " + TeamsTableManager.TEAMS_TABLE_NAME + "("
			+ TeamsTableManager.TEAM_ID_COLUMN_NAME + ")" + ");";

	public PlayerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		ProfileTableManager.onCreate(database);
		TeamsTableManager.onCreate(database);

		database.execSQL(CREATE_PLAYER_TABLE);
		addDefaultPlayers(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant

		ProfileTableManager.onUpgrade(db, oldVersion, newVersion);
		TeamsTableManager.onUpgrade(db, oldVersion, newVersion);

		Log.w(PlayerDatabaseHelper.class.getName(),
		        "Upgrading players table from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME);
		    onCreate(db);
	}

	private void addDefaultPlayers(SQLiteDatabase database) {

		Player[] players = {
				new Player(1, "Steve", true, 520.0f, 150.0f, 100.0f, 420.0f, 1),
				new Player(2, "Alex", true, 540.0f, 200.0f, 80.0f, 380.0f, 1),
				new Player(3, "Thomas", true, 550.0f, 100.0f, 100.0f, 420.0f, 1),
				new Player(4, "Samuel", true, 530.0f, 150.0f, 80.0f, 420.0f, 1),
				new Goalie(5, "David", true, 520.0f, 150.0f, 100.0f, 500.0f, 1),

				new Player(6, "Bartholomew", true, 520.0f, 150.0f, 100.0f,
						420.0f, 0),
				new Player(7, "Edgar", true, 540.0f, 200.0f, 80.0f, 380.0f, 0),
				new Player(8, "Oswald", true, 550.0f, 100.0f, 100.0f, 420.0f, 0),
				new Player(9, "Quinten", true, 530.0f, 150.0f, 80.0f, 420.0f, 0),
				new Goalie(10, "Victor", true, 520.0f, 150.0f, 100.0f, 500.0f,
						0) };
		
		
		for (Player player : players) {
			insertPlayer(database, player);
		}
	}

	private static void insertPlayer(SQLiteDatabase database, Player player) {
		Log.v(PlayerDataSource.class.toString(),
				"adding player: " + player.toString());
		ContentValues values = new ContentValues();
		values.put(PLAYER_NAME_COLUMN_NAME, player.getName());
		values.put(PURCHASED_COLUMN_NAME, player.isPurchased() ? 1 : 0);
		values.put(SHOOT_SPEED_COLUMN_NAME, player.getShootSpeed());
		values.put(RUN_SPEED_COLUMN_NAME, player.getRunSpeed());
		values.put(TACKLE_SKILL_COLUMN_NAME, player.getTackleSkill());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkill());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());
		int isGoalie = player.isGoalie() ? 1 : 0;
		values.put(GOALIE_COLUMN_NAME, isGoalie);
		Log.v("db", "team_id: " + player.getTeamID());
		database.insert(PLAYER_TABLE_NAME, null, values);
	}

}
