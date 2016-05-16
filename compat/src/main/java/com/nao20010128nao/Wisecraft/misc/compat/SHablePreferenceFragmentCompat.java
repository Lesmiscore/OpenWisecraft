package com.nao20010128nao.Wisecraft.misc.compat;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.nao20010128nao.ToolBox.HandledPreferenceCompat;
import android.support.v7.preference.Preference;
import com.nao20010128nao.ToolBox.HandledPreference;

public abstract class SHablePreferenceFragmentCompat extends PreferenceFragmentCompat
{
	protected void sH(Preference pref, HandledPreferenceCompat.OnClickListener handler) {
		if (!(pref instanceof HandledPreferenceCompat))return;
		((HandledPreferenceCompat)pref).setOnClickListener(handler);
	}
	protected void sH(String pref, HandledPreferenceCompat.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
	protected void sH(Preference pref, HandledPreference.OnClickListener handler) {
		if (!(pref instanceof HandledPreferenceCompat))return;
		((HandledPreferenceCompat)pref).setOnClickListener(handler);
	}
	protected void sH(String pref, HandledPreference.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
}
