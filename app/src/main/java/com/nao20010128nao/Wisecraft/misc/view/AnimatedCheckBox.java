package com.nao20010128nao.Wisecraft.misc.view;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.MotionEvent;

public class AnimatedCheckBox extends AppCompatCheckBox
{
	boolean initing=true;
	public AnimatedCheckBox(android.content.Context context) {
		super(context);
		initing=false;
	}

    public AnimatedCheckBox(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
		initing=false;
	}

    public AnimatedCheckBox(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
		initing=false;
	}

	/*
	@Override
	public void setChecked(boolean checked) {
		// TODO: Implement this method
		if(isChecked()==checked){
			return;
		}
		if(initing){
			super.setChecked(checked);
		}else{
			initing=true;
			performClick();
			initing=false;
		}
	}
	*/
}
