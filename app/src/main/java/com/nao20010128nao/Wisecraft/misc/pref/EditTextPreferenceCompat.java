package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.*;
import android.graphics.*;
import android.support.v7.preference.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class EditTextPreferenceCompat extends android.support.v7.preference.EditTextPreference implements SetTextColor{
	int color=Color.BLACK;
	
	public EditTextPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EditTextPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreferenceCompat(Context context) {
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
