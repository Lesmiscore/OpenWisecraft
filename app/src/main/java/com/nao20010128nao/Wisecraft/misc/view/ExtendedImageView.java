package com.nao20010128nao.Wisecraft.misc.view;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ExtendedImageView extends AppCompatImageView
{
	public ExtendedImageView(Context context) {
		super(context);
	}

    public ExtendedImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}

    public ExtendedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}
	
	public void setColor(int color){
		setImageDrawable(new ColorDrawable(color));
	}
	
	public void setColorRes(int les){
		setColor(getResources().getColor(les));
	}
}
