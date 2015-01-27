package com.coryhogan.kanastrokes.rendering;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.coryhogan.kanastrokes.math.Vec2;

public class RenderContext {
	private Bitmap source;
	private Canvas canvas;
	private Paint paint;
	private Paint erasePaint;
	private Rect rect;
	
	public RenderContext(int width, int height, Bitmap.Config config) {
		source = Bitmap.createBitmap(width, height, config);
		canvas = new Canvas(source);
		paint = new Paint();
		erasePaint = new Paint();
		erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		erasePaint.setColor(0x00000000);
		rect = new Rect();
	}
	
	private RenderContext(RenderContext parent) {
		source = parent.source.copy(parent.source.getConfig(), true);
		canvas = new Canvas(source);
		paint = new Paint(parent.paint);
		erasePaint = new Paint(parent.erasePaint);
		rect = new Rect(parent.rect);
	}
	
	public RenderContext copy() {
		return new RenderContext(this);
	}
	
	public void setAntiAlias(boolean antiAlias) {
		paint.setAntiAlias(antiAlias);
		erasePaint.setAntiAlias(antiAlias);
	}
	
	public void clear(int r, int g, int b) {
		canvas.drawRGB(r, g, b);
	}
	
	public void clearToTransparent() {
		source.eraseColor(0x00000000);
	}
	
	public void drawBitmap(Bitmap bitmap) {
		canvas.getClipBounds(rect);
		canvas.drawBitmap(bitmap, null, rect, null);
	}
	
	public void drawBitmap(Bitmap bitmap, int xOffset, int yOffset) {
		canvas.drawBitmap(bitmap, xOffset, yOffset, null);
	}
	
	public void drawCircle(Vec2 position, int radius, int color) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		canvas.drawCircle(position.getX(), position.getY(), radius, paint);
	}
	
	public void eraseCircle(Vec2 position, int radius) {
		canvas.drawCircle(position.getX(), position.getY(), radius, erasePaint);		
	}
	
	public void drawLine(Vec2 start, Vec2 stop, int strokeWidth, int color) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		canvas.drawLine(start.getX(), start.getY(), stop.getX(), stop.getY(), paint);		
	}
	
	public void drawDashedLine(Vec2 pointA, Vec2 pointB, int strokeWidth, int color, int segmentLength, int gapLength) {
		Vec2 start;
		Vec2 stop;
		
		if (pointA.len2() < pointB.len2()) {
			start = pointA;
			stop = pointB;
		} else {
			start = pointB;
			stop = pointA;
		}
		
		Vec2 trajectory = stop.cpy().sub(start).normalize();
		Vec2 currentPoint = start.cpy();
		Vec2 nextPoint = new Vec2();
		
		boolean drawing = true;
		
		while(currentPoint.len2() < stop.len2()) {			
			if (drawing) {
				nextPoint.set(currentPoint);
				nextPoint.add(trajectory.cpy().scl(segmentLength));
				drawLine(currentPoint, nextPoint, strokeWidth, color);
				drawing = false;
			} else {
				nextPoint.set(currentPoint);
				nextPoint.add(trajectory.cpy().scl(gapLength));
				drawing = true;
			}
			currentPoint.set(nextPoint);
		}
	}
	
	public void eraseLine(Vec2 start, Vec2 stop, int strokeWidth) {
		erasePaint.setStrokeWidth(strokeWidth);
		canvas.drawLine(start.getX(), start.getY(), stop.getX(), stop.getY(), erasePaint);
	}
	
	public void drawPath(List<Vec2> vertices, int color, int strokeWidth) {
		if (vertices.size() < 2) {
			return;
		}
		
		Path path = new Path();
		path.moveTo(vertices.get(0).getX(), vertices.get(0).getY());
		for (int i = 0; i < vertices.size(); i++) {
			Vec2 vertex = vertices.get(i);
			path.lineTo(vertex.getX(), vertex.getY());
		}
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		
		canvas.drawPath(path, paint);
	}
	
	public void drawText(String text, int x, int y, int maxWidth, int maxHeight, int color) {
		final float testTextSize = 48f;
		paint.setTextSize(testTextSize);
		paint.getTextBounds(text, 0, text.length(), rect);
		
		float desiredTextWidth = testTextSize * maxWidth / rect.width();
		float desiredTextHeight = testTextSize * maxHeight / rect.height();
		float desiredTextSize = Math.min(desiredTextWidth, desiredTextHeight);
		
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(desiredTextSize);
		paint.setColor(color);
		
		canvas.drawText(text, x, y, paint);
	}
	
	public void dispose() {
		source.recycle();
	}
	
	public Bitmap getSource() {
		return source;
	}
}
