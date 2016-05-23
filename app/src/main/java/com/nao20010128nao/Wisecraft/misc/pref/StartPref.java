package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.ToolBox.*;

public class StartPref extends HandledPreference {
	public static AttributeSet as;
	public StartPref(Context c, AttributeSet attrs) {
		super(c, as = attrs);
	}
	public StartPref(Context c) {
		super(c, as);
	}

	@Override
	protected void onBindView(View view) {
		// TODO: Implement this method
		super.onBindView(view);
		((TextView)view.findViewById(android.R.id.title)).setSingleLine(false);
	}
}
