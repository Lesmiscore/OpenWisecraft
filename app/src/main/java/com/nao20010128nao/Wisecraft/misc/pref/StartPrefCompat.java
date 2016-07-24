package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;
import com.nao20010128nao.ToolBox.HandledPreferenceCompat;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.SetTextColor;
import android.graphics.Color;

public class StartPrefCompat extends HandledPreferenceCompat implements SetTextColor{
	int color=Color.BLACK;
	
	public StartPrefCompat(Context c, AttributeSet attrs) {
		super(c, StartPref.as = attrs);
	}
	public StartPrefCompat(Context c) {
		super(c, StartPref.as);
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
