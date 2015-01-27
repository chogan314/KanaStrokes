package com.coryhogan.kanastrokes.data;

import android.content.Context;
import android.util.SparseArray;

public class SRSManager {	
	private static final SparseArray<Long> srsTimes = new SparseArray<Long>();
	
	static {		
//		srsTimes.put(1, hoursToMS(4));
//		srsTimes.put(2, hoursToMS(8));
//		srsTimes.put(3, hoursToMS(20));
//		srsTimes.put(4, hoursToMS(20));
//		srsTimes.put(5, hoursToMS(20));
//		srsTimes.put(6, hoursToMS(20));
//		srsTimes.put(7, hoursToMS(20));
//		srsTimes.put(8, hoursToMS(20));
//		srsTimes.put(9, hoursToMS(20));
//		srsTimes.put(10, daysToMS(3));
//		srsTimes.put(11, daysToMS(7));
//		srsTimes.put(12, daysToMS(14));
//		srsTimes.put(13, daysToMS(30));
//		srsTimes.put(14, daysToMS(60));
//		srsTimes.put(15, daysToMS(120));
		
		srsTimes.put(1, 0l);
		srsTimes.put(2, 0l);
		srsTimes.put(3, 0l);
		srsTimes.put(4, 0l);
		srsTimes.put(5, 0l);
		srsTimes.put(6, 0l);
		srsTimes.put(7, 0l);
		srsTimes.put(8, 0l);
		srsTimes.put(9, 0l);
		srsTimes.put(10, 0l);
		srsTimes.put(11, 0l);
		srsTimes.put(12, 0l);
		srsTimes.put(13, 0l);
		srsTimes.put(14, 0l);
		srsTimes.put(15, 0l);
	}
	
	private static long hoursToMS(int hours) {
		return 60 * 60 * 1000 * hours;
	}
	
	private static long daysToMS(int days) {
		return hoursToMS(days - 1 * 24) + hoursToMS(20);
	}
	
	public static void levelCharacterUp(Kana character, Context context) {
		if (character.isInReviews()) {
			character.answeredCorrectly();
		}
		character.rankUp();
		character.setLastEncounterTime(System.currentTimeMillis());
		if (character.getRank() <= Kana.REVIEWS_MAK_RANK) {
			character.setNextEncounterTime(character.getLastEncounterTime() + srsTimes.get(character.getRank()));
		} else {
			character.setNextEncounterTime(-1);
		}
		KanaDAO.getInstance(context).updateCharacter(character);
	}
	
	public static void levelCharacterDown(Kana character, Context context) {
		if (!character.isInReviews()) {
			return;
		}
		character.rankDown();
		character.setLastEncounterTime(System.currentTimeMillis());
		character.answeredIncorrectly();
		character.setNextEncounterTime(character.getLastEncounterTime() + srsTimes.get(character.getRank()));
		KanaDAO.getInstance(context).updateCharacter(character);
	}
}















