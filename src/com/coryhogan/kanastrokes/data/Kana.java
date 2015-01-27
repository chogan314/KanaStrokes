package com.coryhogan.kanastrokes.data;

import java.util.Arrays;

public class Kana {
	public static final int LESSONS_MAX_RANK = 0;
	public static final int DRILLS_MAX_RANK = 8;
	public static final int REVIEWS_MAK_RANK = 15;
	
	public enum KanaType {
		HIRAGANA, KATAKANA
	}
	
	private String symbol;
	private String[] readings;
	private KanaType type;
	private int requiredLevel;
	private int rank;
	private long lastEncounterTime;
	private long nextEncounterTime;
	private int timesReviewed;
	private int timesAnsweredCorrectly;
	
	public Kana(String symbol, String[] readings, String type, int requiredLevel, int rank, 
			long lastEncounterTime, long nextEncounterTime, int timesReviewed, int timesAnsweredCorrectly) {
		this.symbol = symbol;
		this.readings = readings;
		this.type = KanaType.valueOf(type);
		this.requiredLevel = requiredLevel;
		this.rank = rank;
		this.lastEncounterTime = lastEncounterTime;
		this.nextEncounterTime = nextEncounterTime;
		this.timesReviewed = timesReviewed;
		this.timesAnsweredCorrectly = timesAnsweredCorrectly;
	}
	
	public boolean isInLessons() {
		return rank <= LESSONS_MAX_RANK;
	}
	
	public boolean isInDrills() {
		return rank > LESSONS_MAX_RANK && rank <= DRILLS_MAX_RANK;
	}
	
	public boolean isInReviews() {
		return rank > DRILLS_MAX_RANK && rank <= REVIEWS_MAK_RANK;
	}
	
	public void rankUp() {
		if (rank <= REVIEWS_MAK_RANK) {
			rank++;
		}
	}
	
	public void rankDown() {
		int nextRank = rank - 3;
		rank = Math.max(nextRank, DRILLS_MAX_RANK + 1);
	}
	
	public void answeredCorrectly() {
		timesReviewed++;
		timesAnsweredCorrectly++;
	}
	
	public void answeredIncorrectly() {
		timesReviewed++;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getPrimaryReading() {
		return readings[0];
	}
	
	public boolean hasAlternateReadings() {
		return readings.length > 1;
	}
	
	public int numReadings() {
		return readings.length;
	}
	
	public String getReading(int index) {
		return readings[index];
	}
	
	public KanaType getType() {
		return type;
	}
	
	public int getRequiredLevel() {
		return requiredLevel;
	}
	
	public int getRank() {
		return rank;
	}
	
	public long getLastEncounterTime() {
		return lastEncounterTime;
	}
	
	public long getNextEncounterTime() {
		return nextEncounterTime;
	}
	
	public int getTimesReviewed() {
		return timesReviewed;
	}
	
	public int getTimesAnsweredCorrectly() {
		return timesAnsweredCorrectly;
	}
	
	public void setLastEncounterTime(long lastEncounterTime) {
		this.lastEncounterTime = lastEncounterTime;
	}
	
	public void setNextEncounterTime(long nextEncounterTime) {
		this.nextEncounterTime = nextEncounterTime;
	}
	
	@Override
	public String toString() {
		return symbol + ": " + Arrays.toString(readings);
	}
}
