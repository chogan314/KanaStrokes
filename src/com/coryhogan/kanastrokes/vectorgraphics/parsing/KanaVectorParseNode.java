package com.coryhogan.kanastrokes.vectorgraphics.parsing;

import java.util.ArrayList;
import java.util.List;

import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.vectorgraphics.KanaVector;

public class KanaVectorParseNode {
	private List<KanaPathParseNode> paths;
	
	public KanaVectorParseNode() {
		paths = new ArrayList<KanaPathParseNode>();
	}
	
	public void appendPath(KanaPathParseNode path) {
		paths.add(path);
	}
	
	public KanaVector toKanaVector(float displayWidth, float displayHeight, float paddingX, float paddingY) {
		List<Vec2> vertices = new ArrayList<Vec2>();
		for (KanaPathParseNode path : paths) {
			path.retrieveVertices(vertices);
		}
		
		Vec2 sourceTopLeft = new Vec2(-1, -1);
		Vec2 sourceBottomRight = new Vec2(-1, -1);
		
		for (KanaPathParseNode path : paths) {
			path.fitBoundingBox(sourceTopLeft, sourceBottomRight);
		}
		
		Vec2 destTopLeft = new Vec2(paddingX, paddingY);
		Vec2 destBottomRight = new Vec2(displayWidth - paddingX, displayHeight - paddingY);
		
		float sourceWidth = sourceBottomRight.getX() - sourceTopLeft.getX();
		float sourceHeight = sourceBottomRight.getY() - sourceTopLeft.getY();
		float destWidth = destBottomRight.getX() - destTopLeft.getX();
		float destHeight = destBottomRight.getY() - destTopLeft.getY();
		
		float widthRatio = destWidth / sourceWidth;
		float heightRatio = destHeight / sourceHeight;
		float scaleValue = 0;
		
		if (widthRatio < 1 || heightRatio < 1) {
			scaleValue = Math.min(widthRatio, heightRatio);			
		} else if (widthRatio > 1 && heightRatio > 1) {
			scaleValue = Math.min(widthRatio, heightRatio);
		}
		
		Vec2 tl = sourceTopLeft.cpy();
		Vec2 br = sourceBottomRight.cpy();
		br.sub(tl);
		tl.set(0, 0);
		br.scl(scaleValue);
		tl.add(destTopLeft);
		br.add(destTopLeft);
		
		if (br.getX() < destBottomRight.getX()) {
			float diffX = destBottomRight.getX() - br.getX();
			br.add(diffX / 2, 0);
			tl.add(diffX / 2, 0);
		}
		
		if (br.getY() < destBottomRight.getY()) {
			float diffY = destBottomRight.getY() - br.getY();
			br.add(0, diffY / 2);
			tl.add(0, diffY / 2);
		}
		
		for (Vec2 vertex : vertices) {
			float newX = map(vertex.getX(), sourceTopLeft.getX(), sourceBottomRight.getX(), tl.getX(), br.getX());
			float newY = map(vertex.getY(), sourceTopLeft.getY(), sourceBottomRight.getY(), tl.getY(), br.getY());
			vertex.set(newX, newY);
		}
		
		KanaVector kanaVector = new KanaVector();
		for (KanaPathParseNode path : paths) {
			kanaVector.appendStroke(path.toPath());
		}
		return kanaVector;
	}
	
	private float map(float value, float r1Start, float r1End, float r2Start, float r2End) {
		float r1Deviation = r1End - r1Start;
		float r2Deviation = r2End - r2Start;
		float deviationsFromR1Start = (value - r1Start) / r1Deviation;
		return r2Start + (r2Deviation * deviationsFromR1Start);
	}
}
