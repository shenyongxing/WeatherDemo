package com.jiubang.sx.weatherdemo.views;

import android.content.Context;

import com.jiubang.sx.weatherdemo.utils.CVector2D;

/**
 * 外层圆圈遮罩
 * Created by sx on 15-10-21.
 */
public class CircleMaskView extends BaseRenderable {
    public CircleMaskView(Context context, float width, float height, int drawableId) {
        super(context, width, height, drawableId);
    }

    @Override
    protected float[] getVertexData() {
        mVCount = 6 ;
        float vertices[] = {
            -mWidth, mHeight, 0,
            -mWidth, -mHeight, 0,
            mWidth, mHeight, 0,

            -mWidth, -mHeight, 0,
            mWidth, -mHeight, 0,
            mWidth, mHeight, 0 };
        return vertices;
    }

    @Override
    protected float[] getTexureData() {
        float[] textures = {
                0f, 0f, 0f, 1, 1, 0f,
                0f, 1,  1, 1,  1, 0f
        };
        return textures;
    }
}
