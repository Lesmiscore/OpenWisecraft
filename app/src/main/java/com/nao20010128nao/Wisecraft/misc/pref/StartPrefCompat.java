package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.*;
import android.support.v7.preference.*;
import android.util.*;
import android.widget.*;
import com.nao20010128nao.ToolBox.*;

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
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(getContext(),R.style.TextAppearance_AppCompat_Medium);
		((TextView)holder.findViewById(android.R.id.title)).setTextColor(0xff_000000);
	}
}
