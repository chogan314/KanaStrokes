package com.coryhogan.kanastrokes.vectorgraphics;

import com.coryhogan.kanastrokes.math.Vec2;

public class KanaPathCubicCurve implements KanaPathComponent {
	private Vec2 p0, p1, p2, p3;
	private Vec2 p0Scl, p1Scl, p2Scl, p3Scl;
	private Vec2 cursor;
	private float percentTraversed = 0;
	private float length;
	
	public KanaPathCubicCurve(Vec2 p0, Vec2 p1, Vec2 p2, Vec2 p3) {
		this.p0 = p0.cpy();
		this.p1 = p1.cpy();
		this.p2 = p2.cpy();
		this.p3 = p3.cpy();
		p0Scl = new Vec2();
		p1Scl = new Vec2();
		p2Scl = new Vec2();
		p3Scl = new Vec2();
		cursor = p0.cpy();
		length = calcLength();
	}
	
	@Override
	public float moveCursor(float distance) {
		float distanceLeft = length - length * percentTraversed;
		
		if (distanceLeft <= distance) {
			cursor.set(p3);
			percentTraversed = 1;
			return distance - distanceLeft;
		}
		
		float percentToTravel = distance / length;
		percentTraversed += percentToTravel;
		cursor.set(pointAt(percentTraversed));
		return 0;
	}
	
	@Override
	public Vec2 getCursor() {
		return cursor;
	}
	
	@Override
	public void resetCursor() {
		cursor.set(p0);
		percentTraversed = 0;
	}
	
	private Vec2 pointAt(float t) {
		float t2 = t * t;
		float t3 = t2 * t;
		float mt = 1 - t;
		float mt2 = mt * mt;
		float mt3 = mt * mt2;
		
		p0Scl.set(p0).scl(mt3);
		p1Scl.set(p1).scl(3 * mt2 * t);
		p2Scl.set(p2).scl(3 * mt * t2);
		p3Scl.set(p3).scl(t3);
		
		return p3Scl.add(p2Scl.add(p1Scl.add(p0Scl)));
	}
	
	private float calcLength() {
		float z = (float) 1 / 2;
		float sum = 0;
		for (int i = 0; i < BezierConstants.tValues.length; i++) {
			float correctedT = z * BezierConstants.tValues[i] + z;
			sum += BezierConstants.cValues[i] * B(correctedT);
		}
		return z * sum;
	}
	
	private float B(float t) {
		float xBase = getDerivative(t, true);
		float yBase = getDerivative(t, false);
		float combined = xBase * xBase + yBase * yBase;
		return (float) Math.sqrt(combined);
	}
	
	private float getDerivative(float t, boolean doX) {
		int n = 2;
		
		Vec2 p10 = p1.cpy().sub(p0).scl(n);
		Vec2 p21 = p2.cpy().sub(p1).scl(n);
		Vec2 p32 = p3.cpy().sub(p2).scl(n);
		
		float a = doX ? p10.getX() : p10.getY();
		float b = doX ? p21.getX() : p21.getY();
		float c = doX ? p32.getX() : p32.getY();
		float v[] = { a, b, c };
		
		float value = 0;
		for (int k = 0; k <= n; k++) {
			value += BezierConstants.binomialCoefficients[n][k] * 
					Math.pow(1 - t, n - k) * Math.pow(t, k) * v[k];
		}
		return value;
	}
	
	@Override
	public boolean isFinished() {
		return percentTraversed >= 1;
	}
}
















