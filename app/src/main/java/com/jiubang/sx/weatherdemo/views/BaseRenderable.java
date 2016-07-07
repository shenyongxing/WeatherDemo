package com.jiubang.sx.weatherdemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.jiubang.sx.weatherdemo.utils.CVector2D;
import com.jiubang.sx.weatherdemo.utils.MatrixState;
import com.jiubang.sx.weatherdemo.utils.ShaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 可绘制对象基类
 * @author shenxing
 */
public abstract class BaseRenderable {
	protected int mProgram ;		// 自定义渲染管线着色器id
	protected int maPositionHandle ;		// 顶点位置属性引用
	protected int muMVPMatrixHandle ;		// 总变换矩阵引用
	private int muMMatrixHandle;	// 位置、旋转变换矩阵引用
	private int maCameraHandle; // 摄像机位置属性引用 
	protected int maTexCoorHandle; // 顶点纹理坐标属性引用

	private String mVertexShader ;		// 顶点着色器
	private String mFragmentShader ;		//片元着色器
	protected FloatBuffer   mVertexBuffer; // 顶点坐标数据缓冲
    protected FloatBuffer   mTextureBuffer; // 顶点着色数据缓冲
    protected int mVCount = 0 ;		// 顶点数量

	protected float mX ;
	protected float mY ;
	protected float mZ ;
	protected float mAngle ;
	protected float mScale = 1.0f;

    protected float mWidth;
    protected float mHeight;
    protected int mTexId ;
    private Context mContext ;
	public CVector2D mSwingAxis = new CVector2D(0, 1);


	public BaseRenderable(Context context, float width, float height, int drawableId) {
		this.mWidth = width;
    	this.mHeight = height;
    	initVertexData();
    	initShader(context) ;
    	mContext = context ;
    	mTexId = initTexture(drawableId) ;
	}

	public BaseRenderable(Context context, float width, float height, Bitmap bitmap) {
		this.mWidth = width;
		this.mHeight = height;
		initVertexData();
		initShader(context) ;
		mContext = context ;
		mTexId = initTexture(bitmap) ;
	}

	//初始化顶点坐标与着色数据的方法
    public void initVertexData() {
    	//顶点坐标数据的初始化================begin============================
		float[] vertices = getVertexData() ;
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // 设置为本地字节顺序
        mVertexBuffer = vbb.asFloatBuffer(); // 转换为int型缓冲
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float textures[] = getTexureData() ;
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
        tbb.order(ByteOrder.nativeOrder()); //设置字节顺序
        mTextureBuffer = tbb.asFloatBuffer(); //转换为Float型缓冲
        mTextureBuffer.put(textures); //向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0); //设置缓冲区起始位置
        //顶点纹理数据的初始化================end============================
    }

	// 获取顶点数据
	protected abstract float[] getVertexData() ;

	protected abstract float[] getTexureData() ;

	protected String getmVertexShaderFileName() {
		return "vertex_tex_at.sh" ;
	}

	protected String getmFragmentShaderFileName() {
		return "frag_tex_at.sh" ;
	}

    // 初始化着色器
    public void initShader(Context context) {
    	//加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile(getmVertexShaderFileName(), context.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile(getmFragmentShaderFileName(), context.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用 
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }

	/**
	 * 绘制
	 */
   	public void drawSelf() {
		//制定使用某套着色器程序
		GLES20.glUseProgram(mProgram);
		MatrixState.setInitStack();
		MatrixState.pushMatrix();
    	MatrixState.translate(mX, mY, mZ);
		MatrixState.rotate(-mAngle, mSwingAxis.x, mSwingAxis.y, 0);
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
   	}

	/**
	 * 初始化纹理图
	 * @param drawableId
	 * @return
	 */
	public int initTexture(int drawableId) {
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);
		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		// 设置采样模式
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = mContext.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D(
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}

	/**
	 * 初始化纹理图
	 * @param bitmap
	 * @return
	 */
	public int initTexture(Bitmap bitmap) {
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);

		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		// 设置采样模式
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

		//实际加载纹理
		GLUtils.texImage2D(
				GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
				0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
				bitmap, 			  //纹理图像
				0					  //纹理边框尺寸
		);
		bitmap.recycle(); 		  //纹理加载成功后释放图片

		return textureId;
	}
}
