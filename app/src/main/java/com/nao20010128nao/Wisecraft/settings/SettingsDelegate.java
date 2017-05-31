package com.nao20010128nao.Wisecraft.settings;
import android.app.*;
import android.content.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;

public class SettingsDelegate{
	public static void openAppSettings(Activity a){
		if(a.getResources().getBoolean(R.bool.twoPane)){
			a.startActivity(new Intent(a,FragmentSettingsActivity.MasterDetailSettings.class));
		}else{
			a.startActivity(new Intent(a,FragmentSettingsActivity.class));
		}
	}
}
