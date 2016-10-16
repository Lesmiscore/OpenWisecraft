package com.nao20010128nao.Wisecraft.misc.view;

import android.content.*;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;

public class FullFitLayout extends ViewGroup 
{
	public FullFitLayout(Context context) {
        super(context);
    }

    public FullFitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullFitLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	@Override
	protected void onLayout(boolean u, int l, int t, int r, int b) {
		if(getChildCount()==0)return;
		View child=getChildAt(0);
		ViewGroup.LayoutParams lp=child.getLayoutParams();
		if(lp instanceof LayoutParams){
			LayoutParams margins=(LayoutParams)lp;
			child.layout(l+margins.leftMargin,t+margins.topMargin,r-margins.rightMargin,b-margins.bottomMargin);
		}else{
			child.layout(l,t,r,b);
		}
	}

	@Override
	public void addView(View child, int width, int height) {
		checkChildren();
		super.addView(child, width, height);
	}

	@Override
	public void addView(View child) {
		checkChildren();
		super.addView(child);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		checkChildren();
		super.addView(child, index, params);
	}

	@Override
	public void addView(View child, int index) {
		checkChildren();
		super.addView(child, index);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		checkChildren();
		super.addView(child, params);
	}
	
	private void checkChildren(){
		if(getChildCount()>1)throw new IllegalStateException("count of children cannot be more than 1");
	}

	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams{
		public LayoutParams(android.content.Context c, android.util.AttributeSet attrs) {
			super(c,attrs);
		}

        public LayoutParams(int width, int height) {
			super(width,height);
		}

        public LayoutParams(android.view.ViewGroup.MarginLayoutParams source) {
			super(source);
		}

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
