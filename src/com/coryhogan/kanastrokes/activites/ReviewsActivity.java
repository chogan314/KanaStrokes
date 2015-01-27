package com.coryhogan.kanastrokes.activites;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coryhogan.kanastrokes.R;
import com.coryhogan.kanastrokes.data.Kana;
import com.coryhogan.kanastrokes.data.KanaLists;
import com.coryhogan.kanastrokes.data.SRSManager;
import com.coryhogan.kanastrokes.rendering.RenderView;
import com.coryhogan.kanastrokes.stages.KanaDrawingStage;
import com.coryhogan.kanastrokes.stages.KanaStrokeByStrokeStage;
import com.coryhogan.kanastrokes.stages.Stage.StageFactory;

public class ReviewsActivity extends Activity {
	private int index = 0;
	private Kana character;
	private RenderView<KanaStrokeByStrokeStage> strokesView;
	private RenderView<KanaDrawingStage> drawingView;
	private TextView readingsText;
	private Button passButton;
	private Button failButton;
	TextView strokesViewBlocker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reviews);
		
		character = KanaLists.reviews.get(index);
		
		strokesView = 
				new RenderView<KanaStrokeByStrokeStage>(this, 
						new StageFactory<KanaStrokeByStrokeStage>() {
							@Override
							public KanaStrokeByStrokeStage createStage(View view) {
								return new KanaStrokeByStrokeStage(view, character);
							}
				});
		
		drawingView = 
				new RenderView<KanaDrawingStage>(this,
						new StageFactory<KanaDrawingStage>() {
							@Override
							public KanaDrawingStage createStage(View view) {
								return new KanaDrawingStage(view);
							}	
				});
		
		FrameLayout strokesFrame = (FrameLayout) findViewById(R.id.reviewsKanaStrokesFrame);
		strokesFrame.addView(strokesView);
		
		strokesViewBlocker = new TextView(this);
		strokesViewBlocker.setGravity(Gravity.CENTER);
		strokesViewBlocker.setTextSize(28);
		strokesViewBlocker.setText("Click to\nreveal character");
		strokesViewBlocker.setBackgroundColor(Color.WHITE);
		strokesViewBlocker.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				strokesViewBlocker.setVisibility(View.INVISIBLE);
				passButton.setVisibility(View.VISIBLE);
				failButton.setVisibility(View.VISIBLE);
			}
		});
		
		strokesFrame.addView(strokesViewBlocker);
		
		FrameLayout drawingFrame = (FrameLayout) findViewById(R.id.reviewsKanaDrawingFrame);
		drawingFrame.addView(drawingView);
		
		readingsText = (TextView) findViewById(R.id.reviewsKanaReadingsText);
		readingsText.setText(character.getType().toString() + "\n" + character.getPrimaryReading());
		
		passButton = (Button) findViewById(R.id.reviewsPassButton);
		passButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (strokesView.isInitialized() && drawingView.isInitialized()) {
					SRSManager.levelCharacterUp(character, ReviewsActivity.this);
					drawingView.getStage().setClearFlag();
					index++;
					if (index >= KanaLists.reviews.size()) {
						finish();
					} else {
						character = KanaLists.reviews.get(index);
						strokesView.getStage().setCharacter(character);
						readingsText.setText(character.getType().toString() + "\n" + character.getPrimaryReading());
						strokesViewBlocker.setVisibility(View.VISIBLE);
						passButton.setVisibility(View.INVISIBLE);
						failButton.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		
		failButton = (Button) findViewById(R.id.reviewsFailButton);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (strokesView.isInitialized() && drawingView.isInitialized()) {
					SRSManager.levelCharacterDown(character, ReviewsActivity.this);
					drawingView.getStage().setClearFlag();
					index++;
					if (index >= KanaLists.reviews.size()) {
						finish();
					} else {
						character = KanaLists.reviews.get(index);
						strokesView.getStage().setCharacter(character);
						readingsText.setText(character.getType().toString() + "\n" + character.getPrimaryReading());
						strokesViewBlocker.setVisibility(View.VISIBLE);
						passButton.setVisibility(View.INVISIBLE);
						failButton.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}
}



















