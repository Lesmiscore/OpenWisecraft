package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;
import com.nao20010128nao.ToolBox.HandledPreferenceCompat;
import com.nao20010128nao.Wisecraft.R;

public class StartPrefCompat extends HandledPreferenceCompat {
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
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(getContext(),R.style.AppPreferenceTextAppearance);
	}
}
