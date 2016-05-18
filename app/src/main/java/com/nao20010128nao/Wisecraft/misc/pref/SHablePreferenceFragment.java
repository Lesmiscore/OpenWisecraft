package com.nao20010128nao.Wisecraft.misc.pref;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import com.nao20010128nao.ToolBox.HandledPreference;
import com.nao20010128nao.ToolBox.HandledPreferenceCompat;

public abstract class SHablePreferenceFragment extends PreferenceFragmentCompat
{
	protected void sH(Preference pref, HandledPreference.OnClickListener handler) {
		if (!(pref instanceof HandledPreferenceCompat))return;
		((HandledPreferenceCompat)pref).setOnClickListener(handler);
	}
	protected void sH(String pref, HandledPreference.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
}