package com.samsung.comp.football.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class creates and updates the players database and is heavily inspired
 * by: http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class PlayerDatabaseHelper extends SQLiteOpenHelper {

	public static final String PLAYER_TABLE_NAME = "books";

	public static final String ID_COLUMN_NAME = "_id";
	public static final String PLAYER_NAME_COLUMN_NAME = "name";

	public static final String PURCHASED_COLUMN_NAME = "purchased";
	public static final String SHOOT_SPEED_COLUMN_NAME = "shoot_speed";
	public static final String RUN_SPEED_COLUMN_NAME = "run_speed";
	public static final String TACKLE_SKILL_COLUMN_NAME = "tackle_skill";
	public static final String TACKLE_PREVENTION_SKILL_COLUMN_NAME = "tackle_prevention";
	public static final String SAVING_SKILL_COLUMN_NAME = "saving_skill";

	public static final String DATABASE_NAME = "database.db";
	public static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ PLAYER_TABLE_NAME + "(" + ID_COLUMN_NAME
			+ " integer primary key autoincrement, " + PLAYER_NAME_COLUMN_NAME
			+ " text not null, " + PURCHASED_COLUMN_NAME
			+ " integer not null, " + SHOOT_SPEED_COLUMN_NAME
			+ " real not null, " + RUN_SPEED_COLUMN_NAME + " real not null, "
			+ TACKLE_SKILL_COLUMN_NAME + " real not null, "
			+ SAVING_SKILL_COLUMN_NAME + " real not null);";

	public PlayerDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		// TODO insert players here!
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// As there hasn't yet been any updates to the database, this is
		// redundant
	}

}
