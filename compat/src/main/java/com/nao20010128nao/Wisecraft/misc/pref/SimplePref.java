package com.nao20010128nao.Wisecraft.misc.pref;

import android.support.v7.preference.*;

public class SimplePref extends Preference {
	public SimplePref(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
	}

	public SimplePref(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}

	public SimplePref(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}

	public SimplePref(android.content.Context context) {
		super(context);
	}
}
