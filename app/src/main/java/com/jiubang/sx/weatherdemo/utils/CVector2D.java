package com.jiubang.sx.weatherdemo.utils;


public final class CVector2D {
	public float x, y;
	public static CVector2D TmpVec01 = new CVector2D();
	public static CVector2D TmpVec02 = new CVector2D();
	public static CVector2D TmpVec03 = new CVector2D();
	
	public CVector2D() {
		x = 0;
		y = 0;
	}
	
	public CVector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void invert()
    {
        x = -x;
        y = -y;
    }
	
	public void set(float x, float y)
    {
		this.x = x;
		this.y = y;
    }
	
	public final float magnitude()
    {
        return (float)Math.sqrt(x * x + y * y);
    }
	
	public final float squareMagnitude()
    {
        return x * x + y * y;
    }
	
    public void normalize()
    {
    	float l = magnitude();
    	if (l != 0) {
    		 x /= l;
    		 y /= l;
		}
    }
    
    public static float distance(CVector2D v1, CVector2D v2) {
    	float dx = v1.x - v2.x;
    	float dy = v1.y - v2.y;
    	return (float)Math.sqrt(dx * dx + dy + dy);
    }
    
    public static void interpolator(CVector2D result, CVector2D start, CVector2D end, float factor) {
    	result.x = start.x * (1 - factor) + end.x * factor;
    	result.y = start.y * (1 - factor) + end.y * factor;
    }
    
    public void multiplied(final float value)
    {
        x *= value;
        y *= value;
    }

    public void multiplied(final CVector2D vector)
    {
        x *= vector.x;
        y *= vector.y;
    }
    
    public float dot(CVector2D vector) {
    	return x * vector.x + y * vector.y;
    }

    public void add(final CVector2D v)
    {
    	this.x += v.x;
    	this.y += v.y;
    }
    
    public void add(final float x, final float y)
    {
        this.x += x;
        this.y += y;
    }
}
