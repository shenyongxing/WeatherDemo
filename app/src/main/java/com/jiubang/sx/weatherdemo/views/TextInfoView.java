package com.jiubang.sx.weatherdemo.views;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 外层圆圈遮罩
 * Created by sx on 15-10-21.
 */
public class TextInfoView extends BaseRenderable {

    public TextInfoView(Context context, float width, float height, Bitmap bitmap) {
        super(context, width, height, bitmap);
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
