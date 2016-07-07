package com.jiubang.sx.weatherdemo.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 着色器工具
 * @author shenxing
 */
public class ShaderUtil {
	// 加载指定着色器
	public static int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType) ;
		if (shader != 0) {
			// 加载shader的源代码
			GLES20.glShaderSource(shader, source);
			// 编译shader
			GLES20.glCompileShader(shader);
			// 存放编译结果的数组
			int[] compiled = new int[1] ;
			// 获取编译状态，并保存在compiled数组中
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			// 编译失败时打印错误信息
			if (compiled[0] == 0) {
				Log.e("ES20_ERROR", "Couldn't complie shader " + shaderType + ":") ;
				Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader)) ;
				GLES20.glDeleteShader(shader);
				shader = 0 ;
			}
		}
		return shader ;
	}
	
	// 创建着色器程序的方法
	public static int createProgram(String vertexSource, String fragmentSource) {
		// 加载顶点着色器
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource) ;
		if (vertexShader == 0) {
			return 0 ;
		}
		
		// 加载片元着色器
		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource) ;
		if (pixelShader == 0) {
			return 0 ;
		}
		
		int program = GLES20.glCreateProgram() ;
		// 程序创建成功
		if (program != 0) {
			// 向程序中加入顶点着色器
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader-->vertexShader") ;
			// 向程序中加入片元着色器
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader-->pixelShader") ;
			// 链接程序
			GLES20.glLinkProgram(program);
			// 存放链接状态的数组
			int[] linkStatus = new int[1] ;
			// 获取链接状态
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0) ;
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e("ES20_ERROR", "Couldn't link program ");
				Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program)) ;
				GLES20.glDeleteProgram(program);
				program = 0 ;
			}
		}
		return program ;
	}
	
	// 检查每一步是否有错误
	public static void checkGlError(String op) {
		int error ;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("ES20_ERROR", op + ": glError " + error) ;
			
			throw new RuntimeException(op + ": glError " + error) ;
		}
	}
	
	// 从sh脚本中加载着色器内容
	public static String loadFromAssetsFile(String fname, Resources r) {
		String result = null ;
		try {
			InputStream in = r.getAssets().open(fname) ;
			int ch = 0 ;
			ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
			while ((ch = in.read()) != -1) {
				baos.write(ch) ;
			}
			byte[] buff = baos.toByteArray() ;
			result = new String(buff, "UTF-8") ;
			result.replace("\\r\\n", "\\n") ;
			in.close();
			baos.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result ;
	}
	
}
