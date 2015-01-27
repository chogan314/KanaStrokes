package com.coryhogan.kanastrokes.stages;

import android.graphics.Bitmap;
import android.view.View;

import com.coryhogan.kanastrokes.input.AndroidInput;
import com.coryhogan.kanastrokes.input.Input;
import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.rendering.RenderContext;

public abstract class Stage {	
	public interface StageFactory<T extends Stage> {
		public T createStage(View view);
	}
	
	protected RenderContext frameBuffer;
	protected final int width, height;
	protected final Vec2 topCenter, centerRight, bottomCenter, centerLeft;
	protected Input input;
	private String name;
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public Stage(View view, String name) {
		this.width = view.getWidth();
		this.height = view.getHeight();
		
		topCenter = new Vec2(width / 2, 0);
		bottomCenter = new Vec2(width / 2, height);
		centerLeft = new Vec2(0, height / 2);
		centerRight = new Vec2(width, height / 2);
		
		this.name = name;
		
		input = new AndroidInput(view.getContext(), view);
		
		frameBuffer = new RenderContext(width, height, Bitmap.Config.ARGB_8888);
	}
	
	public abstract void resume();
	
	public abstract void pause();
	
	public abstract void update(float delta);
	
	public abstract void present(float interpFactor);
	
	public void dispose() {
		frameBuffer.dispose();
	}
	
	public RenderContext getFrameBuffer() {
		return frameBuffer;
	}
	
	public String getName() {
		return name;
	}
}
