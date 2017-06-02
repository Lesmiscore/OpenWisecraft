package com.nao20010128nao.Wisecraft.misc.pref;

import android.content.*;
import android.graphics.*;
import android.support.v7.preference.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class CheckBoxPreferenceCompat extends android.support.v7.preference.CheckBoxPreference implements SetTextColor{
	int color=Color.BLACK;
	
	public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
		color=PreferenceUtils.getDefaultPreferenceTextColor(context);
    }

    public CheckBoxPreferenceCompat(Context context) {
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
