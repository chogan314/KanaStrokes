package com.coryhogan.kanastrokes.vectorgraphics;

import java.util.ArrayList;
import java.util.List;

import com.coryhogan.kanastrokes.math.Vec2;

public class KanaPath implements KanaPathComponent {
	private List<KanaPathComponent> components;
	private int currentComponentIdx;
	private boolean finished = false;
	
	public KanaPath() {
		components = new ArrayList<KanaPathComponent>();
	}
	
	@Override
	public float moveCursor(float distance) {
		KanaPathComponent component = components.get(currentComponentIdx);
		float distRemaining = distance;
		
		while(distRemaining > 0) {
			distRemaining = component.moveCursor(distRemaining);
			if (component.isFinished()) {
				if (currentComponentIdx >= components.size() - 1) {
					finished = true;
					return distRemaining;
				}
				currentComponentIdx++;
				component = components.get(currentComponentIdx);
			}
		}
		
		return distRemaining;
	}
	
	@Override
	public Vec2 getCursor() {
		return components.get(currentComponentIdx).getCursor();
	}
	
	@Override
	public void resetCursor() {
		finished = false;
		currentComponentIdx = 0;
		components.get(currentComponentIdx).resetCursor();
	}
	
	public void appendComponent(KanaPathComponent component) {
		components.add(component);
	}
	
	public void insertComponent(KanaPathComponent component, int index) {
		components.add(index, component);
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
}
