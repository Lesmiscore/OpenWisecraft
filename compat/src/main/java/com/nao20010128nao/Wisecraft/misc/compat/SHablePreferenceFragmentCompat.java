package com.nao20010128nao.Wisecraft.misc.compat;
import android.support.v7.preference.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.*;

public abstract class SHablePreferenceFragmentCompat extends ViewHolderCatchablePreferenceFragment
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
