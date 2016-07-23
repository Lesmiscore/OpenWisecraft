package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.*;
import android.support.v7.preference.*;
import android.util.*;
import android.widget.*;

public class CheckBoxPreferenceCompat extends android.support.v7.preference.CheckBoxPreference {
	public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxPreferenceCompat(Context context) {
        super(context);
    }
	
	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		// TODO: Implement this method
		super.onBindViewHolder(holder);
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(getContext(),R.style.TextAppearance_AppCompat_Medium);
	}
}
