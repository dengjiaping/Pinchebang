package com.lepin.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.lepin.activity.R;

/**
 * 用于在地图选点时标注
 * 
 */
public class MapIcon extends View {
	public static int w;
	public static int h;
	public static Bitmap mBitmap;
	public Point point;

	public MapIcon(Context arg0) {
		super(arg0);
	}
	
	public MapIcon(Context context, int flag,Point point) {
		super(context);
		if (flag == 0) {
			mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.map_starting);
		} else {
			mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.map_end);
		}
		this.point = point;
	}
	
	public MapIcon(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap, point.x /2, point.y / 2, null);
	}
}
