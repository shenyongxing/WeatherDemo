package com.jiubang.sx.weatherdemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import com.jiubang.sx.weatherdemo.R;

/**
 * 
 * @author tang
 *
 */
//CHECKSTYLE:OFF
public class BitmapTools {

	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

		return newbmp;
	}

	// 将view转为bitmap
	public static Bitmap viewToBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bm = view.getDrawingCache();
		return bm;
	}

	public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		return bitmap;
	}

	public static Bitmap convertViewToBitmap(View view) {

		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		return view.getDrawingCache();

	}

	public static Bitmap convertViewToBitmap(Context context, String info, int textSize) {
		TextView tv = new TextView(context) ;
		tv.setTextSize(textSize);
		tv.setTextColor(context.getResources().getColor(R.color.white));
		tv.setText(info);
		return convertViewToBitmap(tv);
	}

}
