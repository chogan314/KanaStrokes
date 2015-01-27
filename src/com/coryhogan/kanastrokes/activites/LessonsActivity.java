package com.coryhogan.kanastrokes.activites;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coryhogan.kanastrokes.R;
import com.coryhogan.kanastrokes.data.Kana;
import com.coryhogan.kanastrokes.data.KanaLists;
import com.coryhogan.kanastrokes.data.SRSManager;
import com.coryhogan.kanastrokes.rendering.RenderView;
import com.coryhogan.kanastrokes.stages.KanaDrawingStage;
import com.coryhogan.kanastrokes.stages.KanaStrokeByStrokeStage;
import com.coryhogan.kanastrokes.stages.Stage.StageFactory;

public class LessonsActivity extends FragmentActivity {
	private int mode;
	private int index = 0;
	List<Kana> characters;
	private Kana character;
	private int repetitions = 0;
	private RenderView<KanaStrokeByStrokeStage> strokesView;
	private RenderView<KanaDrawingStage> drawingView;
	private TextView readingsText;
	private Button nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lessons);
		
		mode = getIntent().getIntExtra("mode", -1);
		
		if (mode == 0) {
			characters = KanaLists.lessons;
		} else if (mode == 1) {
			characters = KanaLists.drills;
		} else {
			finish();
		}
		
		character = characters.get(index);
		
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
		
		FrameLayout strokesFrame = (FrameLayout) findViewById(R.id.lessonsKanaStrokesFrame);
		strokesFrame.addView(strokesView);
		
		FrameLayout drawingFrame = (FrameLayout) findViewById(R.id.lessonsKanaDrawingFrame);
		drawingFrame.addView(drawingView);
		
		readingsText = (TextView) findViewById(R.id.lessonsKanaReadingsText);
		readingsText.setText(character.getType().toString() + "\n" + character.getPrimaryReading());
		
		nextButton = (Button) findViewById(R.id.lessonsNextButton);
		nextButton.setText(repetitions + " / " + 5);
		nextButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (strokesView.isInitialized() && drawingView.isInitialized()) {
					drawingView.getStage().setClearFlag();
					repetitions++;
					if (repetitions >= 5) {
						repetitions = 0;
						index++;
						SRSManager.levelCharacterUp(character, LessonsActivity.this);
						if (index >= characters.size()) {
							finish();
						} else {
							character = characters.get(index);
							strokesView.getStage().setCharacter(character);
							readingsText.setText(character.getType().toString() + "\n" + character.getPrimaryReading());
							nextButton.setText(repetitions + " / " + 5);
						}
					} else {
						strokesView.getStage().resetStrokes();
						nextButton.setText(repetitions + " / " + 5);
					}
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_lessons, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (strokesView.isInitialized() && drawingView.isInitialized()) {
			switch(item.getItemId()) {
			case R.id.lessonsMenuToggleErase:
				drawingView.getStage().toggleErase();
				return true;
			case R.id.lessonsMenuClearDrawing:
				drawingView.getStage().setClearFlag();
				return true;
			case R.id.lessonsMenuToggleGrid:
				drawingView.getStage().toggleGrid();
				return true;
			case R.id.lessonsMenuChooseStrokeWidth:
				return true;
			case R.id.lessonsMenuUndoStroke:
				drawingView.getStage().setUndoStrokeFlag();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}






