package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.support.v7.preference.PreferenceViewHolder;

public class EditTextPreferenceCompat extends android.support.v7.preference.EditTextPreference {
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
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
	}
}
