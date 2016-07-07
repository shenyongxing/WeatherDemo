package com.jiubang.sx.weatherdemo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLES20;

import com.jiubang.sx.weatherdemo.utils.CVector2D;
import com.jiubang.sx.weatherdemo.utils.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 背景视图, 带一个圆柱
 * Created by sx on 15-10-21.
 */
public class CylinderView extends BaseRenderable {
    private FloatBuffer mNormalBuffer ;
    private CircleMaskView mCircleMaskView ;
    public CylinderView(Context context, float width, float height, int drawableId1, int drawableId2) {
        super(context, width, height, drawableId2);
        mCircleMaskView = new CircleMaskView(context, width, height, drawableId1) ;
    }

    @Override
    protected float[] getVertexData() {
        return null ;
    }

    @Override
    protected float[] getTexureData() {
        return null;
    }

    @Override
    public void initVertexData() {
        int n = 36 ;    //切分的份数
        float r = mWidth ;
        float h = mHeight / 4 ;		//高度
        float angdegSpan = 360.0f / n;
        mVCount = 3 * n * 4; //顶点个数，共有3*n*4个三角形，每个三角形都有三个顶点
        //坐标数据初始化
        float[] vertices = new float[mVCount * 3];
        float[] textures = new float[mVCount * 2];//顶点纹理S、T坐标值数组
        //坐标数据初始化
        int count = 0;
        int stCount = 0;
        for(float angdeg = 0;Math.ceil(angdeg)<360;angdeg += angdegSpan)//侧面
        {
            double angrad = Math.toRadians(angdeg);//当前弧度
            double angradNext = Math.toRadians(angdeg+angdegSpan);//下一弧度
            //底圆当前点---0
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //顶圆下一点---3
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;
            //顶圆当前点---2
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;

            //底圆当前点---0
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //底圆下一点---1
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //顶圆下一点---3
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;
        }
        //法向量数据初始化
        float[] normals = new float[vertices.length];
        for (int i = 0;i<vertices.length;i++) {
            if(i % 3 == 1){
                normals[i] = 0;
            }else{
                normals[i] = vertices[i];
            }
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4);//创建顶点法向量数据缓冲
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //st坐标数据初始化
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length * 4);//创建顶点纹理数据缓冲
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTextureBuffer = cbb.asFloatBuffer();//转换为float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
    }

    @Override
    public void drawSelf() {
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.pushMatrix();
        MatrixState.translate(mX, mY, mZ - 2 * mHeight);
        MatrixState.rotate(-mAngle, mSwingAxis.x, mSwingAxis.y, 0);
//        MatrixState.rotate(-30, 1, 0, 0);
        MatrixState.rotate(90, 1, 0, 0);
        MatrixState.scale(mScale, mScale, 1.0f);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
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

        mCircleMaskView.mAngle = mAngle ;
        MatrixState.pushMatrix();
        mCircleMaskView.mSwingAxis = mSwingAxis ;
        mCircleMaskView.drawSelf();
        MatrixState.popMatrix();
    }
}
