package com.nao20010128nao.Wisecraft.misc.view;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;

public class RepeatingImageView extends ExtendedImageView {
	public RepeatingImageView(Context context) {
		super(context);
	}

    public RepeatingImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}

    public RepeatingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		if(drawable instanceof BitmapDrawable){
			BitmapDrawable d = (BitmapDrawable)drawable;
			d.setTargetDensity(getResources().getDisplayMetrics());
			d.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		}
		super.setImageDrawable(drawable);
		super.setBackground(drawable);
	}

	@Override
	public void setBackgroundDrawable(Drawable background) {
		// TODO: Implement this method
		super.setImageDrawable(background);
	}

	@Override
	public void setBackground(Drawable background) {
		// TODO: Implement this method
		super.setImageDrawable(background);
	}
}
