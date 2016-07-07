package com.jiubang.sx.weatherdemo.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;
import android.opengl.Matrix;

/**
 * 存储系统矩阵状态的类
 * @author sx
 */
public class MatrixState {
	private static float[] sProjMatrix = new float[16]; //4x4矩阵 投影用
    private static float[] sVMatrix = new float[16]; //摄像机位置朝向9参数矩阵   
    private static float[] sCurrMatrix; //当前变换矩阵
    public static float[] sLightLocation = new float[]{0, 0, 0}; //定位光光源位置
    public static FloatBuffer sCameraFB;    
    public static FloatBuffer sLightPositionFB;
    
    public static Stack<float[]> sStack = new Stack<float[]>(); //保护变换矩阵的栈
    
    public static void setInitStack()//获取不变换初始矩阵
    {
    	sCurrMatrix = new float[16];
    	Matrix.setRotateM(sCurrMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void pushMatrix()//保护变换矩阵
    {
    	sStack.push(sCurrMatrix.clone());
    }
    
    public static void popMatrix()//恢复变换矩阵
    {
    	sCurrMatrix = sStack.pop();
    }
    
    //设置沿xyz轴移动
    public static void translate(float x, float y, float z) {
    	Matrix.translateM(sCurrMatrix, 0, x, y, z);
    }
    
    //设置绕xyz轴移动
    public static void rotate(float angle, float x, float y, float z) {
    	Matrix.rotateM(sCurrMatrix, 0, angle, x, y, z);
    }
    
    public static void scale(float x, float y, float z)
    {
    	Matrix.scaleM(sCurrMatrix, 0, x, y, z);
    }
    
    
    //设置摄像机
    public static void setCamera(
    		float cx,	//摄像机位置x
    		float cy,   //摄像机位置y
    		float cz,   //摄像机位置z
    		float tx,   //摄像机目标点x
    		float ty,   //摄像机目标点y
    		float tz,   //摄像机目标点z
    		float upx,  //摄像机UP向量X分量
    		float upy,  //摄像机UP向量Y分量
    		float upz   //摄像机UP向量Z分量		
    )
    {
    	Matrix.setLookAtM(
        		sVMatrix, 
        		0, 
        		cx,
        		cy,
        		cz,
        		tx,
        		ty,
        		tz,
        		upx,
        		upy,
        		upz
        );
    	
    	float[] cameraLocation = new float[3]; // 摄像机位置
    	cameraLocation[0] = cx;
    	cameraLocation[1] = cy;
    	cameraLocation[2] = cz;
    	
    	ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder()); // 设置字节顺序
        sCameraFB = llbb.asFloatBuffer();
        sCameraFB.put(cameraLocation);
        sCameraFB.position(0);  
    }
    
    //设置透视投影参数
    public static void setProjectFrustum(
    	float left,		//near面的left
    	float right,    //near面的right
    	float bottom,   //near面的bottom
    	float top,      //near面的top
    	float near,		//near面距离
    	float far       //far面距离
    )
    {
    	Matrix.frustumM(sProjMatrix, 0, left, right, bottom, top, near, far);    	
    }
    
    //设置正交投影参数
    public static void setProjectOrtho(
    	float left,		//near面的left
    	float right,    //near面的right
    	float bottom,   //near面的bottom
    	float top,      //near面的top
    	float near,		//near面距离
    	float far       //far面距离
    )
    {    	
    	Matrix.orthoM(sProjMatrix, 0, left, right, bottom, top, near, far);
    }   
   
    //获取具体物体的总变换矩阵
    public static float[] getFinalMatrix() {
    	float[] mMVPMatrix = new float[16];
    	Matrix.multiplyMM(mMVPMatrix, 0, sVMatrix, 0, sCurrMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, sProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
    
    // 获取具体物体的变换矩阵
    public static float[] getMMatrix()
    {       
        return sCurrMatrix;
    }
    
    // 设置灯光位置的方法
    public static void setLightLocation(float x, float y, float z) {
    	sLightLocation[0] = x;
    	sLightLocation[1] = y;
    	sLightLocation[2] = z;
    	ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder()); // 设置字节顺序
        sLightPositionFB = llbb.asFloatBuffer();
        sLightPositionFB.put(sLightLocation);
        sLightPositionFB.position(0);
    }
    //获取摄像机朝向的矩阵
    public static float[] getCaMatrix()
    {
		return sVMatrix;
    } 
    //获取投影矩阵
    public static float[] getProjMatrix()
    {
		return sProjMatrix;
    }
}
