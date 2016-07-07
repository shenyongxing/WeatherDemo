package com.jiubang.sx.weatherdemo.utils;

import android.util.Log;
import android.view.animation.Interpolator;

public class CusRotationAnimation extends BaseAnimation {
	private float mStartPos	= 0;
	private float mEndPos = 0;
	private float mMaxPos = 0;
	private float mCurPos = 0;
	
	public CusRotationAnimation(float startPos, float endPos, float maxPos, float duration, int tension, float a){
		mStartPos = startPos;
		mEndPos = endPos;
		mMaxPos = maxPos;
		setDuration((long)duration);
		setInterpolator(new DampInterpolator(tension, a, mStartPos / (mMaxPos - mEndPos)));
	}
	
	public float getCurPos(){
		return mCurPos;
	}
	
	public void setFactor(float factor){
		((DampInterpolator)getInterpolator()).setFactor(factor);
	}
	
	@Override
	public void cancel() {
		super.cancel();
		mCurPos = mMaxPos;
	}

	@Override
	protected void applyTransformation(float interpolatedTime) {
		float detal = (mMaxPos - mEndPos) * interpolatedTime;
		mCurPos = detal;
	}
	
	public class DampInterpolator implements Interpolator{
		private final float			mTension;
		private final float			mA;
		private final float			mOffset;
		private float				mFactor = 2.0f;
		public DampInterpolator(int tension, float a, float offset){
			if(tension < 1){
				tension = 1;
			}
			mTension = tension;
			mA = a;
			if(offset != 0){
				mOffset	= (float) (Math.asin(offset) / (2 * Math.PI * mTension));
			}else{
				mOffset	= offset;
			}
		}
		
		public void setFactor(float factor){
			mFactor = factor;
		}
		
		@Override
		public float getInterpolation(float input) {
			float result;
			float time = input + mOffset;
			result = (float) (mA * Math.pow(1- input, mFactor)
					* Math.sin(2 * Math.PI * mTension * time));
	        return result;
		}
	}
}

