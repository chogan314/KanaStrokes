package com.coryhogan.kanastrokes.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.coryhogan.kanastrokes.stages.Stage;
import com.coryhogan.kanastrokes.stages.Stage.StageFactory;

public class RenderView<T extends Stage> extends SurfaceView 
								  					  implements SurfaceHolder.Callback, Runnable {
	
	private static final float UPDATE_RATE = 1f / 120f;
	
	private SurfaceHolder surfaceHolder;
	private Thread renderThread;
	private volatile boolean running = false;
	private boolean initialized = false;
	
	private StageFactory<T> stageFactory;
	private T stage;
	
	private boolean logFPS = false;

	public RenderView(Context context, StageFactory<T> stageFactory) {
		super(context);
		this.stageFactory = stageFactory;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		stage = stageFactory.createStage(this);
		running = true;
		renderThread = new Thread(this, stage.getName());
		renderThread.start();
		initialized = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		while(true) {
			try {
				renderThread.join();
				break;
			} catch (InterruptedException e) {
				// retry
			}
		}
		initialized = false;
		stage.dispose();
	}
	
	@Override
	public void run() {
		Rect dstRect = new Rect();
		long startTime = System.nanoTime();
		
		int frames = 0;
		float accumulatedTime = 0;
		float frameTime = 0;
		while(running) {
			if (!surfaceHolder.getSurface().isValid()) {
				continue;
			}
			
			float delta = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();
			
			if (logFPS) {
				frames++;			
				frameTime += delta;
				
				while (frameTime >= 1) {
					Log.d("FPS", frames + "");
					frames = 0;
					frameTime -= 1;
				}
			}
			
			accumulatedTime += delta;
			
			while (accumulatedTime >= UPDATE_RATE) {
				stage.update(accumulatedTime);
				accumulatedTime -= UPDATE_RATE;
			}
			
			stage.present(accumulatedTime);
			
			Canvas canvas = surfaceHolder.lockCanvas();
			if (canvas != null) {
				canvas.getClipBounds(dstRect);
				canvas.drawBitmap(stage.getFrameBuffer().getSource(), null, dstRect, null);
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
	public T getStage() {
		return stage;
	}
	
	public void doLogFPS(boolean log) {
		this.logFPS = log;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
}
