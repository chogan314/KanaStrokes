package com.coryhogan.kanastrokes.activites;

import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.coryhogan.kanastrokes.R;
import com.coryhogan.kanastrokes.data.Kana;
import com.coryhogan.kanastrokes.data.KanaConstants;
import com.coryhogan.kanastrokes.data.KanaDAO;
import com.coryhogan.kanastrokes.data.KanaLists;

public class MainActivity extends Activity {
	private SharedPreferences prefs;
	private KanaDAO charDAO;
	private Button lessonsButton;
	private Button drillsButton;
	private Button reviewsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		lessonsButton = (Button) findViewById(R.id.lessonsButton);
		drillsButton = (Button) findViewById(R.id.drillsButton);
		reviewsButton = (Button) findViewById(R.id.reviewsButton);
		
		Button refreshButton = (Button) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getKanaData();
			}			
		});
		
		lessonsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (KanaLists.lessons.size() == 0) {
					return;
				}
				Intent intent = new Intent(MainActivity.this, LessonsActivity.class);
				intent.putExtra("mode", 0);
				startActivity(intent);
			}
		});
		
		drillsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (KanaLists.drills.size() == 0) {
					return;
				}
				Intent intent = new Intent(MainActivity.this, LessonsActivity.class);
				intent.putExtra("mode", 1);
				startActivity(intent);
			}
		});
		
		reviewsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (KanaLists.reviews.size() == 0) {
					return;
				}
				Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
				intent.putExtra("index", 0);
				startActivity(intent);
			}
		});
		
		prefs = getSharedPreferences("com.coryhogan.kanastrokes", MODE_PRIVATE);
		charDAO = KanaDAO.getInstance(this);
	}
	
	@Override
	protected void onResume() {		
		super.onResume();
		
		if (prefs.getBoolean("isFirstRun", true)) {			
			for (int i = 0; i < KanaConstants.HIRAGANA.length; i++) {
				charDAO.createCharacterEntry(KanaConstants.HIRAGANA[i], KanaConstants.READINGS[i], 
						Kana.KanaType.HIRAGANA.toString(), KanaConstants.HIRAGANA_LEVELS[i]);
			}
			
			for (int i = 0; i < KanaConstants.KATAKANA.length; i++) {
				charDAO.createCharacterEntry(KanaConstants.KATAKANA[i], KanaConstants.READINGS[i],
						Kana.KanaType.KATAKANA.toString(), KanaConstants.KATAKANA_LEVELS[i]);
			}
			
			prefs.edit().putBoolean("isFirstRun", false).putInt("playerLevel", 1).commit();
		}
		
		getKanaData();
	}
	
	private void getKanaData() {
		int playerLevel = prefs.getInt("playerLevel", -1);
		KanaLists.lessons = charDAO.getLessons(playerLevel);
		KanaLists.drills = charDAO.getDrills(playerLevel);
		KanaLists.reviews = charDAO.getReviews(playerLevel);
		
		Collections.shuffle(KanaLists.lessons);
		Collections.shuffle(KanaLists.drills);
		Collections.shuffle(KanaLists.reviews);
		
		lessonsButton.setText("Lessons: " + KanaLists.lessons.size());
		drillsButton.setText("Drills: " + KanaLists.drills.size());
		reviewsButton.setText("Reviews " + KanaLists.reviews.size());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
