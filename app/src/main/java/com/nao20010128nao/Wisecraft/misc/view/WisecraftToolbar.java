package com.nao20010128nao.Wisecraft.misc.view;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;

import android.support.v7.widget.Toolbar;

// com.nao20010128nao.Wisecraft.misc.view.WisecraftToolbar
public class WisecraftToolbar extends Toolbar{
	public WisecraftToolbar(android.content.Context context) {
		super(context);
	}

    public WisecraftToolbar(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}

    public WisecraftToolbar(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		new Handler().post(new Runnable(){
				public void run(){
					TextView tv=Utils.getActionBarTextView(WisecraftToolbar.this);
					if(tv!=null){
						tv.setGravity(Gravity.CENTER);
					}
				}
			});
	}
}
