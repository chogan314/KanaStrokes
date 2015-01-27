package com.coryhogan.kanastrokes.math;

public class Vec2 {
	private float x, y;
	
	public float getX() { return x; }
	public float getY() { return y; }
	
	public Vec2() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2 cpy() {
		return new Vec2(x, y);
	}
	
	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public float len2() {
		return x * x + y * y;
	}
	
	public Vec2 normalize() {
		float len = len();
		x /= len;
		y /= len;
		return this;
	}
	
	public Vec2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vec2 set(Vec2 other) {
		this.x = other.x;
		this.y = other.y;
		return this;
	}
	
	public Vec2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vec2 add(Vec2 other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}
	
	public Vec2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vec2 sub(Vec2 other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	public Vec2 scl(float val) {
		this.x *= val;
		this.y *= val;
		return this;
	}
	
	public Vec2 setX(float val) {
		this.x = val;
		return this;
	}
	
	public Vec2 setY(float val) {
		this.y = val;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2 other = (Vec2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
