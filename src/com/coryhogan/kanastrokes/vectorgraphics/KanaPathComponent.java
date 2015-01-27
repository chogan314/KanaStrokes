package com.coryhogan.kanastrokes.vectorgraphics;

import com.coryhogan.kanastrokes.math.Vec2;

public interface KanaPathComponent {	
	public float moveCursor(float distance);
	
	public Vec2 getCursor();
	
	public void resetCursor();
	
	public boolean isFinished();
}
