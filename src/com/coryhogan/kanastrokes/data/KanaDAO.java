package com.coryhogan.kanastrokes.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class KanaDAO {
	private static KanaDAO instance;
	private SQLiteDatabase database;
	private KanaDBHelper dbHelper;
	
	private String[] allColumns = {
			KanaDBHelper.KANA_ID,
			KanaDBHelper.KANA_SYMBOL,
			KanaDBHelper.KANA_READINGS,
			KanaDBHelper.KANA_TYPE,
			KanaDBHelper.KANA_REQ_LEVEL,
			KanaDBHelper.KANA_RANK,
			KanaDBHelper.KANA_LAST_ENCOUNTER,
			KanaDBHelper.KANA_NEXT_ENCOUNTER,
			KanaDBHelper.KANA_NUM_REVIEWED,
			KanaDBHelper.KANA_NUM_CORRECT
	};
	
	public static KanaDAO getInstance(Context context) {
		if (instance == null) {
			Log.d("KanaDAO", "Creating new DAO...");
			instance = new KanaDAO(context);
		}
		return instance;
	}
	
	private KanaDAO(Context context) throws SQLException {
		dbHelper = new KanaDBHelper(context);
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
		instance = null;
	}
	
	public void createCharacterEntry(String symbol, String readings, String type, int requiredLevel) {
		ContentValues values = new ContentValues();
		values.put(KanaDBHelper.KANA_SYMBOL, symbol);
		values.put(KanaDBHelper.KANA_READINGS, readings);
		values.put(KanaDBHelper.KANA_TYPE, type);
		values.put(KanaDBHelper.KANA_REQ_LEVEL, requiredLevel);
		values.put(KanaDBHelper.KANA_RANK, 0);
		values.put(KanaDBHelper.KANA_LAST_ENCOUNTER, 0);
		values.put(KanaDBHelper.KANA_NEXT_ENCOUNTER, 0);
		values.put(KanaDBHelper.KANA_NUM_REVIEWED, 0);
		values.put(KanaDBHelper.KANA_NUM_CORRECT, 0);
		database.insert(KanaDBHelper.TABLE_NAME, null, values);
	}
	
	public void updateCharacter(Kana character) {
		ContentValues values = new ContentValues();
		values.put(KanaDBHelper.KANA_RANK, character.getRank());
		values.put(KanaDBHelper.KANA_LAST_ENCOUNTER, character.getLastEncounterTime());
		values.put(KanaDBHelper.KANA_NEXT_ENCOUNTER, character.getNextEncounterTime());
		values.put(KanaDBHelper.KANA_NUM_REVIEWED, character.getTimesReviewed());
		values.put(KanaDBHelper.KANA_NUM_CORRECT, character.getTimesAnsweredCorrectly());
		
		String selection = KanaDBHelper.KANA_SYMBOL + " LIKE ?";
		String[] selectionArgs = { character.getSymbol() };
		
		database.update(KanaDBHelper.TABLE_NAME, values, selection, selectionArgs);
	}
	
	public List<Kana> getAllCharacters() {		
		Cursor cursor = database.query(KanaDBHelper.TABLE_NAME, allColumns, 
				null, null, null, null, null);
		
		List<Kana> characters = cursorToCharacterList(cursor);		
		cursor.close();
		return characters;
	}
	
	public List<Kana> getLessons(int playerLevel) {
		String selection = KanaDBHelper.KANA_REQ_LEVEL + " <= ? AND "
				+ KanaDBHelper.KANA_RANK + " <= " + Kana.LESSONS_MAX_RANK;
		
		String[] selectionArgs = { String.valueOf(playerLevel) };
		
		Cursor cursor = database.query(KanaDBHelper.TABLE_NAME, allColumns, 
				selection, selectionArgs, null, null, null);
		
		List<Kana> characters = cursorToCharacterList(cursor);		
		cursor.close();
		return characters;
	}
	
	public List<Kana> getDrills(int playerLevel) {
		String selection = KanaDBHelper.KANA_REQ_LEVEL + " <= ? AND " 
								 + KanaDBHelper.KANA_RANK + " <= " + Kana.DRILLS_MAX_RANK + " AND "
								 + KanaDBHelper.KANA_RANK + " > " + Kana.LESSONS_MAX_RANK + " AND "
								 + KanaDBHelper.KANA_NEXT_ENCOUNTER + " <= ?";
		
		String[] selectionArgs = { String.valueOf(playerLevel), String.valueOf(System.currentTimeMillis()) };
		
		Cursor cursor = database.query(KanaDBHelper.TABLE_NAME, allColumns, 
				selection, selectionArgs, null, null, null);
		
		List<Kana> characters = cursorToCharacterList(cursor);		
		cursor.close();
		return characters;
	}
	
	public List<Kana> getReviews(int playerLevel) {
		String selection = KanaDBHelper.KANA_REQ_LEVEL + " <= ? AND "
							    + KanaDBHelper.KANA_RANK + " <= " + Kana.REVIEWS_MAK_RANK + " AND "
							    + KanaDBHelper.KANA_RANK + " > " + Kana.DRILLS_MAX_RANK + " AND "
							    + KanaDBHelper.KANA_NEXT_ENCOUNTER + " <= ?";
		
		String[] selectionArgs = { String.valueOf(playerLevel), String.valueOf(System.currentTimeMillis()) };
		
		Cursor cursor = database.query(KanaDBHelper.TABLE_NAME, allColumns, 
				selection, selectionArgs, null, null, null);
		
		List<Kana> characters = cursorToCharacterList(cursor);		
		cursor.close();
		return characters;
	}
	
	private List<Kana> cursorToCharacterList(Cursor cursor) {
		List<Kana> characters = new ArrayList<Kana>();
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Kana character = cursorToCharacter(cursor);
			characters.add(character);
		}
		
		return characters;
	}
	
	private Kana cursorToCharacter(Cursor cursor) {
		Kana character = new Kana(
				cursor.getString(1),
				cursor.getString(2).split("\\."),
				cursor.getString(3),
				cursor.getInt(4),
				cursor.getInt(5),
				cursor.getLong(6),
				cursor.getLong(7),
				cursor.getInt(8),
				cursor.getInt(9));
		return character;
	}
}















