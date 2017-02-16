package com.nao20010128nao.Wisecraft.misc.view;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.lang.reflect.*;
import java.util.*;

import android.support.v7.widget.Toolbar;

// com.nao20010128nao.Wisecraft.misc.view.WisecraftToolbar
public class WisecraftToolbar extends Toolbar{
	NavigationClickListenerDelegator ncld;
	
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
	
	public void setShowBackButton(boolean value){
		ActionBar bar=null;
		try {
			bar = (ActionBar)getContext().getClass().getDeclaredMethod("getSupportActionBar").invoke(getContext());
		} catch (IllegalArgumentException e) {
			
		} catch (NoSuchMethodException e) {
			
		} catch (SecurityException e) {
			
		} catch (InvocationTargetException e) {
			
		} catch (IllegalAccessException e) {
			
		}
		if(bar!=null){
			bar.setDisplayHomeAsUpEnabled(value);
			bar.setDisplayShowHomeEnabled(value);
		}
	}

	@Override
	public void setNavigationOnClickListener(View.OnClickListener listener) {
		if(ncld==null){
			ncld=new NavigationClickListenerDelegator();
			super.setNavigationOnClickListener(ncld);
		}
		ncld.add(listener);
	}
	
	
	private class NavigationClickListenerDelegator extends ArrayList<View.OnClickListener> implements View.OnClickListener {
		@Override
		public void onClick(View p1) {
			for(View.OnClickListener lis:this)
				lis.onClick(p1);
		}
	}
}
