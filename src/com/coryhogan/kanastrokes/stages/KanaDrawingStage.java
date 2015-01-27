package com.coryhogan.kanastrokes.stages;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.coryhogan.kanastrokes.input.Input.TouchEvent;
import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.rendering.RenderContext;

public class KanaDrawingStage extends Stage {
	private static final int DRAWING_CONTEXT_HISTORY_SIZE = 10;
	private RenderContext[] drawingContextHistory;
	private int drawingContextHistoryIndex;
	private RenderContext drawingContext;
	private AtomicBoolean clearFlag;
	private AtomicBoolean gridFlag;
	private AtomicBoolean eraseFlag;
	private AtomicBoolean undoStrokeFlag;
	private AtomicInteger strokeWidth;
	private AtomicInteger strokeColor;
	private Vec2 lastVertex = null;

	public KanaDrawingStage(View view) {
		super(view, "KanaDrawingStage");
		drawingContextHistory = new RenderContext[DRAWING_CONTEXT_HISTORY_SIZE];
		drawingContextHistoryIndex = -1;
		drawingContext = new RenderContext(width, height, Bitmap.Config.ARGB_8888);
		drawingContext.clearToTransparent();
		
		clearFlag = new AtomicBoolean();
		gridFlag = new AtomicBoolean();
		eraseFlag = new AtomicBoolean();
		undoStrokeFlag = new AtomicBoolean();
		strokeWidth = new AtomicInteger(10);
		strokeColor = new AtomicInteger(Color.BLACK);
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
		if (undoStrokeFlag.compareAndSet(true, false) && drawingContextHistoryIndex >= 0) {
			drawingContext.dispose();
			drawingContext = drawingContextHistory[drawingContextHistoryIndex];
			drawingContextHistoryIndex--;
		}
		
		drawingContext.setAntiAlias(true);
		
		if (clearFlag.compareAndSet(true, false)) {
			drawingContext.clearToTransparent();
		}
		
		List<TouchEvent> touchEvents = input.getTouchEvents();
		
		for (int i = 0; i < touchEvents.size(); i++) {
			TouchEvent touchEvent = touchEvents.get(i);
				
			if (touchEvent.pointer != 0) {
				continue;
			}
			
			if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
				if (drawingContextHistoryIndex >= DRAWING_CONTEXT_HISTORY_SIZE - 1) {
					drawingContextHistory[0].dispose();
					for (int j = 0; j < DRAWING_CONTEXT_HISTORY_SIZE - 1; j++) {
						drawingContextHistory[j] = drawingContextHistory[j + 1];
					}
				} else {
					drawingContextHistoryIndex++;
				}
				drawingContextHistory[drawingContextHistoryIndex] = drawingContext.copy();
			}
			
			Vec2 vertex = new Vec2(touchEvent.x, touchEvent.y);
			
			boolean erasing = eraseFlag.get();
			int eraseWidth = (int) (strokeWidth.get() * 1.5);
			
			if (erasing) {
				drawingContext.eraseCircle(vertex, eraseWidth / 2);
			} else {
				drawingContext.drawCircle(vertex, strokeWidth.get() / 2, strokeColor.get());
			}
			
			if (lastVertex != null) {
				if (erasing) {
					drawingContext.eraseLine(lastVertex, vertex, eraseWidth);
				} else {
					drawingContext.drawLine(lastVertex, vertex, strokeWidth.get(), strokeColor.get());
				}
			}
			
			switch (touchEvent.type) {
			case TouchEvent.TOUCH_DOWN:
				lastVertex = vertex;
				break;
			case TouchEvent.TOUCH_DRAGGED:
				lastVertex = vertex;
				break;
			case TouchEvent.TOUCH_UP:
				lastVertex = null;
				break;
			}
		}
	}

	@Override
	public void present(float interpFactor) {		
		frameBuffer.clear(255, 255, 255);
		frameBuffer.setAntiAlias(true);
		
		if (gridFlag.get()) {
			frameBuffer.drawDashedLine(topCenter, bottomCenter, 4, 0xff888888, height / 10, height / 20);
			frameBuffer.drawDashedLine(centerLeft, centerRight, 4, 0xff888888, width / 10, width / 20);
		}
		
		frameBuffer.drawBitmap(drawingContext.getSource());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		drawingContext.dispose();
		for (int i = 0; i < drawingContextHistoryIndex; i++) {
			drawingContextHistory[i].dispose();
		}		
	}
	
	public void setClearFlag() {
		clearFlag.compareAndSet(false, true);
		eraseFlag.compareAndSet(true, false);
	}
	
	public void toggleGrid() {
		if (gridFlag.get()) {
			gridFlag.set(false);
		} else {
			gridFlag.set(true);
		}
	}
	
	public void setStrokeWidth(int width) {
		strokeWidth.set(width);
	}
	
	public void setStrokeColor(int color) {
		strokeColor.set(color);
	}
	
	public void toggleErase() {
		if (eraseFlag.get()) {
			eraseFlag.set(false);
		} else {
			eraseFlag.set(true);
		}
	}
	
	public void setUndoStrokeFlag() {
		undoStrokeFlag.compareAndSet(false, true);
	}
}








