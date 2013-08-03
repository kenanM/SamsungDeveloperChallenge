package com.samsung.comp.football.data;

import static com.samsung.comp.football.data.PlayerDatabaseHelper.ID_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_NAME_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PLAYER_TABLE_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.PURCHASED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.RUN_SPEED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.SAVING_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.SHOOT_SPEED_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TACKLE_PREVENTION_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TACKLE_SKILL_COLUMN_NAME;
import static com.samsung.comp.football.data.PlayerDatabaseHelper.TEAM_ID_COLUMN_NAME;

import com.samsung.comp.football.Players.Goalie;
import com.samsung.comp.football.Players.Player;
import com.samsung.comp.football.Players.Player.TeamColour;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	public static final String GOALIE_COLUMN_NAME = "team_id";


	public static final String DATABASE_NAME = "database.db";
	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_PLAYER_TABLE = "create table "
			+ PLAYER_TABLE_NAME + "(" + ID_COLUMN_NAME
			+ " integer primary key autoincrement, " + PLAYER_NAME_COLUMN_NAME
			+ " text not null, " + PURCHASED_COLUMN_NAME
			+ " integer not null, " + SHOOT_SPEED_COLUMN_NAME
			+ " real not null, " + RUN_SPEED_COLUMN_NAME + " real not null, "
			+ TACKLE_SKILL_COLUMN_NAME + " real not null, "
			+ TACKLE_PREVENTION_SKILL_COLUMN_NAME + " real not null, "
			+ SAVING_SKILL_COLUMN_NAME + " real not null, " + TEAM_ID_COLUMN_NAME
			+ "integer not null, "+ GOALIE_COLUMN_NAME + " integer not null);";

	public PlayerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.v("db", "database helper constructor finishing");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.v("db", "oncreate called executing stuff");
		database.execSQL(CREATE_PLAYER_TABLE);
		addDefaultPlayers(database);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant
	}
	
	private void addDefaultPlayers(SQLiteDatabase database){
		
		
		Player[] players = {
				new Player(1, "Steve", true, 520.0f, 150.0f, 100.0f, 20.0f, 420.0f, 0),
				new Player(2, "Alex", true, 540.0f, 200.0f, 80.0f, 20.0f, 380.0f, 0),
				new Player(3, "Thomas", true, 550.0f, 100.0f, 100.0f, 40.0f, 420.0f, 0),
				new Player(4, "Samuel", true, 530.0f, 150.0f, 80.0f, 40.0f, 420.0f, 0),
				new Goalie(5, "David", true, 520.0f, 150.0f, 100.0f, 20.0f, 500.0f, 0)};
		for(Player player: players){
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
		values.put(TACKLE_PREVENTION_SKILL_COLUMN_NAME,
				player.getTacklePreventionSkill());
		values.put(SAVING_SKILL_COLUMN_NAME, player.getSavingSkill());
		values.put(TEAM_ID_COLUMN_NAME, player.getTeamID());

		database.insert(PLAYER_TABLE_NAME, null, values);
	}
			
}
