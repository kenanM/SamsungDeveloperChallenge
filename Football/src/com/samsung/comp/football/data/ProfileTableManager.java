package com.samsung.comp.football.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.comp.football.Profile;

/**
 * This class creates and updates the players database and is heavily inspired
 * by: http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class ProfileTableManager {

	protected static final String PROFILES_TABLE_NAME = "profiles";

	protected static final String PROFILE_ID_COLUMN_NAME = "_id";
	protected static final String FUNDS_COLUMN_NAME = "funds";

	private static final String CREATE_PROFILES_TABLE = "create table "
			+ PROFILES_TABLE_NAME + "(" + PROFILE_ID_COLUMN_NAME
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FUNDS_COLUMN_NAME
			+ " integer not null" + ");";

	// static class hack
	private ProfileTableManager() {
	}

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_PROFILES_TABLE);
		addDefaultProfile(database);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(ProfileTableManager.class.getName(),
				"Upgrading profiles table from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + PROFILES_TABLE_NAME);
		onCreate(db);
	}

	private static void addDefaultProfile(SQLiteDatabase database) {

		Profile profile = new Profile(1, 0);
		insertProfile(database, profile);
	}

	private static void insertProfile(SQLiteDatabase database, Profile profile) {
		Log.v(PlayerDataSource.class.toString(),
				"adding profile: " + profile.toString());

		ContentValues values = new ContentValues();
		values.put(PROFILE_ID_COLUMN_NAME, profile.getProfileID());
		values.put(FUNDS_COLUMN_NAME, profile.getFunds());

		Log.v("db", "profile_id: " + profile.getProfileID());
		Log.v("db", "profile_funds: " + profile.getFunds());
		database.insert(PROFILES_TABLE_NAME, null, values);
	}

	private static Profile cursorToProfile(Cursor cursor) {

		int profileID = cursor.getInt(cursor
				.getColumnIndex(PROFILE_ID_COLUMN_NAME));

		int funds = cursor.getInt(cursor.getColumnIndex(FUNDS_COLUMN_NAME));

		return new Profile(profileID, funds);
	}

	public static Profile getProfile(SQLiteDatabase database, int profileID) {
		Cursor cursor = database.query(PROFILES_TABLE_NAME, null,
				PROFILE_ID_COLUMN_NAME + "=? ",
				new String[] { Integer.toString(profileID) }, null, null, null);
		cursor.moveToFirst();
		return cursorToProfile(cursor);
	}

}

