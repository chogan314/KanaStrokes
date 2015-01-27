package com.coryhogan.kanastrokes.vectorgraphics;

import java.util.ArrayList;
import java.util.List;

public class KanaVector {
	public List<KanaPath> strokes;
	private int currentStrokeIdx;
	
	public KanaVector() {
		strokes = new ArrayList<KanaPath>();
	}
	
	public void appendStroke(KanaPath stroke) {
		strokes.add(stroke);
	}
	
	public void drawStroke() {
		if (strokes.get(currentStrokeIdx).isFinished()) {
			
		}
	}
	
	public boolean isFinished() {
		return strokes.get(currentStrokeIdx).isFinished();
	}
}
