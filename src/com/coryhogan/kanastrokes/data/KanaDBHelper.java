package com.coryhogan.kanastrokes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KanaDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "kana.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "kana";
	public static final String KANA_ID = "_id";
	public static final String KANA_SYMBOL = "symbol";
	public static final String KANA_READINGS = "readings";
	public static final String KANA_TYPE = "type";
	public static final String KANA_REQ_LEVEL = "requiredLevel";
	public static final String KANA_RANK = "rank";
	public static final String KANA_LAST_ENCOUNTER = "lastEncounter";
	public static final String KANA_NEXT_ENCOUNTER = "nextEncounter";
	public static final String KANA_NUM_REVIEWED = "reviewed";
	public static final String KANA_NUM_CORRECT = "correct";
	
	private static final String CREATE_CMD = 
			"CREATE TABLE " + TABLE_NAME + " ("
			+ KANA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KANA_SYMBOL + " TEXT NOT NULL, "
			+ KANA_READINGS + " TEXT NOT NULL, "
			+ KANA_TYPE + " TEXT NOT NULL, "
			+ KANA_REQ_LEVEL + " INTEGER NOT NULL, "
			+ KANA_RANK + " INTEGER NOT NULL, "
			+ KANA_LAST_ENCOUNTER + " INTEGER NOT NULL, "
			+ KANA_NEXT_ENCOUNTER + " INTEGER NOT NULL, "
			+ KANA_NUM_REVIEWED + " INTEGER NOT NULL, "
			+ KANA_NUM_CORRECT + " INTEGER NOT NULL);";

	public KanaDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Do nothing
	}	
}
