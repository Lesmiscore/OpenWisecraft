package com.nao20010128nao.TESTAPP;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class ScrollingActivity extends AppCompatActivity {
	FrameLayout content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getLayoutResId());
        Toolbar toolbar = (Toolbar) super.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		content=(FrameLayout)super.findViewById(R.id.content);
    }

	@Override
	public void setContentView(View view) {
		// TODO: Implement this method
		content.removeAllViews();
		ViewGroup.LayoutParams lp=view.getLayoutParams();
		if(lp==null)lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		lp.height=lp.width=ViewGroup.LayoutParams.MATCH_PARENT;
		view.setLayoutParams(lp);
		content.addView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO: Implement this method
		setContentView(getLayoutInflater().inflate(layoutResID,content,false));
	}

	@Override
	public View findViewById(int id) {
		// TODO: Implement this method
		return content.findViewById(id);
	}
	
	protected int getLayoutResId(){
		return R.layout.activity_scrolling;
	}
}
