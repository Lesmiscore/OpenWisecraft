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
    }

    public StartPrefCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    public StartPrefCompat(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
    }

    public StartPrefCompat(android.content.Context context) {
        super(context);
    }
	
	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		// TODO: Implement this method
		super.onBindViewHolder(holder);
		PreferenceUtils.onBindViewHolder(getContext(),this,holder);
	}

	@Override
	public void setTextColor(int color) {
		// TODO: Implement this method
		this.color=color;
	}

	@Override
	public int getTextColor() {
		// TODO: Implement this method
		return color;
	}	
}
