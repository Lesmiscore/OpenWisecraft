package com.nao20010128nao.Wisecraft.misc.compat;
import android.preference.Preference;
import com.nao20010128nao.ToolBox.HandledPreference;
import krsw.device.locker.AppCompatPreferenceActivity;

public class CompatSHablePreferenceActivity extends AppCompatPreferenceActivity
{
	public void sH(Preference pref, HandledPreference.OnClickListener handler) {
		if (!(pref instanceof HandledPreference))return;
		((HandledPreference)pref).setOnClickListener(handler);
	}
	public void sH(String pref, HandledPreference.OnClickListener handler) {
		sH(findPreference(pref), handler);
	}
}
