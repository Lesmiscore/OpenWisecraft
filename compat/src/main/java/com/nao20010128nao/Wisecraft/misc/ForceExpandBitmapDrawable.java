package com.nao20010128nao.Wisecraft.misc;

import android.graphics.drawable.*;
import android.graphics.*;

public class ForceExpandBitmapDrawable extends Drawable 
{
	Bitmap bitmap;
	Paint paint=new Paint();
	
	public ForceExpandBitmapDrawable(){
		paint.setDither(true);
	}
	public ForceExpandBitmapDrawable(Bitmap bmp){
		this();
		bitmap=bmp;
	}
	
	@Override
	public int getOpacity() {
		return paint.getAlpha();
	}

	@Override
	public void draw(Canvas p1) {
		p1.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),getBounds(),paint);
	}

	@Override
	public void setAlpha(int p1) {
		paint.setAlpha(p1);
	}

	@Override
	public void setColorFilter(ColorFilter p1) {
		paint.setColorFilter(p1);
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		invalidateSelf();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setDither(boolean dither) {
		paint.setDither(dither);
	}

	public boolean isDither() {
		return paint.isDither();
	}
}
