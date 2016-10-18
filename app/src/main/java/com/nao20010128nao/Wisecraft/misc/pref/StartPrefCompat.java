package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.*;
import android.graphics.*;
import android.support.v7.preference.*;
import android.util.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class StartPrefCompat extends HandledPreferenceCompat implements SetTextColor{
	int color=Color.BLACK;
	
	public StartPrefCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr,defStyleRes);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public StartPrefCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public StartPrefCompat(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public StartPrefCompat(android.content.Context context) {
        super(context);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }
	
	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		super.onBindViewHolder(holder);
		PreferenceUtils.onBindViewHolder(getContext(),this,holder);
	}

	@Override
	public void setTextColor(int color) {
		this.color=color;
	}

	@Override
	public int getTextColor() {
		return color;
	}	
}
