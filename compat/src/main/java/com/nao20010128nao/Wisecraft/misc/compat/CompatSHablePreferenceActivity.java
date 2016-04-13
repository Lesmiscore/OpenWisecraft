package com.nao20010128nao.Wisecraft.misc.compat;
import krsw.device.locker.AppCompatPreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.nao20010128nao.ToolBox.HandledPreference;

public class CompatSHablePreferenceActivity extends AppCompatPreferenceActivity
{
	protected void sH(Preference pref, HandledPreference.OnClickListener handler) {
		if (!(pref instanceof HandledPreference))return;
		((HandledPreference)pref).setOnClickListener(handler);
	}
	protected void sH(String pref, HandledPreference.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
}
