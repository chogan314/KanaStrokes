package com.coryhogan.kanastrokes.vectorgraphics.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.vectorgraphics.BezierConstants;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPathComponent;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPathCubicCurve;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPathLine;
import com.coryhogan.kanastrokes.vectorgraphics.KanaPathQuadraticCurve;

public class KanaPathComponentParseNode {
	private static final float NRRF_PRECISION = 0.000001f;
	
	public enum ComponentType {
		LINE, QUADRATIC_CURVE, CUBIC_CURVE
	}
	
	private ComponentType type;
	private List<Vec2> vertices;
	
	public KanaPathComponentParseNode(ComponentType type) {
		this.type = type;
		vertices = new ArrayList<Vec2>();
	}
	
	public KanaPathComponent toComponent() {
		switch (type) {
		case CUBIC_CURVE:
			return new KanaPathCubicCurve(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3));
		case LINE:
			return new KanaPathLine(vertices.get(0), vertices.get(1));
		case QUADRATIC_CURVE:
			return new KanaPathQuadraticCurve(vertices.get(0), vertices.get(1), vertices.get(2));
		}
		return null;
	}
	
	public void appendVertex(Vec2 vertex) {
		vertices.add(vertex.cpy());
	}
	
	public void retrieveVertices(List<Vec2> vertices) {
		vertices.addAll(this.vertices);
	}
	
	public void fitBoundingBox(Vec2 topLeft, Vec2 bottomRight) {
		switch (type) {
		case CUBIC_CURVE:
			fitBoundingBoxCurve(topLeft, bottomRight);
			break;
		case LINE:
			fitBoundingBoxToVertices(vertices, topLeft, bottomRight);
			break;
		case QUADRATIC_CURVE:
			fitBoundingBoxCurve(topLeft, bottomRight);
			break;
		default:
			break;		
		}
	}
	
	private void fitBoundingBoxToVertices(List<Vec2> vertices, Vec2 topLeft, Vec2 bottomRight) {
		for (Vec2 vertex : vertices) {
			if (topLeft.getX() < 0 || vertex.getX() < topLeft.getX()) {
				topLeft.setX(vertex.getX());
			}
			if (topLeft.getY() < 0 || vertex.getY() < topLeft.getY()) {
				topLeft.setY(vertex.getY());
			}
			if (bottomRight.getX() < 0 || vertex.getX() > bottomRight.getX()) {
				bottomRight.setX(vertex.getX());
			}
			if (bottomRight.getY() < 0 || vertex.getY() > bottomRight.getY()) {
				bottomRight.setY(vertex.getY());
			}
		}
	}
	
	private void fitBoundingBoxCurve(Vec2 topLeft, Vec2 bottomRight) {
		float[] inflections = getInflections();
		
		List<Vec2> points = new ArrayList<Vec2>();
		for (int i = 0; i < inflections.length; i++) {
			float t = inflections[i];
			points.add(getPointAt(t));
		}
		
		fitBoundingBoxToVertices(points, topLeft, bottomRight);
	}
	
	private float map(float value, float r1Start, float r1End, float r2Start, float r2End) {
		float r1Deviation = r1End - r1Start;
		float r2Deviation = r2End - r2Start;
		float deviationsFromR1Start = (value - r1Start) / r1Deviation;
		return r2Start + (r2Deviation * deviationsFromR1Start);
	}
	
	private float calcDerivative(int derivative, float t, float[] v) {
		int n = v.length - 1;
		if (n == 0) {
			return 0;
		}
		
		if (derivative == 0) {
			float value = 0;
			for (int k = 0; k <= n; k++) {
				value += BezierConstants.binomialCoefficients[n][k] * 
						Math.pow(1 - t, n - k) * Math.pow(t, k) * v[k];
			}
			return value;
		}
		
		float[] vPrime = new float[v.length - 1];
		for (int k = 0; k < vPrime.length; k++) {
			vPrime[k] = n * (v[k + 1] - v[k]);
		}
		return calcDerivative(derivative - 1, t, vPrime);
	}
	
	private float[] findAllRoots(int derivative, float[] values) {
		float[] none = new float[0];
		
		if (values.length - derivative <= 1) {
			return none;
		}
		
		if (values.length - derivative == 2) {
			while(values.length > 2) {
				float[] vPrime = new float[values.length - 1];
				for (int k = 0, n = vPrime.length; k < n; k++) {
					vPrime[k] = n * (values[k + 1] - values[k]);
				}
				values = vPrime;
			}
			
			if (values.length < 2) {
				return none;
			}
			
			float root = map(0, values[0], values[1], 0, 1);
			if (root < 0 || root > 1) {
				return none;
			}
			return new float[] { root };
		}
		
		List<Float> roots = new ArrayList<Float>();
		float root;
		for (float t = 0; t <= 1.0; t+= 0.01)	{
			try {
				root = Math.round(findRoots(derivative, t, values) / NRRF_PRECISION) * NRRF_PRECISION;
				if (root < 0 || root > 1) {
					continue;
				}
				if (Math.abs(root - t) <= NRRF_PRECISION) {
					continue;
				}
				if (roots.contains(root)) {
					continue;
				}
				roots.add(root);
			} catch (RuntimeException e) {
				// Do nothing
			}
		}
		
		float[] ret = new float[roots.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = roots.get(i);
		}
		return ret;
	}
	
	private float findRoots(int derivative, float t, float[] values) {
		return findRoots(derivative, t, values, 0);
	}
	
	private float findRoots(int derivative, float t, float[] values, float offset) {
		return findRootsRecursive(derivative, t, values, offset, 0);
	}
	
	private float findRootsRecursive(int derivative, float t, float[] values, float offset, float depth) throws RuntimeException {
		float f = calcDerivative(derivative, t, values) - offset;
		float df = calcDerivative(derivative + 1, t, values);
		float t2 = 0;
		
		if (df == 0) {
			t2 = t - f;
		} else {
			t2 = t - (f / df);
		}
		
		if (depth > 12) {
			if (Math.abs(t - t2) < NRRF_PRECISION) {
				return (int) (t2 / NRRF_PRECISION) * NRRF_PRECISION;
			}
			throw new RuntimeException("Newton-Raphson ran past recursion depth");
		}
		
		if (Math.abs(t - t2) > NRRF_PRECISION) {
			return findRootsRecursive(derivative, t2, values, offset, depth + 1);
		}
		
		return t2;
	}
	
	private float[] getInflections() {
		float[] ret = {};
		List<Float> tValues = new ArrayList<Float>();
		tValues.add(0.0f);
		tValues.add(1.0f);
		
		float[] xValues = new float[vertices.size()];
		float[] yValues = new float[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			xValues[i] = vertices.get(i).getX();
			yValues[i] = vertices.get(i).getY();
		}
		
		float[] roots;
		roots = findAllRoots(1, xValues);
		for (float t : roots) {
			if (0 < t && t < 1) {
				tValues.add(t);
			}
		}
		
		roots = findAllRoots(1, yValues);
		for (float t : roots) {
			if (0 < t && t < 1) {
				tValues.add(t);
			}
		}
		
		int order = vertices.size() - 1;
		if (order > 2) {
			roots = findAllRoots(2, xValues);
			for (float t : roots) {
				if (0 < t && t < 1) {
					tValues.add(t);
				}
			}
			
			roots = findAllRoots(2, yValues);
			for (float t : roots) {
				if (0 < t && t < 1) {
					tValues.add(t);
				}
			}
		}
		
		ret = new float[tValues.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = tValues.get(i);
		}
		Arrays.sort(ret);
		tValues = new ArrayList<Float>();
		for (float f : ret) {
			if (!tValues.contains(f)) {
				tValues.add(f);
			}
		}
		
		ret = new float[tValues.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = tValues.get(i);
		}
		
		if (ret.length > (2 * order + 2)) {
			Log.d("KanaPathComponentParseNode", "getInflections returning too many roots: " + ret.length);
			return new float[0];
		}
		return ret;
	}
	
	private Vec2 getPointAt(float t) {
		if (type.equals(ComponentType.QUADRATIC_CURVE)) {
			float t2 = t * t;
			float mt = 1 - t;
			float mt2 = mt * mt;
			
			Vec2 p0 = vertices.get(0);
			Vec2 p1 = vertices.get(1);
			Vec2 p2 = vertices.get(2);
			
			Vec2 p0Scl = p0.cpy().scl(mt2);
			Vec2 p1Scl = p1.cpy().scl(2 * mt * t);
			Vec2 p2Scl = p2.cpy().scl(t2);
			
			return p2Scl.add(p1Scl.add(p0Scl));
		} else if (type.equals(ComponentType.CUBIC_CURVE)) {
			float t2 = t * t;
			float t3 = t2 * t;
			float mt = 1 - t;
			float mt2 = mt * mt;
			float mt3 = mt * mt2;
			
			Vec2 p0 = vertices.get(0);
			Vec2 p1 = vertices.get(1);
			Vec2 p2 = vertices.get(2);
			Vec2 p3 = vertices.get(3);
			
			Vec2 p0Scl = p0.cpy().scl(mt3);
			Vec2 p1Scl = p1.cpy().scl(3 * mt2 * t);
			Vec2 p2Scl = p2.cpy().scl(3 * mt * t2);
			Vec2 p3Scl = p3.cpy().scl(t3);
			
			return p3Scl.add(p2Scl.add(p1Scl.add(p0Scl)));
		}
		
		return null;
	}
}



















