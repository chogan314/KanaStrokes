package com.coryhogan.kanastrokes.vectorgraphics;

import com.coryhogan.kanastrokes.math.Vec2;

public class KanaPathLine implements KanaPathComponent {
	private Vec2 start;
	private Vec2 stop;
	private Vec2 trajectory;
	private Vec2 trajectoryScaleable;
	private Vec2 cursor;
	private Vec2 toStop;
	private float length;
	
	public KanaPathLine(Vec2 start, Vec2 stop) {
		this.start = start.cpy();
		this.stop = stop.cpy();
		cursor = start.cpy();
		toStop = stop.cpy().sub(start);
		trajectory = toStop.cpy().normalize();
		trajectoryScaleable = trajectory.cpy();
		length = toStop.len();
	}
	
	@Override
	public float moveCursor(float distance) {		
		if (toStop.len() <= distance) {
			cursor.set(stop);
			return distance - toStop.len();
		}
		
		cursor.add(trajectoryScaleable.scl(distance));
		trajectoryScaleable.set(trajectory);
		toStop.set(stop).sub(cursor);
		return 0;
	}
	
	public float moveCursorByPercent(float percentOfLength) {
		float distance = length * percentOfLength;
		return moveCursor(distance);
	}
	
	@Override
	public Vec2 getCursor() {
		return cursor;
	}
	
	@Override
	public void resetCursor() {
		cursor.set(start);
		toStop.set(stop).sub(start);
	}
	
	@Override
	public boolean isFinished() {
		return cursor.equals(stop);
	}
}













