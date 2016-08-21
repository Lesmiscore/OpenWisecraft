package com.nao20010128nao.Wisecraft.misc.pref;
import android.support.v7.preference.*;
import com.nao20010128nao.ToolBox.*;

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
