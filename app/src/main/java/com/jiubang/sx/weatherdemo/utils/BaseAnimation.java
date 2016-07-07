package com.jiubang.sx.weatherdemo.utils;


import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public abstract class BaseAnimation {
	public static final int INFINITE = -1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;

	private Interpolator		mInterpolator = null;
	
	private boolean		mMore = false;
	private boolean 	mInitialized = false;
	private boolean		mStarted = false;
	private boolean		mEnded = false;
	private boolean 	mOneMoreTime = true;
	
	private long		mStartTime = 0;
	private long		mStartOffset = 0;
	
	private long		mDuration = 1;
	private int			mRepeatCount = 0;
	private int			mRepeated = 0;

	private String		mName = "";
	
	private GLAnimationListener		mListener = null;
	
	public interface GLAnimationListener {
		public void onAnimationStart(BaseAnimation a);
		public void	onAnimationEnd(BaseAnimation a);
		public void onAnimationRepeat(BaseAnimation a);
	}
	
	public BaseAnimation() {
		
	}
	
	public void reset() {
		mInitialized = false;
        mRepeated = 0;
        mMore = true;
        mOneMoreTime = true;
    }
	
	public void cancel() {
        if (mStarted && !mEnded) {
            if (mListener != null) mListener.onAnimationEnd(this);
            mEnded = true;
        }
        // Make sure we move the animation to the end
        mStartTime = Long.MIN_VALUE;
        mMore = mOneMoreTime = false;
    }
	
	public void initialize() {
        reset();
        mInitialized = true;
    }
	
	public boolean isInitialized() {
		return mInitialized;
	}
	
	public Interpolator getInterpolator() {
		return mInterpolator;
	}
	
	public boolean getTransformation(long currentTime) {
		if (mStartTime == -1) {
            mStartTime = currentTime;
        }

        final long startOffset = getStartOffset();
        final long duration = mDuration;
        float normalizedTime;
        if (duration != 0) {
            normalizedTime = ((float) (currentTime - (mStartTime + startOffset))) /
                    (float) duration;
        } else {
            // time is a step-change with a zero duration
            normalizedTime = currentTime < mStartTime ? 0.0f : 1.0f;
        }

        final boolean expired = normalizedTime >= 1.0f;
        mMore = !expired;

        if (normalizedTime >= 0.0f && normalizedTime <= 1.0f) {
            if (!mStarted) {
                mStarted = true;
            }

            if (mInterpolator == null) {
				mInterpolator = new LinearInterpolator();
			}
            
            final float interpolatedTime = mInterpolator.getInterpolation(normalizedTime);
            applyTransformation(interpolatedTime);
        }

        if (expired) {
            if (mRepeatCount == mRepeated) {
                if (!mEnded) {
                    mEnded = true;
                    if (mListener != null) {
                        mListener.onAnimationEnd(this);
                    }
                }
            } else {
                if (mRepeatCount > 0) {
                    mRepeated++;
                }

                mStartTime = -1;
                mMore = true;

                if (mListener != null) {
                    mListener.onAnimationRepeat(this);
                }
            }
        }

        if (!mMore && mOneMoreTime) {
            mOneMoreTime = false;
            return true;
        }

        return mMore;
	}
	
	protected void applyTransformation(float interpolatedTime) {
    }
	
	public long getStartOffset() {
		return mStartOffset;
	}
	
	public void setStartOffset(long startOffset) {
        mStartOffset = startOffset;
    }
	
	public void setDuration(long durationMillis) {
        if (durationMillis < 0) {
            throw new IllegalArgumentException("GLAnimation duration cannot be negative");
        }
        mDuration = durationMillis;
    }
	
	public void setStartTime(long startTimeMillis) {
        mStartTime = startTimeMillis;
        mStarted = mEnded = false;
        mRepeated = 0;
        mMore = true;
    }
	
	public void start() {
        setStartTime(-1);
    }
	
	public void startNow() {
        setStartTime(SystemClock.uptimeMillis());
    }
	
	public void setRepeatCount(int repeatCount) {
        if (repeatCount < 0) {
            repeatCount = INFINITE;
        }
        mRepeatCount = repeatCount;
    }
	
	public void setAnimationListener(GLAnimationListener listener) {
        mListener = listener;
    }
	
	public void setInterpolator(Interpolator i) {
        mInterpolator = i;
    }
	
	public void setName(String name) {
		mName = name;
	}
	
	public String name() {
		return mName;
	}
	
	public boolean hasStarted() {
        return mStarted;
    }
	
	public boolean hasEnded() {
        return mEnded;
    }
}
