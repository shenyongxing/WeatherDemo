package com.jiubang.sx.weatherdemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.jiubang.sx.weatherdemo.R;
import com.jiubang.sx.weatherdemo.utils.BitmapTools;
import com.jiubang.sx.weatherdemo.utils.CVector2D;
import com.jiubang.sx.weatherdemo.utils.CusRotationAnimation;
import com.jiubang.sx.weatherdemo.utils.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 晴天视图
 * Created by shenxing on 15-10-21.
 */
public class WeatherSunnyView extends GLSurfaceView{
    private CVector2D mSwingAxis = new CVector2D(0, 1);
    private float mAngle ;
    private CusRotationAnimation mRotationAnimation ;
    public WeatherSunnyView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        WeatherRender mWeatherRender = new WeatherRender(context);
        setRenderer(mWeatherRender);
    }

    private class WeatherRender implements GLSurfaceView.Renderer {
        private Context mContext ;
        private CylinderView mBackground ;
        private CircleMaskView mCircleMask ;
        private CircleView mCircleView ;
        private WeatherSateView mState_sunny ;
        private TextInfoView mWeaInfo ;
        private TextInfoView mDateInfo ;
        public WeatherRender(Context context) {
            mContext = context ;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//            GLES20.glClearColor(36 / 255.f, 50 / 255.f, 76 / 255.f, 1.0f);
            //        The renderer only renders when the surface is created, or when requestRender() is called.
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i("shenxing", "width : " + width + ", height : " + height) ;
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0, 0, 5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            mBackground = new CylinderView(mContext, ratio / 2, ratio / 2, R.drawable.next_weather_bg, R.drawable.next_weather_bg_thick) ;
            mCircleMask = new CircleMaskView(mContext, ratio / 2, ratio / 2, R.drawable.next_weather_bg_tunnel);
            mCircleView = new CircleView(mContext, ratio / 2 + 0.1f, ratio / 2 + 0.1f, R.drawable.next_weather_bg_tunnel_light) ;
            mState_sunny = new WeatherSateView(mContext, 0.1f, 0.1f, R.drawable.next_weather_sunny) ;
            mState_sunny.mY = 0.1f ;

            Bitmap weaInfoBitmap = BitmapTools.convertViewToBitmap(mContext, "20°C", 20) ;
            mWeaInfo = new TextInfoView(mContext, 1.0f * weaInfoBitmap.getWidth() / width * ratio, 1.0f * weaInfoBitmap.getHeight() / height, weaInfoBitmap) ;
            mWeaInfo.mY = -0.05f;
            Bitmap dateInfoBitmap = BitmapTools.convertViewToBitmap(mContext, "Tus,09-22", 26) ;
            mDateInfo = new TextInfoView(mContext, 1.0f * dateInfoBitmap.getWidth() / width * ratio, 1.0f * dateInfoBitmap.getHeight() / height, dateInfoBitmap) ;
            mDateInfo.mY = -0.15f;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glDisable(GLES20.GL_CULL_FACE);  // 关闭背面剪裁
            if (mRotationAnimation != null) {
                mRotationAnimation.getTransformation(SystemClock.uptimeMillis()) ;
                mAngle = mRotationAnimation.getCurPos() ;
            }
            // 绘制天气状态图
            mState_sunny.mAngle = mAngle;
            mState_sunny.mSwingAxis = mSwingAxis ;
            mState_sunny.mZ = 3.0f ;
            mState_sunny.drawSelf();
            // 绘制天气信息文字
            mWeaInfo.mAngle = mAngle;
            mWeaInfo.mSwingAxis = mSwingAxis ;
            mWeaInfo.mZ = 3.0f ;
            mWeaInfo.drawSelf();
            // 绘制日期信息
            mDateInfo.mAngle = mAngle;
            mDateInfo.mSwingAxis = mSwingAxis ;
            mDateInfo.mZ = 3.0f ;
            mDateInfo.drawSelf();
            // 绘制圆圈背景
            mBackground.mAngle = mAngle ;
            mBackground.mSwingAxis = mSwingAxis ;
            mBackground.drawSelf();
            // 绘制圆圈外层的圆环
            mCircleView.drawSelf();
            // 绘制透明圆环遮罩
            mCircleMask.mAngle = mAngle;
            mCircleMask.mSwingAxis = mSwingAxis ;
            mCircleMask.drawSelf();
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mCurrentX = event.getX();
        float mCurrentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (contains(mCurrentX, mCurrentY)) {
                    float maxPos = 60;
                    float duration = 2000;
                    int tension = 3;
                    float startPos = 0;
                    mRotationAnimation = new CusRotationAnimation(startPos, 0, maxPos, duration, tension, 0.8f);
                    mRotationAnimation.startNow();
                    rotatePanel(mCurrentX, mCurrentY);
                } else {
                    Log.i("WeatherDemo", "click outside ") ;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return true;
    }

    private boolean contains(float x, float y) {
        int halfWidth = getWidth() >> 1 ;
        int halfHeight = getHeight() >> 1;

        float convertX = x - halfWidth ;
        float convertY = -y + halfHeight ;

        // todo 此处是特殊值， 一般应用时需修改
        int left = -halfWidth / 2 ;
        int top = halfHeight / 2 ;
        int right = halfWidth / 2 ;
        int bottom = -halfHeight / 2;

        if (left < right && top > bottom && convertX >= left && convertX < right && convertY >= bottom && convertY < top) {
            return true ;
        } else {
            return false ;
        }
    }

    // 这个算法挺好的， get it
    private void rotatePanel(float x, float y) {
        int halfScreenW = getWidth() >> 1;
        int halfScreenH = getHeight() >> 1;
        float dx = halfScreenW - x;
        float dy = halfScreenH - y;
        float dist = (float)Math.sqrt(dx * dx + dy * dy);
        if (dist > halfScreenW) {
            dist = halfScreenW;
        }

        mSwingAxis.set(halfScreenW - x, halfScreenH - y);
        float nx = halfScreenW - x;
        float ny = halfScreenH - y;
        if (ny == 0) {
            mSwingAxis.set(0, 1.0f);
        } else {
            mSwingAxis.set(1.0f, -(nx * 1.0f) / ny);
        }

        if (y > halfScreenH) {
            mSwingAxis.invert();
        }
        mSwingAxis.normalize();
        Log.i("WeatherDemo", "mSwingAxis : " + mSwingAxis.x + ", " + mSwingAxis.y) ;
        mAngle = (60 * dist / halfScreenW) * (y < halfScreenH ? 1 : -1);
    }
}
