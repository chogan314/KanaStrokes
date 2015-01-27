package com.coryhogan.kanastrokes.vectorgraphics.parsing;

import java.util.ArrayList;
import java.util.List;

import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPath;

public class KanaPathParseNode {
	private List<KanaPathComponentParseNode> components;
	
	public KanaPathParseNode() {
		components = new ArrayList<>();
	}
	
	public KanaPath toPath() {
		KanaPath path = new KanaPath();
		for (KanaPathComponentParseNode component : components) {
			path.appendComponent(component.toComponent());
		}
		return path;
	}
	
	public void appendComponent(KanaPathComponentParseNode component) {
		components.add(component);
	}
	
	public void retrieveVertices(List<Vec2> vertices) {
		for (KanaPathComponentParseNode component : components) {
			component.retrieveVertices(vertices);
		}
	}
	
	public void fitBoundingBox(Vec2 topLeft, Vec2 bottomRight) {
		for (KanaPathComponentParseNode component : components) {
			component.fitBoundingBox(topLeft, bottomRight);
		}
	}
}
