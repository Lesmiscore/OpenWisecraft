package com.nao20010128nao.TESTAPP;
import android.app.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;

public class ScrollingActivityDelegate
{
	Activity actv;Callback cb;
	AppCompatDelegate delegate;
	FrameLayout content;
	public ScrollingActivityDelegate(Activity mContext,AppCompatDelegate delegate,Callback cb){
		TypedArray a = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            a.recycle();
            throw new IllegalStateException(
				"You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
		actv=mContext;
		this.cb=cb;
		this.delegate=delegate;
	}
	
    protected void onCreate(Bundle savedInstanceState) {
        delegate.setContentView(cb.getLayoutResId());
        Toolbar toolbar = (Toolbar) actv.getWindow().findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);
		content=(FrameLayout)delegate.findViewById(R.id.content);
		View nativeContent=actv.findViewById(android.R.id.content);
		content.setId(android.R.id.content);
		nativeContent.setId(View.NO_ID);
    }
	
	public void setContentView(View view) {
		ViewGroup.LayoutParams lp=view.getLayoutParams();
		if(lp==null)lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		lp.height=lp.width=ViewGroup.LayoutParams.MATCH_PARENT;
		setContentView(view,lp);
	}

	public void setContentView(int layoutResID) {
		setContentView(actv.getLayoutInflater().inflate(layoutResID,content,false));
	}

	public void setContentView(View view, ViewGroup.LayoutParams params) {
		content.removeAllViews();
		view.setLayoutParams(params);
		content.addView(view);
	}
	
	public View findViewById(int id) {
		return content.findViewById(id);
	}
	
	public static interface Callback{
		int getLayoutResId();
	}
}
