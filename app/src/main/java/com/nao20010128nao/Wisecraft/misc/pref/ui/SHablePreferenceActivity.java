package com.nao20010128nao.Wisecraft.misc.pref.ui;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.nao20010128nao.ToolBox.HandledPreference;

public abstract class SHablePreferenceActivity extends PreferenceActivity {
	public void sH(Preference pref, HandledPreference.OnClickListener handler) {
		if (!(pref instanceof HandledPreference))return;
		((HandledPreference)pref).setOnClickListener(handler);
	}
	public void sH(String pref, HandledPreference.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
}
