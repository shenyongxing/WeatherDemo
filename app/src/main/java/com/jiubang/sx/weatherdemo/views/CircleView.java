package com.jiubang.sx.weatherdemo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLES20;

import com.jiubang.sx.weatherdemo.utils.MatrixState;

/**
 * 外层圆圈视图
 * Created by sx on 15-10-21.
 */
public class CircleView extends BaseRenderable {
    private int mCircleProgressHandle ;
    private float mProgress = 0.0f ;
    public CircleView(Context context, float width, float height, int drawableId) {
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

    @Override
    protected String getmFragmentShaderFileName() {
        return "circle_frag_tex_at.sh" ;
    }

    @Override
    public void initShader(Context context) {
        super.initShader(context);
        mCircleProgressHandle = GLES20.glGetUniformLocation(mProgram, "mProgress");
    }

    @Override
    public void drawSelf() {
        if (mProgress > 180.f) {
            mProgress = 0 ;
        } else {
            mProgress += 1f ;
        }
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.pushMatrix();
        MatrixState.translate(mX, mY, mZ);
        MatrixState.rotate(mAngle, 0, 0, 1);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(mCircleProgressHandle, mProgress);
        //将顶点数据传入渲染管线
        GLES20.glVertexAttribPointer(
                maPositionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );
        //将纹理数据传入渲染管线
        GLES20.glVertexAttribPointer(
                maTexCoorHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mTextureBuffer
        );
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVCount);
        MatrixState.popMatrix();
    }
}
