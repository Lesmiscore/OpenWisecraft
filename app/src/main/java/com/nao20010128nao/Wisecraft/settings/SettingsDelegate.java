package com.nao20010128nao.Wisecraft.settings;
import android.app.*;
import android.content.*;

import com.nao20010128nao.Wisecraft.activity.FragmentSettingsActivity;

public class SettingsDelegate extends ContextWrapper
{
	private SettingsDelegate(){super(null);}
	public static void openAppSettings(Activity a){
		a.startActivity(new Intent(a,FragmentSettingsActivity.class));
		return;
	}
}
