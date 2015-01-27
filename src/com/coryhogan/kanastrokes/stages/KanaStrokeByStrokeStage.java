package com.coryhogan.kanastrokes.stages;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.coryhogan.kanastrokes.data.Kana;
import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.rendering.RenderContext;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPath;
import com.coryhogan.kanastrokes.vectorgraphics.KanaVector;
import com.coryhogan.kanastrokes.vectorgraphics.parsing.KanaVectorParseNode;
import com.coryhogan.kanastrokes.vectorgraphics.parsing.KanaVectorParser;

public class KanaStrokeByStrokeStage extends Stage {
	private RenderContext strokesContext;
	private int strokesContextSize;
	private int strokesContextXOffset, strokesContextYOffset;
	private float strokeSpeed;
	private float maxSegmentDistance;
	private boolean drawing = false;
	private Vec2 cursorPos = new Vec2();
	private Queue<KanaPath> queuedStrokes;
	private Kana character;
	private Object characterMutex;
	private AtomicBoolean nextStrokeFlag;
	private AtomicBoolean drawAllStrokesFlag;
	private AtomicBoolean resetStrokesFlag;
	private AtomicBoolean gridFlag;

	public KanaStrokeByStrokeStage(View view, Kana character) {
		super(view, "KanaStrokeByStrokeStage");
		initStrokesContext();
		strokeSpeed = (float) strokesContextSize / 5;
		maxSegmentDistance = 6;
		queuedStrokes = new LinkedList<KanaPath>();
		this.character = character;
		characterMutex = new Object();
		nextStrokeFlag = new AtomicBoolean();
		drawAllStrokesFlag = new AtomicBoolean();
		resetStrokesFlag = new AtomicBoolean();
		gridFlag = new AtomicBoolean();
		testSetup();
	}
	
	private void initStrokesContext() {
		strokesContextSize = Math.min(width, height);
		strokesContext = new RenderContext(strokesContextSize, strokesContextSize, Bitmap.Config.ARGB_8888);
		strokesContext.setAntiAlias(true);
		strokesContextXOffset = (int) ((width - strokesContextSize) / 2);
		strokesContextYOffset = (int) ((height - strokesContextSize) / 2);
	}
	
	private void testSetup() {
//		KanaPathComponent line01 = new KanaPathLine(new Vec2((float) strokesContextSize / 3, 10), new Vec2((float) strokesContextSize / 3, (float) strokesContextSize / 4 + 10));
//		KanaPathComponent line02 = new KanaPathLine(new Vec2((float) (strokesContextSize * 2) / 3, 10), new Vec2((float) (strokesContextSize * 2) / 3, (float) strokesContextSize / 4 + 10));
//		KanaPathComponent cCurve01 = new KanaPathCubicCurve(new Vec2((float) strokesContextSize / 4, (float) strokesContextSize / 2), 
//																					new Vec2((float) strokesContextSize / 4, (float) (strokesContextSize * 3) / 4),
//																					new Vec2((float) (strokesContextSize * 3) / 4, (float) (strokesContextSize * 3) / 4),
//																					new Vec2((float) (strokesContextSize * 3) / 4, (float) strokesContextSize / 2));
//		KanaPath path01 = new KanaPath();
//		path01.appendComponent(line01);
//		KanaPath path02 = new KanaPath();
//		path02.appendComponent(line02);
//		KanaPath path03 = new KanaPath();
//		path03.appendComponent(cCurve01);
//		queuedStrokes.add(path01);
//		queuedStrokes.add(path02);
//		queuedStrokes.add(path03);
		
		String test = 
				"<kana>\n"
				+ "<path\n"
				+ "d=\"m 169.0922,145.68794 c 28.39716,1.29078 192.32624,-6.4539 250.41135,-16.78014\" />\n"
				+ "<path\n"
				+ "d=\"M 267.19149,52.75177 C 259.44681,95.347516 240.08511,317.3617 305.9149,393.51773\" />\n"
				+ "<path\n"
				+ "d=\"M 364,179.24823 C 328.05236,401.43748 202.20552,429.00745 176.4098,390.81852 142.51802,340.64389 "
				+ "200.49819,200.27318 361.64429,214.18166 c 115.20435,17.26076 178.9834,166.42403 -2.80741,206.44245\" />"
				+ "</kana>";
		
		KanaVectorParseNode node = KanaVectorParser.ParseKanaVector(test);
		KanaVector vector = node.toKanaVector(strokesContextSize, strokesContextSize, 10, 10);
		for (KanaPath stroke : vector.strokes) {
			queuedStrokes.add(stroke);
		}
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float delta) {
		if (!queuedStrokes.isEmpty()) {
			float dist = delta * strokeSpeed;
			KanaPath currentStroke = queuedStrokes.peek();
			
			if (!drawing) {
				cursorPos.set(currentStroke.getCursor());
			}
			
			currentStroke.moveCursor(dist);
			Vec2 lastPos = cursorPos.cpy();
			cursorPos.set(currentStroke.getCursor());
			
			if (currentStroke.isFinished()) {
				queuedStrokes.remove();
				drawing = false;
			}
			strokesContext.drawCircle(lastPos, 5, Color.BLACK);
			strokesContext.drawCircle(cursorPos, 5, Color.BLACK);
			strokesContext.drawLine(lastPos, cursorPos, 10, Color.BLACK);
		}
	}

	@Override
	public void present(float interpFactor) {
		frameBuffer.clear(255, 255, 255);
		frameBuffer.drawBitmap(strokesContext.getSource(), strokesContextXOffset, strokesContextYOffset);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		strokesContext.dispose();
	}
	
	public void setCharacter(Kana character) {
		synchronized (characterMutex) {
			this.character = character;
		}
	}
	
	public void nextStroke() {
		nextStrokeFlag.compareAndSet(false, true);
	}
	
	public void drawAllStrokes() {
		drawAllStrokesFlag.compareAndSet(false, true);
	}
	
	public void resetStrokes() {
		resetStrokesFlag.compareAndSet(false, true);
	}
	
	public void drawGrid(boolean toDraw) {
		gridFlag.set(toDraw);
	}
}
